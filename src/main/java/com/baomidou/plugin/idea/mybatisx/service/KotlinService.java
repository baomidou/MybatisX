package com.baomidou.plugin.idea.mybatisx.service;

import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtClass;
import org.jetbrains.kotlin.psi.KtNamedFunction;
import org.jetbrains.kotlin.psi.KtPsiUtil;

import java.util.Collection;

public class KotlinService {

    public static KotlinService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, KotlinService.class);
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

}
