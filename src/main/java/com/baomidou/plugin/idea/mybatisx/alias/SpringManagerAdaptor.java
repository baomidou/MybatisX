package com.baomidou.plugin.idea.mybatisx.alias;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.spring.CommonSpringModel;
import com.intellij.spring.SpringManager;

/**
 * The type Spring manager adaptor.
 */
public class SpringManagerAdaptor {
    private SpringManager springManager;

    /**
     * Instantiates a new Spring manager adaptor.
     *
     * @param project the project
     */
    public SpringManagerAdaptor(Project project) {
        this.springManager = SpringManager.getInstance(project);
    }

    /**
     * Gets models.
     *
     * @param module the module
     * @return the models
     */
    public Iterable<? extends CommonSpringModel> getModels(Module module) {
        return springManager.getCombinedModel(module).getRelatedModels();
    }
}
