package com.baomidou.plugin.idea.mybatisx.setting;

import com.google.common.base.Joiner;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.DELETE_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.INSERT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.SELECT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.UPDATE_GENERATOR;

/**
 * The type Mybatis setting.
 * 这里是全局配置, 所以配置文件在目录($APP_CONFIG$)下
 *
 * @author yanglin
 */
@State(
    name = "MybatisXSettings",
    storages = @Storage(value = "$APP_CONFIG$/mybatisx.xml"))
public class MybatisXSettings implements PersistentStateComponent<MybatisXSettings> {

    // 配置的默认值
    private String mapperIcon;
    private String insertGenerator;
    private String updateGenerator;
    private String deleteGenerator;
    private String selectGenerator;

    private static transient Joiner joiner = Joiner.on(";");
    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static MybatisXSettings getInstance() {
        MybatisXSettings service = ServiceManager.getService(MybatisXSettings.class);
        // 配置的默认值
        if (service.insertGenerator == null) {
            service.insertGenerator = joiner.join(INSERT_GENERATOR.getPatterns());
        }
        if (service.updateGenerator == null) {
            service.updateGenerator = joiner.join(UPDATE_GENERATOR.getPatterns());
        }
        if (service.deleteGenerator == null) {
            service.deleteGenerator = joiner.join(DELETE_GENERATOR.getPatterns());
        }
        if (service.selectGenerator == null) {
            service.selectGenerator = joiner.join(SELECT_GENERATOR.getPatterns());
        }
        if (service.mapperIcon == null) {
            service.mapperIcon = MapperIcon.BIRD.name();
        }
        return service;
    }

    @Nullable
    @Override
    public MybatisXSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull MybatisXSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getInsertGenerator() {
        return insertGenerator;
    }

    public String getUpdateGenerator() {
        return updateGenerator;
    }

    public String getDeleteGenerator() {
        return deleteGenerator;
    }

    public String getSelectGenerator() {
        return selectGenerator;
    }

    public String getMapperIcon() {
        return mapperIcon;
    }

    public void setInsertGenerator(String insertGenerator) {
        this.insertGenerator = insertGenerator;
    }

    public void setUpdateGenerator(String updateGenerator) {
        this.updateGenerator = updateGenerator;
    }

    public void setDeleteGenerator(String deleteGenerator) {
        this.deleteGenerator = deleteGenerator;
    }

    public void setSelectGenerator(String selectGenerator) {
        this.selectGenerator = selectGenerator;
    }

    public void setMapperIcon(String mapperIcon) {
        this.mapperIcon = mapperIcon;
    }

    public enum MapperIcon {
        DEFAULT,
        BIRD;
    }
}
