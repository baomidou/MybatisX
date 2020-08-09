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
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EditorAutoCompletion {
    private @NotNull LinkedList<SyntaxAppender> jpaList;
    private List<TxField> mappingField;
    private PsiClass entityClass;
    final AreaOperateManager appenderManager;
    TableMappingResolver tableMappingResolver;
    private String text;


    private EditorAutoCompletion(PsiClass entityClass, String text) {
        this.entityClass = entityClass;
        tableMappingResolver = new TableMappingResolver(entityClass);
        this.text = text;
        mappingField = tableMappingResolver.getFields();


        appenderManager = new CompositeManagerAdaptor(mappingField);
        jpaList = appenderManager.splitAppenderByText(text);
    }

    static Map<PsiClass, EditorAutoCompletion> map = new ConcurrentHashMap<>();

    /**
     * 加一层缓存
     *
     * @param entityClass
     * @param text
     * @return
     */
    public static EditorAutoCompletion createEditorAutoCompletion(PsiClass entityClass, String text) {
        return new EditorAutoCompletion(entityClass, text);
    }

    public MxParameterManager getParameter() {
        List<MxParameter> parameters = appenderManager.getParameters(entityClass, new LinkedList<>(jpaList));
        return new MxParameterManager(parameters);
    }


    public ReturnWrapper getReturn() {
        LinkedList<SyntaxAppender> linkedList = new LinkedList<>(jpaList);
        return appenderManager.getReturnWrapper(text, entityClass, linkedList);
    }

    public MapperTagInfo generateMapperXml(PsiMethod psiMethod) {
        MapperTagInfo mapperTagInfo = appenderManager.generateMapperXml(new LinkedList<>(jpaList), entityClass, psiMethod,
            tableMappingResolver.getTableName());
        mapperTagInfo.setId(text);
        return mapperTagInfo;
    }
}
