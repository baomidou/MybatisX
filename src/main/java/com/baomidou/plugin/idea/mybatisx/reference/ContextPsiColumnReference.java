package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingHolder;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * The type Context psi field reference.
 *
 * @author yanglin
 */
public class ContextPsiColumnReference extends PsiReferenceBase<XmlAttributeValue> {

    /**
     * The Resolver.
     */
    protected ContextReferenceSetResolver<XmlAttributeValue, DbElement> resolver;
    private PsiClass mapperClass;

    /**
     * The Index.
     */
    protected int index;

    /**
     * Instantiates a new Context psi field reference.
     *
     * @param element     the element
     * @param range       the range
     * @param index       the index
     * @param mapperClass
     */
    public ContextPsiColumnReference(XmlAttributeValue element, TextRange range, int index, PsiClass mapperClass) {
        super(element, range, false);
        this.index = index;
        resolver = ReferenceSetResolverFactory.createPsiColumnResolver(element);
        this.mapperClass = mapperClass;
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public PsiElement resolve() {
        Optional<DbElement> resolved = resolver.resolve(index);
        return resolved.orElse(null);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = getElement().getProject();

        EntityMappingResolverFactory entityMappingResolverFactory
            = new EntityMappingResolverFactory(project);
        EntityMappingHolder entityMappingHolder = entityMappingResolverFactory.searchEntity(mapperClass);
        String tableName = entityMappingHolder.getTableName();
        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        List<DbElement> dbElementList = new LinkedList<>();
        for (DbDataSource dataSource : dbPsiFacade.getDataSources()) {
            JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(dataSource);
            for (DasNamespace schema : schemas) {
                if (schema.isIntrospected()) {
                    DasTable dasTable = DasUtil.findChild(schema, DasTable.class, ObjectKind.TABLE, tableName);
                    if (dasTable != null) {
                        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dasTable);
                        for (DasColumn column : columns) {
                            DbElement element = dbPsiFacade.findElement(column);
                            dbElementList.add(element);
                        }
                    }
                }
            }
        }
        return dbElementList.size() > 0 ? dbElementList.toArray() : PsiReference.EMPTY_ARRAY;
    }


    /**
     * Gets resolver.
     *
     * @return the resolver
     */
    public ContextReferenceSetResolver<XmlAttributeValue, DbElement> getResolver() {
        return resolver;
    }

    /**
     * Sets resolver.
     *
     * @param resolver the resolver
     */
    public void setResolver(ContextReferenceSetResolver<XmlAttributeValue, DbElement> resolver) {
        this.resolver = resolver;
    }

    /**
     * Gets index.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets index.
     *
     * @param index the index
     */
    public void setIndex(int index) {
        this.index = index;
    }
}
