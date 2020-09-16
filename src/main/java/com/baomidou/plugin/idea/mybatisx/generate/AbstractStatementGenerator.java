package com.baomidou.plugin.idea.mybatisx.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.GroupTwo;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.service.EditorService;
import com.baomidou.plugin.idea.mybatisx.service.JavaService;
import com.baomidou.plugin.idea.mybatisx.setting.MybatisSetting;
import com.baomidou.plugin.idea.mybatisx.ui.ListSelectionListener;
import com.baomidou.plugin.idea.mybatisx.ui.UiComponentFacade;
import com.baomidou.plugin.idea.mybatisx.util.CollectionUtils;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupStep;
import com.intellij.openapi.ui.popup.util.BaseListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiPrimitiveType;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.CommonProcessors.CollectProcessor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * <p>
 * 抽象 Statement 代码生成器
 * </p>
 *
 * @author jobob
 * @since 2018-07-30
 */
public abstract class AbstractStatementGenerator {

    public static final AbstractStatementGenerator UPDATE_GENERATOR = new UpdateGenerator("update", "modify", "set");

    public static final AbstractStatementGenerator SELECT_GENERATOR = new SelectGenerator("select", "get", "look", "find", "list", "search", "count", "query");

    public static final AbstractStatementGenerator DELETE_GENERATOR = new DeleteGenerator("del", "cancel");

    public static final AbstractStatementGenerator INSERT_GENERATOR = new InsertGenerator("insert", "add", "new");

    public static final Set<AbstractStatementGenerator> ALL = ImmutableSet.of(UPDATE_GENERATOR, SELECT_GENERATOR, DELETE_GENERATOR, INSERT_GENERATOR);

    private static final Function<Mapper, String> FUN = new Function<Mapper, String>() {
        @Override
        public String apply(Mapper mapper) {
            VirtualFile vf = mapper.getXmlTag().getContainingFile().getVirtualFile();
            if (null == vf) return "";
            return vf.getCanonicalPath();
        }
    };

    /**
     * 获取方法的返回类型
     *
     * @param method
     * @return
     */
    public static Optional<PsiClass> getSelectResultType(@Nullable PsiMethod method) {
        if (null == method) {
            return Optional.empty();
        }
        PsiType returnType = method.getReturnType();
        // 是基本类型, 并且不是 void
        if (returnType instanceof PsiPrimitiveType && !PsiType.VOID.equals(returnType)) {
            return JavaUtils.findClazz(method.getProject(), Objects.requireNonNull(((PsiPrimitiveType) returnType).getBoxedTypeName()));
        } else if (returnType instanceof PsiClassReferenceType) {
            PsiClassReferenceType type = (PsiClassReferenceType) returnType;
            if (type.hasParameters()) {
                PsiType[] parameters = type.getParameters();
                // 处理是 List 的返回结果, 将List<T> 的泛型拿出来
                if (parameters.length == 1) {
                    PsiType parameter = parameters[0];
                    // 通常情况 List<?> 这里一定是引用类型, 但是进入这里的来源还有检查方法,
                    // 不仅仅只是生成xml,  所以这里加一个判断.
                    if (parameter instanceof PsiClassReferenceType) {
                        type = (PsiClassReferenceType) parameter;
                    }
                }
            }
            return Optional.ofNullable(type.resolve());
        }
        return Optional.empty();
    }

    public static void applyGenerate(@Nullable final PsiMethod method) {
        if (null == method) {
            return;
        }
        final Project project = method.getProject();
        final AbstractStatementGenerator[] generators = getGenerators(method);
        if (1 == generators.length) {
            generators[0].execute(method);
        } else {
            BaseListPopupStep<AbstractStatementGenerator> step = new BaseListPopupStep<AbstractStatementGenerator>("[ Statement type for method: " + method.getName() + "]", generators) {
                @Override
                public PopupStep onChosen(AbstractStatementGenerator selectedValue, boolean finalChoice) {
                    return this.doFinalStep(new Runnable() {
                        @Override
                        public void run() {
                            WriteCommandAction.writeCommandAction(project).run(() -> selectedValue.execute(method));
                        }
                    });
                }
            };
            JBPopupFactory.getInstance().createListPopup(step).showInFocusCenter();
        }
    }

    @NotNull
    public static AbstractStatementGenerator[] getGenerators(@NotNull PsiMethod method) {
        GenerateModel model = MybatisSetting.getInstance().getStatementGenerateModel();
        String target = method.getName();
        List<AbstractStatementGenerator> result = Lists.newArrayList();
        for (AbstractStatementGenerator generator : ALL) {
            if (model.matchesAny(generator.getPatterns(), target)) {
                result.add(generator);
            }
        }
        return CollectionUtils.isNotEmpty(result) ? result.toArray(new AbstractStatementGenerator[result.size()]) : ALL.toArray(new AbstractStatementGenerator[ALL.size()]);
    }

    private Set<String> patterns;

    public AbstractStatementGenerator(@NotNull String... patterns) {
        this.patterns = Sets.newHashSet(patterns);
    }

    public void execute(@NotNull final PsiMethod method) {
        PsiClass psiClass = method.getContainingClass();
        if (null == psiClass) {
            return;
        }
        CollectProcessor<Mapper> processor = new CollectProcessor<>();
        JavaService.getInstance(method.getProject()).process(psiClass, processor);
        final List<Mapper> mappers = Lists.newArrayList(processor.getResults());
        if (1 == mappers.size()) {
            setupTag(method, (Mapper) Iterables.getOnlyElement(mappers, (Object) null));
        } else if (mappers.size() > 1) {
            Collection<String> paths = Collections2.transform(mappers, FUN);
            UiComponentFacade.getInstance(method.getProject()).showListPopup("Choose target mapper xml to generate", new ListSelectionListener() {
                @Override
                public void selected(int index) {
                    setupTag(method, mappers.get(index));
                }

                @Override
                public boolean isWriteAction() {
                    return true;
                }
            }, paths.toArray(new String[0]));
        }
    }

    private void setupTag(PsiMethod method, Mapper mapper) {
        GroupTwo target = getTarget(mapper, method);
        target.getId().setStringValue(method.getName());
        target.setValue(" ");
        XmlTag tag = target.getXmlTag();
        assert tag != null;
        int offset = tag.getTextOffset() + tag.getTextLength() - tag.getName().length() + 1;
        EditorService editorService = EditorService.getInstance(method.getProject());
        editorService.format(tag.getContainingFile(), tag);
        editorService.scrollTo(tag, offset);
    }

    @Override
    public String toString() {
        return this.getDisplayText();
    }

    @NotNull
    protected abstract GroupTwo getTarget(@NotNull Mapper mapper, @NotNull PsiMethod method);

    @NotNull
    public abstract String getId();

    @NotNull
    public abstract String getDisplayText();

    public Set<String> getPatterns() {
        return patterns;
    }

    public void setPatterns(Set<String> patterns) {
        this.patterns = patterns;
    }

}
