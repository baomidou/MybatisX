package com.baomidou.plugin.idea.mybatisx.smartjpa.operate;







import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.model.AppendTypeEnum;
import com.baomidou.plugin.idea.mybatisx.smartjpa.ui.MapperTagInfo;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import com.intellij.util.containers.ArrayListSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CompositeManagerAdaptor implements AreaOperateManager {
    List<AreaOperateManager> typeManagers = new ArrayList<>();

    public CompositeManagerAdaptor(final List<TxField> mappingField) {
        this.init(mappingField);
    }

    protected void init(final List<TxField> mappingField) {
        this.typeManagers.add(new SelectOperator(mappingField));
        this.typeManagers.add(new InsertOperator(mappingField));
        this.typeManagers.add(new UpdateOperator(mappingField));
        this.typeManagers.add(new DeleteOperator(mappingField));
    }

    @NotNull
    @Override
    public LinkedList<SyntaxAppender> getJpaList(final String splitParam) {
        final LinkedList<SyntaxAppender> results = new LinkedList<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            results.addAll(typeManager.getJpaList(splitParam));
        }
        return results;
    }


    @Override
    public Set<String> getCompletionContent(final LinkedList<SyntaxAppender> splitList) {
        final Set<String> results = new ArrayListSet<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            results.addAll(typeManager.getCompletionContent(splitList));
        }
        return results;
    }

    @Override
    public List<MxParameter> getParameters(PsiClass entityClass,
                                           LinkedList<SyntaxAppender> jpaStringList) {
        if (jpaStringList.size() == 0 || jpaStringList.get(0).getType() != AppendTypeEnum.AREA) {
            return Collections.emptyList();
        }
        SyntaxAppender syntaxAppender = jpaStringList.peek();

        final List<MxParameter> results = new ArrayList<>();
        for (final AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                results.addAll(typeManager.getParameters(entityClass, jpaStringList));
            }
        }
        return results;
    }

    @Override
    public ReturnWrapper getReturnWrapper(String text, PsiClass entityClass) {
        LinkedList<SyntaxAppender> jpaStringList = getJpaList(text);
        if (jpaStringList.size() == 0 || jpaStringList.get(0).getType() != AppendTypeEnum.AREA) {
            return null;
        }
        SyntaxAppender syntaxAppender = jpaStringList.peek();

        for (AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                return typeManager.getReturnWrapper(text, entityClass);
            }
        }
        return null;
    }

    @Override
    public boolean support(String text) {
        return true;
    }

    @Override
    public MapperTagInfo generateMapperXml(String text, PsiClass entityClass, PsiMethod psiMethod, String tableNameByEntityName) {
        LinkedList<SyntaxAppender> jpaStringList = getJpaList(text);
        if (jpaStringList.size() == 0 || jpaStringList.get(0).getType() != AppendTypeEnum.AREA) {
            return null;
        }
        SyntaxAppender syntaxAppender = jpaStringList.peek();

        for (AreaOperateManager typeManager : this.typeManagers) {
            if (typeManager.support(syntaxAppender.getText())) {
                return typeManager.generateMapperXml(text, entityClass, psiMethod, tableNameByEntityName);
            }
        }
        return null;
    }


}
