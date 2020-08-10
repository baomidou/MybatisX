package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameterDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.TableMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.CompositeManagerAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

public class CommonGenerator implements PlatformGenerator {
    private @NotNull LinkedList<SyntaxAppender> jpaList;
    private List<TxField> mappingField;
    private PsiClass entityClass;
    final AreaOperateManager appenderManager;
    EntityMappingResolver tableMappingResolver;
    private String text;


    private CommonGenerator(PsiClass entityClass, String text) {
        this.entityClass = entityClass;
        tableMappingResolver = new TableMappingResolver(entityClass);
        this.text = text;
        mappingField = tableMappingResolver.getFields();


        appenderManager = new CompositeManagerAdaptor(mappingField);
        jpaList = appenderManager.splitAppenderByText(text);
    }

    /**
     * 加一层缓存
     *
     * @param entityClass
     * @param text
     * @return
     */
    public static CommonGenerator createEditorAutoCompletion(PsiClass entityClass, String text) {
        return new CommonGenerator(entityClass, text);
    }

    public TypeDescriptor getParameter() {
        List<TxParameter> parameters = appenderManager.getParameters(entityClass, new LinkedList<>(jpaList));
        return new TxParameterDescriptor(parameters);
    }


    public TypeDescriptor getReturn() {
        LinkedList<SyntaxAppender> linkedList = new LinkedList<>(jpaList);
        return appenderManager.getReturnWrapper(text, entityClass, linkedList);
    }

    @Override
    public void generateMapperXml(PsiMethod psiMethod, MybatisXmlGenerator mybatisXmlGenerator) {
        appenderManager.generateMapperXml(
            text,
            new LinkedList<>(jpaList),
            entityClass,
            psiMethod,
            tableMappingResolver.getTableName()
            , mybatisXmlGenerator);

    }

}
