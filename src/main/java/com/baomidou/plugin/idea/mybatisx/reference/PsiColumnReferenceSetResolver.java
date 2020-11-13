package com.baomidou.plugin.idea.mybatisx.reference;

import com.baomidou.plugin.idea.mybatisx.dom.MapperBacktrackingUtils;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasNamespace;
import com.intellij.database.model.DasTable;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbElement;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PropertyUtil;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.util.containers.JBIterable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * The type Psi field reference set resolver.
 *
 * @author yanglin
 */
public class PsiColumnReferenceSetResolver extends ContextReferenceSetResolver<XmlAttributeValue, DbElement> {

    /**
     * Instantiates a new Psi field reference set resolver.
     *
     * @param from the from
     */
    protected PsiColumnReferenceSetResolver(XmlAttributeValue from) {
        super(from);
    }

    @NotNull
    @Override
    public String getText() {
        return getElement().getValue();
    }


    @Override
    public Optional<DbElement> getStartElement(@Nullable String firstText) {
        Optional<PsiClass> clazz = MapperBacktrackingUtils.getPropertyClazz(getElement());
        if(!clazz.isPresent()){
            return Optional.empty();
        }
        PsiClass entityClass = clazz.get();
        assert firstText != null;
        PsiMethod propertySetter = PropertyUtil.findPropertySetter(entityClass, firstText, false, true);
        if(null == propertySetter){
            return Optional.empty();
        }
        EntityMappingResolverFactory entityMappingResolverFactory
            = new EntityMappingResolverFactory(project);

        String tableName = entityMappingResolverFactory.findTableName(entityClass);
        DbPsiFacade dbPsiFacade = DbPsiFacade.getInstance(project);
        for (DbDataSource dataSource : dbPsiFacade.getDataSources()) {
            JBIterable<? extends DasNamespace> schemas = DasUtil.getSchemas(dataSource);
            for (DasNamespace schema : schemas) {
                if(schema.isDisplayed()){
                    DasTable dasTable = DasUtil.findChild(schema, DasTable.class, ObjectKind.TABLE, tableName);
                    if(dasTable != null){
                        DasColumn child = DasUtil.findChild(dasTable, DasColumn.class, ObjectKind.COLUMN, firstText);
                        if(child!=null){
                            DbElement element = dbPsiFacade.findElement(child);
                            return Optional.ofNullable(element);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

}
