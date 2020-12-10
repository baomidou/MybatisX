package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.dom.MapperBacktrackingUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.baomidou.plugin.idea.mybatisx.util.MybatisConstants;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * The type Psi field reference set resolver.
 *
 * @author yanglin
 */
public class PsiColumnReferenceSetResolver {


    private static final Splitter SPLITTER = Splitter.on(MybatisConstants.DOT_SEPARATOR);

    /**
     * The Project.
     */
    protected Project project;

    /**
     * The Element.
     */
    protected XmlAttributeValue element;

    /**
     * The Texts.
     */
    protected List<String> texts;

    /**
     * Instantiates a new Context reference set resolver.
     *
     * @param element the element
     */
    protected PsiColumnReferenceSetResolver(@NotNull XmlAttributeValue element) {
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
    public final Optional<DasTable> resolve(int index) {
        return getStartElement();
    }

    /**
     * Gets start element.
     *
     * @return the start element
     */
    public Optional<DasTable> getStartElement() {
        return getStartElement(Iterables.getFirst(texts, null));
    }


    /**
     * Gets element.
     *
     * @return the element
     */
    public XmlAttributeValue getElement() {
        return element;
    }

    /**
     * Sets element.
     *
     * @param element the element
     */
    public void setElement(XmlAttributeValue element) {
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

    @NotNull
    public String getText() {
        return getElement().getValue();
    }


    public Optional<DasTable> getStartElement(@Nullable String firstText) {
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getEntityClass(getElement());
        if (!clazz.isPresent()) {
            return Optional.empty();
        }
        PsiClass entityClass = clazz.get();
        assert firstText != null;
        EntityMappingResolverFactory entityMappingResolverFactory
            = new EntityMappingResolverFactory(project);

        String tableName = entityMappingResolverFactory.findTableName(entityClass);
        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        for (DbDataSource dataSource : dbPsiFacade.getDataSources()) {
            JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(dataSource);
            for (DasNamespace schema : schemas) {
                DasTable dasTable = DasUtil.findChild(schema, DasTable.class, ObjectKind.TABLE, tableName);
                if (dasTable != null) {
                    return Optional.of(dasTable);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<DbElement> findColumns(DasTable dasTable) {
        String firstText = Iterables.getFirst(texts, null);
        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        DasColumn child = DasUtil.findChild(dasTable, DasColumn.class, ObjectKind.COLUMN, firstText);
        if (child != null) {
            DbElement element = dbPsiFacade.findElement(child);
            return Optional.ofNullable(element);
        }
        return Optional.empty();
    }

}
