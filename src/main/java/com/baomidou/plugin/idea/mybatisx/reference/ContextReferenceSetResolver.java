package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.util.MybatisConstants;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * The type Context reference set resolver.
 *
 * @param <F> the type parameter
 * @param <K> the type parameter
 * @author yanglin
 */
public abstract class ContextReferenceSetResolver<F extends PsiElement, K extends PsiElement> {

    private static final Splitter SPLITTER = Splitter.on(MybatisConstants.DOT_SEPARATOR);

    /**
     * The Project.
     */
    protected Project project;

    /**
     * The Element.
     */
    protected F element;

    /**
     * The Texts.
     */
    protected List<String> texts;

    /**
     * Instantiates a new Context reference set resolver.
     *
     * @param element the element
     */
    protected ContextReferenceSetResolver(@NotNull F element) {
        this.element = element;
        this.project = element.getProject();
        this.texts = Lists.newArrayList(SPLITTER.split(getText()));
    }

    /**
     * Resolve optional.
     *
     * @param index the index
     * @return the optional
     */
    public final Optional<K> resolve(int index) {
        Optional<K> startElement = getStartElement();
        return startElement.isPresent() ? (texts.size() > 1 ? parseNext(startElement, texts, index) : startElement) : Optional.empty();
    }

    private Optional<K> parseNext(Optional<K> current, List<String> texts, int index) {
        int ind = 1;
        while (current.isPresent() && ind <= index) {
            String text = texts.get(ind);
            if (text.contains(" ")) {
                return Optional.empty();
            }
            current = resolve(current.get(), text);
            ind++;
        }
        return current;
    }

    /**
     * Gets start element.
     *
     * @return the start element
     */
    public Optional<K> getStartElement() {
        return getStartElement(Iterables.getFirst(texts, null));
    }

    /**
     * Gets start element.
     *
     * @param firstText the first text
     * @return the start element
     */
    @NotNull
    public abstract Optional<K> getStartElement(@Nullable String firstText);

    /**
     * Gets text.
     *
     * @return the text
     */
    @NotNull
    public abstract String getText();

    /**
     * Resolve optional.
     *
     * @param current the current
     * @param text    the text
     * @return the optional
     */
    @NotNull
    public Optional<K> resolve(K current, String text){
        return Optional.empty();
    }

    /**
     * Gets element.
     *
     * @return the element
     */
    public F getElement() {
        return element;
    }

    /**
     * Sets element.
     *
     * @param element the element
     */
    public void setElement(F element) {
        this.element = element;
    }

    /**
     * Gets project.
     *
     * @return the project
     */
    public Project getProject() {
        return project;
    }

    /**
     * Sets project.
     *
     * @param project the project
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
