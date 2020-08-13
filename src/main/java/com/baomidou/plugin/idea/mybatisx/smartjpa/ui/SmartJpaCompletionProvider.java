package com.baomidou.plugin.idea.mybatisx.smartjpa.ui;

import a.h.B;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.CompositeManagerAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.baomidou.plugin.idea.mybatisx.util.Icons;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.JavaCompletionSorting;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SmartJpaCompletionProvider {

    private static final Logger logger = LoggerFactory.getLogger(SmartJpaCompletionProvider.class);


    public void addCompletion(@NotNull final CompletionParameters parameters,
                              @NotNull final CompletionResultSet result, PsiClass mapperClass) {
        PsiElement originalPosition = parameters.getOriginalPosition();
        assert originalPosition != null;


        String prefix = CompletionUtil.findJavaIdentifierPrefix(parameters);
        // 按照 mybatisplus3 > mybatisplus2 > resultMap 的顺序查找映射关系
        final Optional<AreaOperateManager> appenderManagerOptional = getAreaOperateManager(mapperClass, parameters.getEditor());
        if (!appenderManagerOptional.isPresent()) {
            logger.info("not support, prefix: {} ", prefix);
            return;
        }
        AreaOperateManager areaOperateManager = appenderManagerOptional.get();
        // 添加排序
        CompletionResultSet completionResultSet = JavaCompletionSorting.addJavaSorting(parameters, result);

        logger.info("tip prefix:{} ", prefix);
        final LinkedList<SyntaxAppender> splitList = areaOperateManager.splitAppenderByText(prefix);
        logger.info("split completion ");
        if (splitList.size() > 0) {
            final SyntaxAppender last = splitList.getLast();
            last.pollLast(splitList);
        }
        // 将语句划分为可以划分的字符串
        final String splitString = splitList.stream().map(x -> x.getText()).collect(Collectors.joining());

        logger.info("split join :{} ", splitString);
        // 获得一个完成结果集,  可能是原先的也可能是新的
        completionResultSet = this.getCompletionResultSet(completionResultSet, prefix, splitString);
        logger.info("completion result success");
        // 获得提示列表
        List<String> appendList = null;
        if (splitList.size() > 0) {
            appendList = areaOperateManager.getCompletionContent(splitList);
        } else {
            appendList = areaOperateManager.getCompletionContent();
        }
        // 自动提示
        SmartJpaCompletionInsertHandler daoCompletionInsertHandler =
            new SmartJpaCompletionInsertHandler(parameters.getEditor(), parameters.getEditor().getProject());
        // 通用字段
        List<LookupElement> lookupElementList = appendList.stream()
            .map(x -> buildLookupElement(x, daoCompletionInsertHandler))
            .collect(Collectors.toList());
        // 添加到提示
        completionResultSet.addAllElements(lookupElementList);


    }

    private Key<Boolean> FOUND_OPERATOR_MANAGER = Key.create("FOUND_OPERATOR_MANAGER");
    private Key<AreaOperateManager> OPERATOR_MANAGER = Key.create("OPERATOR_MANAGER");

    /**
     * 在editor级别做初始化字段的缓存
     * @param mapperClass
     * @param editor
     * @return
     */
    @Nullable
    private Optional<AreaOperateManager> getAreaOperateManager(PsiClass mapperClass, @NotNull Editor editor) {
        Boolean foundAreaOperateManager = editor.getUserData(FOUND_OPERATOR_MANAGER);
        if (foundAreaOperateManager != null && !foundAreaOperateManager) {
            return Optional.empty();
        }
        AreaOperateManager areaOperateManager = null;
        EntityMappingResolverFactory entityMappingResolverFactory = new EntityMappingResolverFactory(editor.getProject(), mapperClass);
        PsiClass entityClass = entityMappingResolverFactory.searchEntity();
        EntityMappingResolver mybatisPlus3MappingResolver = entityMappingResolverFactory.getEntityMappingResolver();
        if (mybatisPlus3MappingResolver == null) {
            foundAreaOperateManager = false;
            editor.putUserData(FOUND_OPERATOR_MANAGER, foundAreaOperateManager);
            return Optional.empty();
        }
        // 第一次初始化
        List<TxField> mappingField = mybatisPlus3MappingResolver.getFields();
        areaOperateManager = new CompositeManagerAdaptor(mappingField, entityClass);
        foundAreaOperateManager = true;

        editor.putUserData(FOUND_OPERATOR_MANAGER, foundAreaOperateManager);
        editor.putUserData(OPERATOR_MANAGER, areaOperateManager);

        return Optional.ofNullable(areaOperateManager);
    }


    @NotNull
    private CompletionResultSet getCompletionResultSet(@NotNull final CompletionResultSet resultSet,
                                                       final String prefix,
                                                       final String splitString) {
        CompletionResultSet completionResultSet = resultSet;
        if (prefix.length() >= splitString.length()) {
            final String newFragmentPrefix = prefix.substring(splitString.length());
            completionResultSet = completionResultSet.withPrefixMatcher(newFragmentPrefix);
            logger.info("getCompletionResultSet changed prefix: {}", completionResultSet.getPrefixMatcher().getPrefix());
        }
        return completionResultSet;
    }


    private LookupElement buildLookupElement(final String str,
                                             InsertHandler<LookupElement> insertHandler) {
        return LookupElementBuilder.create(str)
            .withIcon(Icons.MAPPER_LINE_MARKER_ICON)
            .bold()
            .withInsertHandler(insertHandler);

    }

}
