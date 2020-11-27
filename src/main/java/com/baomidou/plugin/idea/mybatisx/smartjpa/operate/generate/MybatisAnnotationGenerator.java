package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.MapperClassGenerateFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TxField;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 生成mybatis的xml文件内容.
 * 第一版在这里预留一个形式
 * 后续可以加入:  springjpa提示, 生成mybatis注解
 */
public class MybatisAnnotationGenerator implements Generator {

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
    private Mapper mapper;
    private MapperClassGenerateFactory mapperClassGenerateFactory;
    private Project project;

    /**
     * Instantiates a new Mybatis annotation generator.
     *
     * @param mapperClassGenerateFactory
     * @param mapper
     * @param project                     the project
     */
    public MybatisAnnotationGenerator(MapperClassGenerateFactory mapperClassGenerateFactory, Mapper mapper, @NotNull Project project) {
        this.mapperClassGenerateFactory = mapperClassGenerateFactory;
        this.mapper = mapper;
        this.project = project;
    }

    @Override
    public void generateSelect(String id, String value, Boolean isResultType, String resultMap, String resultType, List<TxField> resultFields, PsiClass entityClass) {
        List<String> importClass = new ArrayList<>();

        String text = "<script>" + value + "</script>";
        String resultMapAnnotationPrefix = "";
        if (!isResultType) {
            resultMapAnnotationPrefix = "@ResultMap(\"BaseResultMap\")";
            importClass.add("org.apache.ibatis.annotations.ResultMap");
        }
        text = wrappedText(text);
        text = "@Select(\"" + text + "\")" + "\n";
        text = resultMapAnnotationPrefix + text;
        importClass.add("org.apache.ibatis.annotations.Select");
        mapperClassGenerateFactory.generateMethod(text, importClass);
    }

    private String wrappedText(String text) {
        text = text.replaceAll("\"", "\\\\\"");
        String[] split = text.split("\n");
        if (split.length > 1) {
            text = String.join("\" \n + \"", split);
        }
        return text;
    }

    @Override
    public void generateDelete(String id, String value) {
        String text = "<script>" + value + "</script>";
        text = wrappedText(text);
        text = "@Delete(\"" + text + "\")";
        mapperClassGenerateFactory.generateMethod(text, Collections.singletonList("org.apache.ibatis.annotations.Delete"));
    }

    @Override
    public void generateInsert(String id, String value) {
        String text = "<script>" + value + "</script>";

        text = wrappedText(text);
        text = "@Insert(\"" + text + "\")";
        mapperClassGenerateFactory.generateMethod(text, Collections.singletonList("org.apache.ibatis.annotations.Insert"));
    }

    @Override
    public void generateUpdate(String id, String value) {
        String text = "<script>" + value + "</script>";
        text = wrappedText(text);
        text = "@Update(\"" + text + "\")";
        mapperClassGenerateFactory.generateMethod(text, Collections.singletonList("org.apache.ibatis.annotations.Update"));
    }
}
