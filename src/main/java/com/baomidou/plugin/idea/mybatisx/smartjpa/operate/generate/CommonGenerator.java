package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.smartjpa.common.SyntaxAppender;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameter;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxParameterDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.MybatisPlus3MappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.CompositeManagerAdaptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.manager.AreaOperateManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

/**
 * 常用的生成器
 */
public class CommonGenerator implements PlatformGenerator {
    private @NotNull LinkedList<SyntaxAppender> jpaList;
    private EntityMappingResolver entityMappingResolver;
    private List<TxField> mappingField;
    private PsiClass entityClass;
    final AreaOperateManager appenderManager;
    private String text;


    private CommonGenerator(PsiClass entityClass, String text, EntityMappingResolver entityMappingResolver) {
        this.entityClass = entityClass;
        this.text = text;
        mappingField = entityMappingResolver.getFields();
        appenderManager = new CompositeManagerAdaptor(mappingField,entityClass);
        jpaList = appenderManager.splitAppenderByText(text);
        this.entityMappingResolver = entityMappingResolver;
    }

    /**
     * 加一层缓存
     *
     * @param entityClass
     * @param text
     * @param entityMappingResolver
     * @return
     */
    public static CommonGenerator createEditorAutoCompletion(PsiClass entityClass, String text, EntityMappingResolver entityMappingResolver) {
        return new CommonGenerator(entityClass, text,entityMappingResolver);
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
            entityMappingResolver.getTableName()
            , mybatisXmlGenerator);

    }

}
