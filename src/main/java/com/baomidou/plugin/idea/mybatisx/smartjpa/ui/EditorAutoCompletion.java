package com.baomidou.plugin.idea.mybatisx.smartjpa.ui;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.MxParameterManager;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.parameter.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.completion.res.ReturnWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.mapping.TableMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.CompositeManagerAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EditorAutoCompletion {
    List<TxField> mappingField;
    private PsiClass entityClass;
    final AreaOperateManager appenderManager;
    TableMappingResolver tableMappingResolver ;


    private EditorAutoCompletion(PsiClass entityClass) {
        this.entityClass = entityClass;
        tableMappingResolver = new TableMappingResolver(entityClass);
        mappingField = tableMappingResolver.getFields();

        appenderManager = new CompositeManagerAdaptor(mappingField);
    }

    static Map<PsiClass, EditorAutoCompletion> map = new ConcurrentHashMap<>();

    /**
     * 加一层缓存
     *
     * @param entityClass
     * @return
     */
    public static EditorAutoCompletion createEditorAutoCompletion(PsiClass entityClass) {

        if (!map.containsKey(entityClass)) {
            map.put(entityClass, new EditorAutoCompletion(entityClass));
        }
        return map.get(entityClass);
    }

    public MxParameterManager getParameter(String text) {
        LinkedList<SyntaxAppender> jpaStringList = appenderManager.getJpaList(text);
        List<MxParameter> parameters = appenderManager.getParameters(entityClass, jpaStringList);
        return new MxParameterManager(parameters);
    }


    public ReturnWrapper getReturn(String text) {
        return appenderManager.getReturnWrapper(text, entityClass);
    }

    public MapperTagInfo generateMapperXml(PsiMethod psiMethod, String text) {
        return appenderManager.generateMapperXml(text, entityClass, psiMethod, tableMappingResolver.getTableName());
    }
}
