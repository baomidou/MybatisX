package com.baomidou.plugin.idea.mybatisx.service;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.JavaUtils;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiImportList;
import com.intellij.psi.PsiImportStatement;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiType;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.CommonProcessors;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtPsiUtil;

import java.util.Collection;
import java.util.Optional;

/**
 * The type Java service.
 *
 * @author yanglin
 */
public class JavaService {
    private Project project;

    private JavaPsiFacade javaPsiFacade;

    private EditorService editorService;

    /**
     * Instantiates a new Java service.
     *
     * @param project the project
     */
    public JavaService(Project project) {
        this.project = project;
        this.javaPsiFacade = JavaPsiFacade.getInstance(project);
        this.editorService = EditorService.getInstance(project);
    }

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    public static JavaService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, JavaService.class);
    }

    /**
     * Gets reference clazz of psi field.
     *
     * @param field the field
     * @return the reference clazz of psi field
     */
    public Optional<PsiClass> getReferenceClazzOfPsiField(@NotNull PsiElement field) {
        if (!(field instanceof PsiField)) {
            return Optional.empty();
        }
        PsiType type = ((PsiField) field).getType();
        return type instanceof PsiClassReferenceType ? Optional.ofNullable(((PsiClassReferenceType) type).resolve()) :
            Optional.empty();
    }

    /**
     * Find statement optional.
     *
     * @param method the method
     * @return the optional
     */
    public Optional<DomElement> findStatement(@Nullable PsiMethod method) {
        CommonProcessors.FindFirstProcessor<IdDomElement> processor = new CommonProcessors.FindFirstProcessor<>();
        processMethod(method, processor);
        return processor.isFound() ? Optional.ofNullable(processor.getFoundValue()) : Optional.empty();
    }

    /**
     * Process.
     *
     * @param psiMethod the psi method
     * @param processor the processor
     */
    public void processMethod(@NotNull PsiMethod psiMethod, @NotNull Processor<IdDomElement> processor) {
        PsiClass psiClass = psiMethod.getContainingClass();
        if (null == psiClass) {
            return;
        }
        String id = psiClass.getQualifiedName() + "." + psiMethod.getName();
        Collection<Mapper> mappers = MapperUtils.findMappers(psiMethod.getProject());

        mappers.stream()
            .flatMap(mapper -> mapper.getDaoElements().stream())
            .filter(idDom -> MapperUtils.getIdSignature(idDom).equals(id))
            .forEach(processor::process);

    }


    /**
     * Process.
     *
     * @param clazz     the clazz
     * @param processor the processor
     */
    @SuppressWarnings("unchecked")
    public void processClass(@NotNull PsiClass clazz, @NotNull Processor<Mapper> processor) {
        String ns = clazz.getQualifiedName();
        for (Mapper mapper : MapperUtils.findMappers(clazz.getProject())) {
            if (MapperUtils.getNamespace(mapper).equals(ns)) {
                processor.process(mapper);
            }
        }
    }

    /**
     * Process Kotlin Class
     * @param clazz     the ktclass
     * @param processor the processor
     */
    public void processKotlinClass(@NotNull KtClass clazz, @NotNull Processor<Mapper> processor) {
        String ns = clazz.getName();
        String packageName = KtPsiUtil.getPackageName(clazz);
        ns = packageName + "." + ns;
        for (Mapper mapper : MapperUtils.findMappers(clazz.getProject())) {
            if (MapperUtils.getNamespace(mapper).equals(ns)) {
                processor.process(mapper);
            }
        }
    }

    /**
     * process KtNamedFunction
     * @param ktMethod  the kotlin method
     * @param processor the processor
     */
    public void processKotlinMethod(@NotNull KtNamedFunction ktMethod, @NotNull Processor<IdDomElement> processor) {
        KtClass parentClass = PsiTreeUtil.getParentOfType(ktMethod, KtClass.class);
        if (null == parentClass || !KtPsiUtil.isTrait(parentClass)) {
            return;
        }
        String packageName = KtPsiUtil.getPackageName(parentClass);
        String id = packageName + "." + parentClass.getName() + "." + ktMethod.getName();
        Collection<Mapper> mappers = MapperUtils.findMappers(ktMethod.getProject());

        mappers.stream()
            .flatMap(mapper -> mapper.getDaoElements().stream())
            .filter(idDom -> MapperUtils.getIdSignature(idDom).equals(id))
            .forEach(processor::process);
    }

    /**
     * Find with find first processor optional.
     *
     * @param target the target
     * @return the optional
     */
    public Optional<Mapper> findWithFindFirstProcessor(@NotNull PsiClass target) {
        CommonProcessors.FindFirstProcessor<Mapper> processor = new CommonProcessors.FindFirstProcessor<>();
        processClass(target, processor);
        return Optional.ofNullable(processor.getFoundValue());
    }

    /**
     * Import clazz.
     *
     * @param file      the file
     * @param clazzName the clazz name
     */
    public void importClazz(PsiJavaFile file, String clazzName) {
        if (!JavaUtils.hasImportClazz(file, clazzName)) {
            Optional<PsiClass> clazz = JavaUtils.findClazz(project, clazzName);
            PsiImportList importList = file.getImportList();
            if (clazz.isPresent() && null != importList) {
                PsiElementFactory elementFactory = javaPsiFacade.getElementFactory();
                PsiImportStatement statement = elementFactory.createImportStatement(clazz.get());
                importList.add(statement);
                editorService.format(file, statement);
            }
        }
    }
}

