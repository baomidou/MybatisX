package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.SpringManager;

public class SpringManagerAdaptor {
    private SpringManager springManager;

    public SpringManagerAdaptor(Project project) {
        this.springManager = SpringManager.getInstance(project);
    }

    public Iterable<? extends CommonSpringModel> getModels(Module module) {
        return springManager.getCombinedModel(module).getRelatedModels();
    }
}
