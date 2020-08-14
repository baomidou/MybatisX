package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;

/**
 * 生成mybatis的xml文件内容.
 * 第一版在这里预留一个形式
 * 后续可以加入:  springjpa提示, 生成mybatis注解
 */
public class MybatisXmlGenerator {

    public static final String ID = "id";
    public static final String RESULT_MAP = "resultMap";
    private Mapper mapper;
    private Project project;

    public MybatisXmlGenerator(Mapper mapper, @NotNull Project project) {
        this.mapper = mapper;
        this.project = project;
    }

    public void generateSelect(String id, String value) {
        XmlTag select = mapper.ensureTagExists().createChildTag("select", null, value, false);
        select.setAttribute(ID,id);
        select.setAttribute(RESULT_MAP,"BaseResultMap");
        mapper.ensureTagExists().addSubTag(select,false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(select);
    }

    public void generateDelete(String id, String value) {
        XmlTag delete = mapper.ensureTagExists().createChildTag("delete", null, value, false);
        delete.setAttribute(ID,id);
        mapper.ensureTagExists().addSubTag(delete,false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(delete);
    }

    public void generateInsert(String id, String value) {
        XmlTag insert = mapper.ensureTagExists().createChildTag("insert", null, value, false);
        insert.setAttribute(ID,id);
        XmlTag xmlTag = mapper.ensureTagExists();
        xmlTag.addSubTag(insert,false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(insert);
    }

    public void generateUpdate(String id, String value) {
        XmlTag update = mapper.ensureTagExists().createChildTag("update", null, value, false);
        update.setAttribute(ID,id);
        mapper.ensureTagExists().addSubTag(update,false);

        CodeStyleManager instance = CodeStyleManager.getInstance(project);
        instance.reformat(update);
    }
}
