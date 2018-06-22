package com.baomidou.plugin.idea.mybatisx.setting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.baomidou.plugin.idea.mybatisx.generate.GenerateModel;
import com.baomidou.plugin.idea.mybatisx.generate.StatementGenerator;

import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

import static com.baomidou.plugin.idea.mybatisx.generate.StatementGenerator.DELETE_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.StatementGenerator.INSERT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.StatementGenerator.SELECT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.StatementGenerator.UPDATE_GENERATOR;

/**
 * @author yanglin
 */
@State(
        name = "MybatisSettings",
        storages = @Storage(id = "other", file = "$APP_CONFIG$/mybatis.xml"))
public class MybatisSetting implements PersistentStateComponent<Element> {

    private GenerateModel statementGenerateModel;

    private Gson gson = new Gson();

    private Type gsonTypeToken = new TypeToken<Set<String>>() {
    }.getType();

    public MybatisSetting() {
        statementGenerateModel = GenerateModel.START_WITH_MODEL;
    }

    public static MybatisSetting getInstance() {
        return ServiceManager.getService(MybatisSetting.class);
    }

    @Nullable
    @Override
    public Element getState() {
        Element element = new Element("MybatisSettings");
        element.setAttribute(INSERT_GENERATOR.getId(), gson.toJson(INSERT_GENERATOR.getPatterns()));
        element.setAttribute(DELETE_GENERATOR.getId(), gson.toJson(DELETE_GENERATOR.getPatterns()));
        element.setAttribute(UPDATE_GENERATOR.getId(), gson.toJson(UPDATE_GENERATOR.getPatterns()));
        element.setAttribute(SELECT_GENERATOR.getId(), gson.toJson(SELECT_GENERATOR.getPatterns()));
        element.setAttribute("statementGenerateModel", String.valueOf(statementGenerateModel.getIdentifier()));
        return element;
    }

    @Override
    public void loadState(Element state) {
        loadState(state, INSERT_GENERATOR);
        loadState(state, DELETE_GENERATOR);
        loadState(state, UPDATE_GENERATOR);
        loadState(state, SELECT_GENERATOR);
        statementGenerateModel = GenerateModel.getInstance(state.getAttributeValue("statementGenerateModel"));
    }

    private void loadState(Element state, StatementGenerator generator) {
        String attribute = state.getAttributeValue(generator.getId());
        if (null != attribute) {
            generator.setPatterns((Set<String>) gson.fromJson(attribute, gsonTypeToken));
        }
    }

    public GenerateModel getStatementGenerateModel() {
        return statementGenerateModel;
    }

    public void setStatementGenerateModel(GenerateModel statementGenerateModel) {
        this.statementGenerateModel = statementGenerateModel;
    }
}
