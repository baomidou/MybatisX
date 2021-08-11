package com.baomidou.plugin.idea.mybatisx.util;

import com.baomidou.plugin.idea.mybatisx.dom.model.Configuration;
import com.baomidou.plugin.idea.mybatisx.dom.model.IdDomElement;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Package;
import com.baomidou.plugin.idea.mybatisx.dom.model.TypeAlias;
import com.baomidou.plugin.idea.mybatisx.dom.model.TypeAliases;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.Processor;
import com.intellij.util.xml.DomElement;
import com.intellij.util.xml.DomUtil;
import com.intellij.util.xml.GenericAttributeValue;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;


/**
 * The type Mapper utils.
 *
 * @author yanglin
 */
public final class MapperUtils {

    private MapperUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Find parent id dom element optional.
     *
     * @param element the element
     * @return the optional
     */
    public static Optional<IdDomElement> findParentIdDomElement(@Nullable PsiElement element) {
        DomElement domElement = DomUtil.getDomElement(element);
        if (null == domElement) {
            return Optional.empty();
        }
        if (domElement instanceof IdDomElement) {
            return Optional.of((IdDomElement) domElement);
        }
        return Optional.ofNullable(DomUtil.getParentOfType(domElement, IdDomElement.class, true));
    }

    /**
     * Create mapper from file template psi element.
     *
     * @param fileTemplateName the file template name
     * @param fileName         the file name
     * @param directory        the directory
     * @param pops             the pops
     * @param project          the project
     * @return the psi element
     * @throws Exception the exception
     */
    public static PsiElement createMapperFromFileTemplate(@NotNull String fileTemplateName,
                                                          @NotNull String fileName,
                                                          @NotNull PsiDirectory directory,
                                                          @Nullable Properties pops,
                                                          @NotNull Project project) throws Exception {
        FileTemplate fileTemplate = FileTemplateManager.getInstance(project).getJ2eeTemplate(fileTemplateName);
        PsiFile file = directory.findFile(fileName + "." + fileTemplate.getExtension());
        // ignore file exists
        if (file != null) {
            Messages.showMessageDialog("file " + file.getName() + " already exists", "Generate File", Messages.getWarningIcon());
            return null;
        }
        return FileTemplateUtil.createFromTemplate(fileTemplate, fileName, pops, directory);
    }

    /**
     * Find mapper directories collection.
     *
     * @param project the project
     * @return the collection
     */
    @NotNull
    public static Collection<PsiDirectory> findMapperDirectories(@NotNull Project project) {
        return Collections2.transform(findMappers(project), new Function<Mapper, PsiDirectory>() {
            @Override
            public PsiDirectory apply(Mapper input) {
                return input.getXmlElement().getContainingFile().getContainingDirectory();
            }
        });
    }

    /**
     * Is element within mybatis file boolean.
     *
     * @param element the element
     * @return the boolean
     */
    public static boolean isElementWithinMybatisFile(@NotNull PsiElement element) {
        PsiFile psiFile = element.getContainingFile();
        return element instanceof XmlElement && DomUtils.isMybatisFile(psiFile);
    }

    /**
     * Find mappers collection.
     *
     * @param project the project
     * @return the collection
     */
    @NotNull
    @NonNls
    public static Collection<Mapper> findMappers(@NotNull Project project) {
        return DomUtils.findDomElements(project, Mapper.class);
    }

