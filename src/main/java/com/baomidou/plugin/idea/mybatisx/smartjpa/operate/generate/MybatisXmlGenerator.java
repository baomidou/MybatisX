package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.baomidou.plugin.idea.mybatisx.util.ClassCreator;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 生成mybatis的xml文件内容.
 * 第一版在这里预留一个形式
 * 后续可以加入:  springjpa提示, 生成mybatis注解
 */
public class MybatisXmlGenerator implements Generator {

    /**
     * The constant ID.
     */
    public static final String ID = "id";
    /**
     * The constant RESULT_MAP.
     */
    public static final String RESULT_MAP = "resultMap";
    /**
     * The constant RESULT_TYPE.
     */
    public static final String RESULT_TYPE = "resultType";
    private static final Logger logger = LoggerFactory.getLogger(MybatisXmlGenerator.class);
    private MapperClassGenerateFactory mapperClassGenerateFactory;
    private Mapper mapper;
    private Project project;

    /**
     * Instantiates a new Mybatis xml generator.
     *
     * @param mapperClassGenerateFactory
     * @param mapper                     the mapper
     * @param project                    the project
     */
    public MybatisXmlGenerator(MapperClassGenerateFactory mapperClassGenerateFactory, Mapper mapper, @NotNull Project project) {
        this.mapperClassGenerateFactory = mapperClassGenerateFactory;
        this.mapper = mapper;
        this.project = project;
    }

    @Override
    public void generateSelect(String id,
                               String value,
                               Boolean isResultType,
                               String resultMap,
                               String resultType,
                               List<TxField> resultFields,
                               PsiClass entityClass) {
        generateSelectTag(id, value, isResultType, resultMap, resultType, resultFields, entityClass);

        mapperClassGenerateFactory.generateMethod();

    }

    private void generateResultMapTag(String resultMap,
                                      String resultType,
                                      List<TxField> resultFields) {
        XmlTag xmlTag = mapper.ensureTagExists();
        XmlTag resultMapTag = xmlTag.createChildTag("resultMap", null, null, false);
        resultMapTag.setAttribute("id", resultMap);
        resultMapTag.setAttribute("type", resultType);

        for (TxField resultField : resultFields) {
            XmlTag result = resultMapTag.createChildTag("result", null, null, false);
            result.setAttribute("column", resultField.getColumnName());
            result.setAttribute("property", resultField.getFieldName());
            if (resultField.getJdbcType() != null) {
                result.setAttribute("jdbcType", resultField.getJdbcType());
            }
            resultMapTag.addSubTag(result, false);
        }
        xmlTag.addSubTag(resultMapTag, false);
    }

    private void generateSelectTag(String id,
                                   String value,
                                   Boolean isResultType,
                                   String resultMap,
                                   String resultType,
                                   List<TxField> resultFields,
                                   PsiClass entityClass) {
        XmlTag xmlTag = mapper.ensureTagExists();
        XmlTag select = xmlTag.createChildTag("select", null, value, false);
        select.setAttribute(ID, id);
        // 是否被映射结果集
        if (isResultType) {
            select.setAttribute(RESULT_TYPE, resultType);
        } else {
            select.setAttribute(RESULT_MAP, resultMap);
            if (isGenerateResultMap(xmlTag, resultMap)) {
                generateResultMapClass(entityClass, resultType, resultFields);
                generateResultMapTag(resultMap, resultType, resultFields);
            }
        }


        xmlTag.addSubTag(select, false);
        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(select);

    }

    Set<String> allowedResultMapNames = new HashSet<String>(){
        {
            add("BaseResultMap");
            add("BlobResultMap");
        }
    };
    private void generateResultMapClass(PsiClass entityClass, String resultType, List<TxField> resultFields) {

        Set<String> allowFields = resultFields.stream().map(TxField::getFieldName).collect(Collectors.toSet());
        String dtoName = null;
        int i = resultType.lastIndexOf(".");
        if (i > -1) {
            dtoName = resultType.substring(i + 1);
        }
        if (dtoName == null) {
            dtoName = resultType;
        }
        ClassCreator classCreator = new ClassCreator();
        classCreator.createFromAllowedFields(allowFields, entityClass, dtoName);
    }

    private Boolean isGenerateResultMap(XmlTag xmlTag, String resultMapId) {
        Boolean generate = null;
        // 支持countByXX的形式, 这种形式没有resultMapId
        if (resultMapId == null) {
            generate = false;
        }
        if (allowedResultMapNames.contains(resultMapId)) {
            generate = false;
        }

        if (generate == null) {
            @NotNull XmlTag[] resultMaps = xmlTag.findSubTags("resultMap");
            for (XmlTag resultMap : resultMaps) {
                XmlAttribute id = resultMap.getAttribute("id");
                if (id != null && resultMapId.equals(id.getValue())) {
                    generate = false;
                }
            }
        }
        if (generate == null) {
            generate = true;
        }
        return generate;
    }

    @Override
    public void generateDelete(String id, String value) {
        XmlTag delete = mapper.ensureTagExists().createChildTag("delete", null, value, false);
        delete.setAttribute(ID, id);
        mapper.ensureTagExists().addSubTag(delete, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(delete);

        mapperClassGenerateFactory.generateMethod();
    }

    @Override
    public void generateInsert(String id, String value) {
        XmlTag insert = mapper.ensureTagExists().createChildTag("insert", null, value, false);
        insert.setAttribute(ID, id);
        XmlTag xmlTag = mapper.ensureTagExists();
        xmlTag.addSubTag(insert, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(insert);

        mapperClassGenerateFactory.generateMethod();

    }

    @Override
    public void generateUpdate(String id, String value) {
        XmlTag update = mapper.ensureTagExists().createChildTag("update", null, value, false);
        update.setAttribute(ID, id);
        mapper.ensureTagExists().addSubTag(update, false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(update);

        mapperClassGenerateFactory.generateMethod();

    }

    @Override
    public boolean checkCanGenerate(PsiClass mapperClass) {
        final Collection<Mapper> mappers = MapperUtils.findMappers(mapperClass.getProject(), mapperClass);
        if (!hasMapperXmlFiles(mappers)) {
            final String message = "mapper :'" + mapperClass.getQualifiedName() + "'is not related mapper xml";
            Messages.showWarningDialog(message, "generate failure");
            return false;
        }
        return true;
    }

    private boolean hasMapperXmlFiles(Collection<Mapper> mappers) {
        return mappers.size() > 0;
    }
}
