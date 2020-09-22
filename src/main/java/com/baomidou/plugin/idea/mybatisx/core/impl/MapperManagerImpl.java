package com.baomidou.plugin.idea.mybatisx.core.impl;

import com.baomidou.plugin.idea.mybatisx.core.MapperManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.file.PsiJavaDirectoryFactory;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScopeUtil;
import com.intellij.psi.search.PackageScope;
import com.intellij.spring.facet.SpringFacet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.java.JavaSourceRootType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mapper管理实现
 *
 * @author makejava
 * @version 1.0.0
 * @since 2018 /08/06 10:24
 */
public class MapperManagerImpl implements MapperManager {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(MapperManagerImpl.class);
    /**
     * 项目对象
     */
    private Project project;

    /**
     * 缓存的Mapper类文件
     */
    private Map<Module, List<PsiJavaFile>> mapperClsMap = new ConcurrentHashMap<>();

    /**
     * Instantiates a new Mapper manager.
     *
     * @param project the project
     */
    public MapperManagerImpl(Project project) {
        this.project = project;
        this.init();
    }

    /**
     * 初始化方法
     */
    @Override
    public void init() {
        mapperClsMap.clear();
        if (this.project == null) {
            return;
        }
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            List<PsiJavaFile> javaFileList = new ArrayList<>();

            // 拿到Module中的所有资源文件夹目录
            List<VirtualFile> moduleDir = ModuleRootManager.getInstance(module).getSourceRoots(JavaSourceRootType.SOURCE);


            // 通过module拿到所有的mapper扫描包
            Set<String> packageNameSet = new HashSet<>();
            // 定死一个
            packageNameSet.add("com.sjhy.gadget.service.dao");

            logger.info("扫描到的包：package：{}", packageNameSet);

            // 扫描所有的包，保存扫描到的接口类
            packageNameSet.forEach(packageName -> {
                for (VirtualFile dir : moduleDir) {
                    // 查找包路径
                    VirtualFile packageDir = dir.findFileByRelativePath(packageName.replace(".", "/"));
                    if (packageDir == null || !packageDir.isDirectory()) {
                        continue;
                    }
                    // 获取到包对象
                    PsiPackage psiPackage = JavaDirectoryService.getInstance().getPackage(PsiDirectoryFactory.getInstance(project).createDirectory(packageDir));
                    if (psiPackage == null) {
                        continue;
                    }
                    // 创建搜索作用域
                    PackageScope packageScope = new PackageScope(psiPackage, true, false);
                    // 搜索到所有的java接口
                    Collection<VirtualFile> virtualFiles = FilenameIndex.getAllFilesByExt(project, "java", packageScope);
                    // 转换为PsiJava文件
                    PsiManager psiManager = PsiManager.getInstance(project);
                    virtualFiles.forEach(virtualFile -> {
                        PsiFile psiFile = psiManager.findFile(virtualFile);
                        PsiJavaFile psiJavaFile;
                        if (psiFile instanceof PsiJavaFile) {
                            psiJavaFile = (PsiJavaFile) psiFile;
                        } else {
                            return;
                        }
                        // 添加到结果中
                        javaFileList.add(psiJavaFile);
                    });
                }
            });

            // 将结果put至map中
            mapperClsMap.put(module, javaFileList);
        }





        //TODO 监听创建新文件，或删除文件时同步更新
    }

    /**
     * 通过module获取所有Mapper类文件
     *
     * @param module 模型对象
     * @return Mapper类文件集合
     */
    @Override
    public List<PsiJavaFile> getMapperClsFileByModule(@NotNull Module module) {
        return mapperClsMap.get(module);
    }

    /**
     * 获取项目中所有的Mapper类文件
     *
     * @return Mapper类文件集合
     */
    @Override
    public List<PsiJavaFile> getAllMapperClsFile() {
        List<PsiJavaFile> javaFileList = new ArrayList<>();
        mapperClsMap.forEach((k, v) -> javaFileList.addAll(v));
        return javaFileList;
    }
}
