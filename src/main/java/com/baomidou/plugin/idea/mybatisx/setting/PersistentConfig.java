package com.baomidou.plugin.idea.mybatisx.setting;


import com.baomidou.plugin.idea.mybatisx.model.Config;
import com.baomidou.plugin.idea.mybatisx.model.User;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.util.Map;
import org.jetbrains.annotations.Nullable;


/**
 * 配置持久化
 */
@State(name = "PersistentConfig", storages = {@Storage("free-mybatis-generator-config.xml")})
public class PersistentConfig implements PersistentStateComponent<PersistentConfig> {

    private Map<String, Config> initConfig;
    private Map<String, User> users;
    private Map<String, Config> historyConfigList;

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


    public Map<String, Config> getInitConfig() {
        return initConfig;
    }

    public void setInitConfig(Map<String, Config> initConfig) {
        this.initConfig = initConfig;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public void setUsers(Map<String, User> users) {
        this.users = users;
    }

    public Map<String, Config> getHistoryConfigList() {
        return historyConfigList;
    }

    public void setHistoryConfigList(Map<String, Config> historyConfigList) {
        this.historyConfigList = historyConfigList;
    }
}
