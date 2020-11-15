package com.baomidou.plugin.idea.mybatisx.setting;


import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.model.User;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Map;


/**
 * 配置持久化
 */
@State(name = "PersistentConfig", storages = {@Storage("free-mybatis-generator-config.xml")})
public class PersistentConfig implements PersistentStateComponent<PersistentConfig> {

    private Map<String, Config> initConfig;
    private Map<String, User> users;
    private Map<String, Config> historyConfigList;

    /**
     * Gets instance.
     *
     * @param project the project
     * @return the instance
     */
    @Nullable
    public static PersistentConfig getInstance(Project project) {
        return ServiceManager.getService(project, PersistentConfig.class);
    }

    @Override
    @Nullable
    public PersistentConfig getState() {
        return this;
    }

    @Override
    public void loadState(PersistentConfig persistentConfig) {
        XmlSerializerUtil.copyBean(persistentConfig, this);
    }


    /**
     * Gets init config.
     *
     * @return the init config
     */
    public Map<String, Config> getInitConfig() {
        return initConfig;
    }

    /**
     * Sets init config.
     *
     * @param initConfig the init config
     */
    public void setInitConfig(Map<String, Config> initConfig) {
        this.initConfig = initConfig;
    }

    /**
     * Gets users.
     *
     * @return the users
     */
    public Map<String, User> getUsers() {
        return users;
    }

    /**
     * Sets users.
     *
     * @param users the users
     */
    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    /**
     * Gets history config list.
     *
     * @return the history config list
     */
    public Map<String, Config> getHistoryConfigList() {
        return historyConfigList;
    }

    /**
     * Sets history config list.
     *
     * @param historyConfigList the history config list
     */
    public void setHistoryConfigList(Map<String, Config> historyConfigList) {
        this.historyConfigList = historyConfigList;
    }
}
