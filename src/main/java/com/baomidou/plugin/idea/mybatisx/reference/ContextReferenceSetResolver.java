package com.baomidou.plugin.idea.mybatisx.reference;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.baomidou.plugin.idea.mybatisx.util.MybatisConstants;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author yanglin
 */
public abstract class ContextReferenceSetResolver<F extends PsiElement, K extends PsiElement> {

    private static final Splitter SPLITTER = Splitter.on(MybatisConstants.DOT_SEPARATOR);

    protected Project project;

    protected F element;

    protected List<String> texts;

    protected ContextReferenceSetResolver(@NotNull F element) {
        this.element = element;
        this.project = element.getProject();
        this.texts = Lists.newArrayList(SPLITTER.split(getText()));
    }

    @NotNull
    public final Optional<? extends PsiElement> resolve(int index) {
        Optional<K> startElement = getStartElement();
        return startElement.isPresent() ? (texts.size() > 1 ? parseNext(startElement, texts, index) : startElement) : Optional.<PsiElement>absent();
    }

    private Optional<K> parseNext(Optional<K> current, List<String> texts, int index) {
        int ind = 1;
        while (current.isPresent() && ind <= index) {
            String text = texts.get(ind);
            if (text.contains(" ")) {
                return Optional.absent();
            }
            current = resolve(current.get(), text);
            ind++;
        }
        return current;
    }

    public Optional<K> getStartElement() {
        return getStartElement(Iterables.getFirst(texts, null));
    }

    @NotNull
    public abstract Optional<K> getStartElement(@Nullable String firstText);

    @NotNull
    public abstract String getText();

    @NotNull
    public abstract Optional<K> resolve(@NotNull K current, @NotNull String text);

    public F getElement() {
        return element;
    }

    public void setElement(F element) {
        this.element = element;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
