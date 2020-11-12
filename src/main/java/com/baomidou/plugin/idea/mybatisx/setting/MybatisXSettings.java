package com.baomidou.plugin.idea.mybatisx.setting;

import com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator;
import com.baomidou.plugin.idea.mybatisx.generate.GenerateModel;
import com.baomidou.plugin.idea.mybatisx.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Set;

import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.*;

/**
 * The type Mybatis setting.
 *
 * @author yanglin
 */
@State(
    name = "MybatisXSettings",
    storages = @Storage(value = "$APP_CONFIG$/mybatis.xml"))
public class MybatisXSettings implements PersistentStateComponent<Element> {

    private static final String MAPPER_ICON = "mapperIcon";
    private GenerateModel statementGenerateModel;

    private Gson gson = new Gson();

    private Type gsonTypeToken = new TypeToken<Set<String>>() {
    }.getType();

    /**
     * Instantiates a new Mybatis setting.
     */
    public MybatisXSettings() {
        statementGenerateModel = GenerateModel.START_WITH_MODEL;
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MybatisXSettings getInstance() {
        return ServiceManager.getService(MybatisXSettings.class);
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
        element.setAttribute("mapperIcon",getMapperIcon());
        return element;
    }

    @Override
    public void loadState(Element state) {
        loadState(state, INSERT_GENERATOR);
        loadState(state, DELETE_GENERATOR);
        loadState(state, UPDATE_GENERATOR);
        loadState(state, SELECT_GENERATOR);
        statementGenerateModel = GenerateModel.getInstance(state.getAttributeValue("statementGenerateModel"));
        String mapperIcon = state.getAttributeValue(MAPPER_ICON);
        if(StringUtils.isEmpty(mapperIcon)){
            mapperIcon = MapperIcon.BIRD.name();
        }
        this.mapperIcon = mapperIcon;
    }

    private void loadState(Element state, AbstractStatementGenerator generator) {
        String attribute = state.getAttributeValue(generator.getId());
        if (null != attribute) {
            generator.setPatterns(gson.fromJson(attribute, gsonTypeToken));
        }
    }

    /**
     * Gets statement generate model.
     *
     * @return the statement generate model
     */
    public GenerateModel getStatementGenerateModel() {
        return statementGenerateModel;
    }

    /**
     * Sets statement generate model.
     *
     * @param statementGenerateModel the statement generate model
     */
    public void setStatementGenerateModel(GenerateModel statementGenerateModel) {
        this.statementGenerateModel = statementGenerateModel;
    }

    private String mapperIcon;

    public void setMapperIcon(String mapperIcon) {
        this.mapperIcon = mapperIcon;
    }

    public String getMapperIcon() {
        if(mapperIcon == null){
            mapperIcon = MapperIcon.BIRD.name();
        }
        return mapperIcon;
    }

    public enum MapperIcon{
        DEFAULT,
        BIRD;
    }
}
