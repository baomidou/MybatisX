package com.baomidou.plugin.idea.mybatisx.contributor;

import a.h.K;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.CommentAnnotationMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.JpaAnnotationMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.SmartJpaCompletionProvider;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.google.common.base.Optional;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.util.Key;
import com.intellij.psi.CustomHighlighterTokenType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mapper的jpa方法提示
 */
public class MapperMethodCompletionContributor extends CompletionContributor {

    public static final @NotNull Key<Optional<PsiClass>> KEY = Key.create("mapper.finder");

    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        Editor editor = parameters.getEditor();
        PsiClass mapperClass = null;
        Optional<PsiClass> mapperClassOption = editor.getUserData(KEY);
        if (mapperClassOption!=null && !mapperClassOption.isPresent()) {
            return;
        }
        if (mapperClassOption == null) {
            mapperClass = findMapperClass(parameters);
            if (mapperClass == null) {
                editor.putUserData(KEY, Optional.absent());
                return;
            }
            mapperClassOption = Optional.of(mapperClass);
            editor.putUserData(KEY, mapperClassOption);
        }
        mapperClass = mapperClassOption.get();


        logger.info("MapperMethodCompletionContributor.fillCompletionVariants start");

        try {
            SmartJpaCompletionProvider smartJpaCompletionProvider = new SmartJpaCompletionProvider();
            smartJpaCompletionProvider.addCompletion(parameters, result, mapperClass);
        } catch (Throwable e) {
            logger.error("自动提示异常", e);
        }

        logger.info("MapperMethodCompletionContributor.fillCompletionVariants end");

    }

    @Nullable
    protected PsiClass findMapperClass(@NotNull CompletionParameters parameters) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            logger.info("类型不是 BASIC");
            return null;
        }
        if (inCommentOrLiteral(parameters)) {
            logger.info("注释区间不提示");
            return null;
        }
        // 验证当前类必须是接口
        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiMethod currentMethod = PsiTreeUtil.getParentOfType(originalPosition, PsiMethod.class);
        if (currentMethod != null) {
            logger.info("当前位置在方法体内部, 不提示");
            return null;
        }

        PsiClass mapperClass = PsiTreeUtil.getParentOfType(originalPosition, PsiClass.class);
        if (mapperClass == null || !mapperClass.isInterface()) {
            logger.info("当前类不是接口, 不提示");
            return null;
        }

        boolean found = false;
        java.util.Optional<PsiClass> entityClassByMapperClass = JpaAnnotationMappingResolver.findEntityClassByMapperClass(mapperClass);
        if (entityClassByMapperClass.isPresent()) {
            found = true;
        }
        if (!found) {
            Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(mapperClass.getProject(), mapperClass);
            if (!firstMapper.isPresent()) {
                logger.info("当前类不是mapper接口, 不提示");
                // 支持 mapper 接口上面写 @Entity 注解
                PsiDocComment docComment = mapperClass.getDocComment();
                if (docComment != null && docComment.findTagByName(CommentAnnotationMappingResolver.TABLE_ENTITY) == null) {
                    return null;
                }
            }
        }
        return mapperClass;
    }

    private static boolean inCommentOrLiteral(CompletionParameters parameters) {
        HighlighterIterator iterator = ((EditorEx) parameters.getEditor()).getHighlighter().createIterator(parameters.getOffset());
        if (iterator.atEnd()) return false;

        IElementType elementType = iterator.getTokenType();
        if (elementType == CustomHighlighterTokenType.WHITESPACE) {
            iterator.retreat();
            elementType = iterator.getTokenType();
        }
        return elementType == CustomHighlighterTokenType.LINE_COMMENT ||
            elementType == CustomHighlighterTokenType.MULTI_LINE_COMMENT ||
            elementType == CustomHighlighterTokenType.STRING ||
            elementType == CustomHighlighterTokenType.SINGLE_QUOTED_STRING;
    }

    private static final Logger logger = LoggerFactory.getLogger(MapperMethodCompletionContributor.class);


}
