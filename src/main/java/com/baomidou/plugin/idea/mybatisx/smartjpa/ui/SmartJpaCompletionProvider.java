package com.baomidou.plugin.idea.mybatisx.smartjpa.ui;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.TableMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.CompositeManagerAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.MapperSearch;
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
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class SmartJpaCompletionProvider {

    private static final Logger logger = LoggerFactory.getLogger(SmartJpaCompletionProvider.class);


    public void addCompletion(@NotNull final CompletionParameters parameters,
                              @NotNull final CompletionResultSet result) {
        logger.info("DaoCompletionProvider.addCompletions, result: {},hashCode:{}", result, parameters.hashCode());
        PsiElement originalPosition = parameters.getOriginalPosition();
        assert originalPosition != null;
        Project project = originalPosition.getProject();
        PsiClass mapperClass = PsiTreeUtil.getParentOfType(originalPosition, PsiClass.class);

        Editor editor = parameters.getEditor();

        String prefix = CompletionUtil.findJavaIdentifierPrefix(parameters);


        // 添加排序
        CompletionResultSet completionResultSet = JavaCompletionSorting.addJavaSorting(parameters, result);

        MapperSearch mapperSearch = new MapperSearch();

        assert mapperClass != null;
        PsiClass entityClass = mapperSearch.searchEntity(project, mapperClass);
        TableMappingResolver tableMappingResolver = new TableMappingResolver(entityClass);
        List<TxField> mappingField = tableMappingResolver.getFields();

        final AreaOperateManager appenderManager = new CompositeManagerAdaptor(mappingField, entityClass);

        logger.info("提示前缀:{} ", prefix);
        final LinkedList<SyntaxAppender> splitList = appenderManager.splitAppenderByText(prefix);
        if (splitList.size() > 0) {
            final SyntaxAppender last = splitList.getLast();
            last.pollLast(splitList);
        }
        // 将语句划分为可以划分的字符串
        final String splitString = splitList.stream().map(x -> x.getText()).collect(Collectors.joining());

        // 获得一个完成结果集,  可能是原先的也可能是新的
        completionResultSet = this.getCompletionResultSet(completionResultSet, prefix, splitString);

        // 获得提示列表
        List<String> appendList = null;
        if (splitList.size() > 0) {
            appendList = appenderManager.getCompletionContent(splitList);
        } else {
            appendList = appenderManager.getCompletionContent();
        }
        // 自动提示
        SmartJpaCompletionInsertHandler daoCompletionInsertHandler =
            new SmartJpaCompletionInsertHandler(editor, project, parameters);
        // 通用字段
        List<LookupElement> lookupElementList = appendList.stream()
            .map(x -> buildLookupElement(x, daoCompletionInsertHandler))
            .collect(Collectors.toList());
        // 添加到提示
        completionResultSet.addAllElements(lookupElementList);

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
