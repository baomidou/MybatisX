package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.intellij.psi.xml.XmlTag;

/**
 * 生成mybatis的xml文件内容.
 * 第一版在这里预留一个形式
 * 后续可以加入:  springjpa提示, 生成mybatis注解
 */
public class MybatisXmlGenerator {

    private Mapper mapper;

    public MybatisXmlGenerator(Mapper mapper) {
        this.mapper = mapper;
    }

    public void generateSelect(String id, String value) {
        XmlTag select = mapper.ensureTagExists().createChildTag("select", null, value, false);
        select.setAttribute("id",id);
        select.setAttribute("resultMap","BaseResultMap");
        mapper.ensureTagExists().addSubTag(select,false);
    }

    public void generateDelete(String id, String value) {

        XmlTag delete = mapper.ensureTagExists().createChildTag("delete", null, value, false);
        delete.setAttribute("id",id);
        mapper.ensureTagExists().addSubTag(delete,false);
    }

    public void generateInsert(String id, String value) {
        XmlTag insert = mapper.ensureTagExists().createChildTag("insert", null, value, false);
        insert.setAttribute("id",id);
        mapper.ensureTagExists().addSubTag(insert,false);
    }

    public void generateUpdate(String id, String value) {
        XmlTag update = mapper.ensureTagExists().createChildTag("update", null, value, false);
        update.setAttribute("id",id);
        mapper.ensureTagExists().addSubTag(update,false);
    }
}
