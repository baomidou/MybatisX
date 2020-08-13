package com.baomidou.plugin.idea.mybatisx.contributor;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.CommentAnnotationMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.JpaAnnotationMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.SmartJpaCompletionProvider;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
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

import java.util.Optional;

/**
 * mapper的jpa方法提示
 */
public class MapperMethodCompletionContributor extends CompletionContributor {

    public static final Key<Boolean> FOUND = Key.create("mapper.finder");

    public static final Key<PsiClass> MAPPER = Key.create("mapper.finder");

    /**
     * 填充变量
     * <p>
     * 这一层做验证
     *
     * @param parameters
     * @param result
     */
    @Override
    public void fillCompletionVariants(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        super.fillCompletionVariants(parameters, result);
        Editor editor = parameters.getEditor();

        Boolean found = editor.getUserData(FOUND);
        if (found != null && !found) {
            return;
        }
        PsiClass mapperClass = editor.getUserData(MAPPER);
        if (mapperClass == null) {
            Optional<PsiClass> mapperClassOptional = findMapperClass(parameters);
            if (mapperClassOptional.isPresent()) {
                mapperClass = mapperClassOptional.get();
                editor.putUserData(MAPPER, mapperClass);
                editor.putUserData(FOUND, true);
            }else{
                editor.putUserData(FOUND, false);
                return;
            }

        }

        logger.info("MapperMethodCompletionContributor.fillCompletionVariants start");

        try {
            SmartJpaCompletionProvider smartJpaCompletionProvider = new SmartJpaCompletionProvider();
            smartJpaCompletionProvider.addCompletion(parameters, result, mapperClass);
        } catch (Throwable e) {
            logger.error("自动提示异常", e);
        }

        logger.info("MapperMethodCompletionContributor.fillCompletionVariants end");

    }

    @NotNull
    protected Optional<PsiClass> findMapperClass(@NotNull CompletionParameters parameters) {
        if (parameters.getCompletionType() != CompletionType.BASIC) {
            logger.info("类型不是 BASIC");
            return Optional.empty();
        }
        if (inCommentOrLiteral(parameters)) {
            logger.info("注释区间不提示");
            return Optional.empty();
        }
        // 验证当前类必须是接口
        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiMethod currentMethod = PsiTreeUtil.getParentOfType(originalPosition, PsiMethod.class);
        if (currentMethod != null) {
            logger.info("当前位置在方法体内部, 不提示");
            return Optional.empty();
        }

        PsiClass mapperClass = PsiTreeUtil.getParentOfType(originalPosition, PsiClass.class);
        if (mapperClass == null || !mapperClass.isInterface()) {
            logger.info("当前类不是接口, 不提示");
            return Optional.empty();
        }

        Optional<PsiClass> entityClassByMapperClass = JpaAnnotationMappingResolver.findEntityClassByMapperClass(mapperClass);
        if (entityClassByMapperClass.isPresent()) {
            return entityClassByMapperClass;
        }
        Optional<Mapper> firstMapper = MapperUtils.findFirstMapper(mapperClass.getProject(), mapperClass);
        if (!firstMapper.isPresent()) {
            logger.info("当前类不是mapper接口, 不提示");
            // 支持 mapper 接口上面写 @Entity 注解
            PsiDocComment docComment = mapperClass.getDocComment();
            if (docComment != null && docComment.findTagByName(CommentAnnotationMappingResolver.TABLE_ENTITY) == null) {
                return Optional.empty();
            }
        }
        return Optional.ofNullable(mapperClass);
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
