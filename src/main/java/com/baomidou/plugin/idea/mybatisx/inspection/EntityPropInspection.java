package com.baomidou.plugin.idea.mybatisx.inspection;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiManager;
import com.intellij.spring.contexts.model.LocalModel;
import com.intellij.spring.model.CommonSpringBean;
import com.intellij.spring.model.custom.CustomLocalComponentsDiscoverer;
import com.intellij.spring.model.jam.stereotype.CustomSpringComponent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;

/**
 * 实体属性检测工具
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018/08/04 20:35
 */
public class EntityPropInspection extends CustomLocalComponentsDiscoverer {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(EntityPropInspection.class);

    @NotNull
    @Override
    public Collection<CommonSpringBean> getCustomComponents(@NotNull LocalModel localModel) {
        localModel.getModule();
        VirtualFile virtualFile = localModel.getModule().getModuleFile().getParent();
        VirtualFile daoFile = virtualFile.findFileByRelativePath("src/main/java/com/sjhy/gadget/service/dao/UserInfoDao.java");
        PsiJavaFile psiDaoFile = (PsiJavaFile) PsiManager.getInstance(localModel.getModule().getProject()).findFile(daoFile);
        PsiClass psiClass = psiDaoFile.getClasses()[0];
        return Collections.singleton(new CustomSpringComponent(psiClass));
    }
}