    /**
     * Find mappers collection.
     *
     * @param project   the project
     * @param namespace the namespace
     * @return the collection
     */
    @NotNull
    @NonNls
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull String namespace) {
        List<Mapper> result = Lists.newArrayList();
        for (Mapper mapper : findMappers(project)) {
            if (getNamespace(mapper).equals(namespace)) {
                result.add(mapper);
            }
        }
        return result;
    }

    @NotNull
    @NonNls
    public static Collection<XmlElement> findTags(@NotNull Project project, @NotNull PsiMethod method) {
        final PsiClass containingClass = method.getContainingClass();
        if (containingClass == null) {
            return Collections.emptyList();
        }
        List<XmlElement> result = Lists.newArrayList();
        for (Mapper mapper : findMappers(project)) {
            final GenericAttributeValue<PsiClass> namespace = mapper.getNamespace();
            final PsiClass namespaceValue = namespace.getValue();
            if (namespaceValue == null) {
                continue;
            }
            if (namespaceValue.equals(containingClass) ||
                namespaceValue.isInheritor(containingClass, true)) {
                final Optional<IdDomElement> first = mapper.getDaoElements().stream()
                    .filter(item -> method.getName().equals(item.getId().getStringValue()))
                    .findFirst();
                first.ifPresent(item -> result.add(item.getXmlElement()));
            }
        }
        return result;
    }

    public static XmlTag findTag(Project project, PsiMethod psiMethod) {
        final PsiClass containingClass = psiMethod.getContainingClass();
        if (containingClass == null) {
            return null;
        }
        for (Mapper mapper : findMappers(project)) {
            final GenericAttributeValue<PsiClass> namespace = mapper.getNamespace();
            final String namespaceStringValue = namespace.getStringValue();
            if (namespaceStringValue != null &&
                namespaceStringValue.equals(containingClass.getQualifiedName())) {
                return findTagByMapper(mapper, psiMethod.getName());
            }
        }
        return null;
    }

    private static XmlTag findTagByMapper(Mapper mapper, String id) {
        for (IdDomElement daoElement : mapper.getDaoElements()) {
            if (id.equals(daoElement.getId().getStringValue())) {
                return daoElement.getXmlTag();
            }
        }
        return null;
    }

    /**
     * Find mappers collection.
     *
     * @param project the project
     * @param clazz   the clazz
     * @return the collection
     */
    @NotNull
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull PsiClass clazz) {
        return JavaUtils.isElementWithinInterface(clazz) ? findMappers(project, Objects.requireNonNull(clazz.getQualifiedName())) : Collections.emptyList();
    }

    /**
     * Find mappers collection.
     *
     * @param project the project
     * @param method  the method
     * @return the collection
     */
    @NotNull
    public static Collection<Mapper> findMappers(@NotNull Project project, @NotNull PsiMethod method) {
        PsiClass clazz = method.getContainingClass();
        return null == clazz ? Collections.emptyList() : findMappers(project, clazz);
    }

    /**
     * Find first mapper optional.
     *
     * @param project   the project
     * @param namespace the namespace
     * @return the optional
     */
    @NonNls
    public static Optional<Mapper> findFirstMapper(@NotNull Project project, @NotNull String namespace) {
        Collection<Mapper> mappers = findMappers(project, namespace);
        return CollectionUtils.isEmpty(mappers) ? Optional.<Mapper>empty() : Optional.of(mappers.iterator().next());
    }

    /**
     * Find first mapper optional.
     *
     * @param project the project
     * @param clazz   the clazz
     * @return the optional
     */
    @NonNls
    public static Optional<Mapper> findFirstMapper(@NotNull Project project, @NotNull PsiClass clazz) {
        String qualifiedName = clazz.getQualifiedName();
        return null != qualifiedName ? findFirstMapper(project, qualifiedName) : Optional.<Mapper>empty();
    }

    /**
     * Find first mapper optional.
     *
     * @param project the project
     * @param method  the method
     * @return the optional
     */
    @NonNls
    public static Optional<Mapper> findFirstMapper(@NotNull Project project, @NotNull PsiMethod method) {
        PsiClass containingClass = method.getContainingClass();
        return null != containingClass ? findFirstMapper(project, containingClass) : Optional.empty();
    }

    /**
     * Gets mapper.
     *
     * @param element the element
     * @return the mapper
     */
    @SuppressWarnings("unchecked")
    @NotNull
    @NonNls
    public static Mapper getMapper(@NotNull DomElement element) {
        Optional<Mapper> optional = Optional.ofNullable(DomUtil.getParentOfType(element, Mapper.class, true));
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new IllegalArgumentException("Unknown element");
        }
    }

    /**
     * Gets namespace.
     *
     * @param mapper the mapper
     * @return the namespace
     */
    @NotNull
    @NonNls
    public static String getNamespace(@NotNull Mapper mapper) {
        String ns = mapper.getNamespace().getStringValue();
        return null == ns ? "" : ns;
    }

    /**
     * Gets namespace.
     *
     * @param element the element
     * @return the namespace
     */
    @NotNull
    @NonNls
    public static String getNamespace(@NotNull DomElement element) {
        return getNamespace(getMapper(element));
    }

    /**
     * Is mapper with same namespace boolean.
     *
     * @param mapper the mapper
     * @param target the target
     * @return the boolean
     */
    @NonNls
    public static boolean isMapperWithSameNamespace(@Nullable Mapper mapper, @Nullable Mapper target) {
        return null != mapper && null != target && getNamespace(mapper).equals(getNamespace(target));
    }

    /**
     * Gets id.
     *
     * @param <T>        the type parameter
     * @param domElement the dom element
     * @return the id
     */
    @Nullable
    @NonNls
    public static <T extends IdDomElement> String getId(@NotNull T domElement) {
        return domElement.getId().getRawText();
    }

    /**
     * Gets id signature.
     *
     * @param <T>        the type parameter
     * @param domElement the dom element
     * @return the id signature
     */
    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement) {
        return getNamespace(domElement) + "." + getId(domElement);
    }

    /**
     * Gets id signature.
     *
     * @param <T>        the type parameter
     * @param domElement the dom element
     * @param mapper     the mapper
     * @return the id signature
     */
    @NotNull
    @NonNls
    public static <T extends IdDomElement> String getIdSignature(@NotNull T domElement, @NotNull Mapper mapper) {
        Mapper contextMapper = getMapper(domElement);
        String id = getId(domElement);
        if (id == null) {
            id = "";
        }
        String idsignature = getIdSignature(domElement);
        //getIdSignature(domElement)
        return isMapperWithSameNamespace(contextMapper, mapper) ? id : idsignature;
    }

    /**
     * Process configured type aliases.
     *
     * @param project   the project
     * @param processor the processor
     */
    public static void processConfiguredTypeAliases(@NotNull Project project, @NotNull Processor<TypeAlias> processor) {
        for (Configuration conf : getMybatisConfigurations(project)) {
            for (TypeAliases tas : conf.getTypeAliases()) {
                for (TypeAlias ta : tas.getTypeAlias()) {
                    String stringValue = ta.getAlias().getStringValue();
                    if (null != stringValue && !processor.process(ta)) {
                        return;
                    }
                }
            }
        }
    }

    private static Collection<Configuration> getMybatisConfigurations(Project project) {
        return DomUtils.findDomElements(project, Configuration.class);
    }

    /**
     * Process configured package.
     *
     * @param project   the project
     * @param processor the processor
     */
    public static void processConfiguredPackage(@NotNull Project project,
                                                @NotNull Processor<Package> processor) {
        for (Configuration conf : getMybatisConfigurations(project)) {
            for (TypeAliases tas : conf.getTypeAliases()) {
                for (Package pkg : tas.getPackages()) {
                    if (!processor.process(pkg)) {
                        return;
                    }
                }
            }
        }
    }


}
