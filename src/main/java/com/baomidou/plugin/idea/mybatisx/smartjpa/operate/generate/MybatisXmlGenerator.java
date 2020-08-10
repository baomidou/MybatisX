package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Delete;
import com.baomidou.plugin.idea.mybatisx.dom.model.Insert;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Select;
import com.baomidou.plugin.idea.mybatisx.dom.model.Update;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.util.xml.DomElement;

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
        Select select = mapper.addSelect();
        select.getId().setStringValue(id);
        select.setValue(value);
    }

    public void generateDelete(String id, String value) {
        Delete delete = mapper.addDelete();
        delete.getId().setStringValue(id);
        delete.setValue(value);
    }

    public void generateInsert(String id, String value) {
        Insert insert = mapper.addInsert();
        insert.getId().setStringValue(id);
        insert.setValue(value);
    }

    public void generateUpdate(String id, String value) {
        Update update = mapper.addUpdate();
        update.getId().setStringValue(id);
        update.setValue(value);
    }
}
