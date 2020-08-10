package com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate;

import com.baomidou.plugin.idea.mybatisx.dom.model.Delete;
import com.baomidou.plugin.idea.mybatisx.dom.model.Insert;
import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.dom.model.Select;
import com.baomidou.plugin.idea.mybatisx.dom.model.Update;

public class MybatisXmlGenerator {

    private Mapper mapper;

    public MybatisXmlGenerator(Mapper mapper) {
        this.mapper = mapper;
    }

    public void generateSelect(String id,String value){
        Select select = mapper.addSelect();
        select.getId().setStringValue(id);
//        select.getXmlElement().add(LookupElementBuilder.create("--TEST--").getPsiElement());
//        select.getXmlElement().add(LookupElementBuilder.create(value).getPsiElement());
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
