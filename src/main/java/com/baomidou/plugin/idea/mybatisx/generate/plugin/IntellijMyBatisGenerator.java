package com.baomidou.plugin.idea.mybatisx.generate.plugin;


import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijIntrospector;
import com.baomidou.plugin.idea.mybatisx.generate.plugin.helper.IntellijTableInfo;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedKotlinFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaTypeResolver;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.codegen.RootClassInfo;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.MergeConstants;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.exception.InvalidConfigurationException;
import org.mybatis.generator.exception.ShellException;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.mybatis.generator.internal.NullProgressCallback;
import org.mybatis.generator.internal.ObjectFactory;
import org.mybatis.generator.internal.XmlFileMergerJaxp;
import org.mybatis.generator.internal.util.ClassloaderUtility;
import org.mybatis.generator.internal.util.StringUtility;
import org.mybatis.generator.internal.util.messages.Messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class IntellijMyBatisGenerator {
    private Configuration configuration;
    private ShellCallback shellCallback;
    private List<GeneratedJavaFile> generatedJavaFiles = new ArrayList();
    private List<GeneratedXmlFile> generatedXmlFiles = new ArrayList();
    private List<GeneratedKotlinFile> generatedKotlinFiles = new ArrayList();
    private List<String> warnings;
    private Set<String> projects = new HashSet();

    public IntellijMyBatisGenerator(Configuration configuration, ShellCallback shellCallback, List<String> warnings) throws InvalidConfigurationException {
        if (configuration == null) {
            throw new IllegalArgumentException(Messages.getString("RuntimeError.2"));
        } else {
            this.configuration = configuration;
            if (shellCallback == null) {
                this.shellCallback = new DefaultShellCallback(false);
            } else {
                this.shellCallback = shellCallback;
            }

            if (warnings == null) {
                this.warnings = new ArrayList<>();
            } else {
                this.warnings = warnings;
            }

            this.configuration.validate();
        }
    }

    public void generate(ProgressCallback callback, IntellijTableInfo tableInfo) throws SQLException, IOException, InterruptedException {
        this.generate(callback, null, null, true, tableInfo);
    }

    public void generate(ProgressCallback callback, Set<String> contextIds, IntellijTableInfo tableInfo) throws SQLException, IOException, InterruptedException {
        this.generate(callback, contextIds, null, true, tableInfo);
    }

    public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames, IntellijTableInfo tableInfo) throws SQLException, IOException, InterruptedException {
        this.generate(callback, contextIds, fullyQualifiedTableNames, true, tableInfo);
    }

    public void generate(ProgressCallback callback, Set<String> contextIds, Set<String> fullyQualifiedTableNames, boolean writeFiles, IntellijTableInfo tableInfo) throws SQLException, IOException, InterruptedException {
        if (callback == null) {
            callback = new NullProgressCallback();
        }

        this.generatedJavaFiles.clear();
        this.generatedXmlFiles.clear();
        ObjectFactory.reset();
        RootClassInfo.reset();
        List<Context> contextsToRun;
        if (contextIds != null && !contextIds.isEmpty()) {
            contextsToRun = new ArrayList<>();

            for (Context contextItem : this.configuration.getContexts()) {
                if (contextIds.contains(contextItem.getId())) {
                    contextsToRun.add(contextItem);
                }
            }
        } else {
            contextsToRun = this.configuration.getContexts();
        }

        if (!this.configuration.getClassPathEntries().isEmpty()) {
            ClassLoader classLoader = ClassloaderUtility.getCustomClassloader(this.configuration.getClassPathEntries());
            ObjectFactory.addExternalClassLoader(classLoader);
        }

        int totalSteps = 0;


        for (Context contextItem : contextsToRun) {
            totalSteps += contextItem.getIntrospectionSteps();
        }

        callback.introspectionStarted(totalSteps);

        for (Context contextItem : contextsToRun) {
            introspectIntellijTables(contextItem, callback, this.warnings, fullyQualifiedTableNames, tableInfo);
        }

        totalSteps = 0;

        for (Context contextItem : contextsToRun) {
            totalSteps += contextItem.getGenerationSteps();
        }

        callback.generationStarted(totalSteps);

        for (Context contextItem : contextsToRun) {
            contextItem.generateFiles(callback, this.generatedJavaFiles, this.generatedXmlFiles, this.generatedKotlinFiles, this.warnings);
        }

        if (writeFiles) {
            callback.saveStarted(this.generatedXmlFiles.size() + this.generatedJavaFiles.size());

            for (GeneratedXmlFile gxf : generatedXmlFiles) {
                this.projects.add(gxf.getTargetProject());
                this.writeGeneratedXmlFile(gxf, callback);
            }

            for (GeneratedJavaFile gjf : generatedJavaFiles) {
                this.projects.add(gjf.getTargetProject());
                this.writeGeneratedJavaFile(gjf, callback);
            }

            for (GeneratedKotlinFile gkf : generatedKotlinFiles) {
                this.projects.add(gkf.getTargetProject());
                this.writeGeneratedKotlinFile(gkf, callback);
            }

            for (String project : projects) {
                this.shellCallback.refreshProject(project);
            }
        }

        callback.done();
    }


    private void introspectIntellijTables(Context context,
                                          ProgressCallback callback,
                                          List<String> warnings,
                                          Set<String> fullyQualifiedTableNames,
                                          IntellijTableInfo tableInfo) throws SQLException, InterruptedException {
        JavaTypeResolver javaTypeResolver = ObjectFactory.createJavaTypeResolver(context, warnings);
        callback.startTask(Messages.getString("Progress.0"));
        IntellijIntrospector databaseIntrospector = new IntellijIntrospector(context, javaTypeResolver, warnings, tableInfo);
        Iterator<TableConfiguration> tableConfigurationIterator = context.getTableConfigurations().iterator();


        TableConfiguration tc;
        String tableName;
        do {

            if (!tableConfigurationIterator.hasNext()) {
                return;
            }

            tc = tableConfigurationIterator.next();
            tableName = StringUtility.composeFullyQualifiedTableName(tc.getCatalog(),
                tc.getSchema(),
                tc.getTableName(),
                '.');
        } while (fullyQualifiedTableNames != null
            && !fullyQualifiedTableNames.isEmpty()
            && !fullyQualifiedTableNames.contains(tableName));

        if (!tc.areAnyStatementsEnabled()) {
            warnings.add(Messages.getString("Warning.0", tableName));
        } else {
            callback.startTask(Messages.getString("Progress.1", tableName));
            List<IntrospectedTable> tables = databaseIntrospector.introspectTables(tc);
            if (tables != null) {
                //TODO 1.4.0 无法通过get获取, 等1.4.1发布后修复
                try {
                    Field introspectedTablesField = Context.class.getDeclaredField("introspectedTables");
                    introspectedTablesField.setAccessible(true);
                    List<IntrospectedTable> introspectedTables = new ArrayList<>();
                    introspectedTablesField.set(context, introspectedTables);
                    introspectedTables.addAll(tables);
                } catch (NoSuchFieldException | IllegalAccessException ignore) {
                }
            }

            callback.checkCancel();
        }


    }

    private void writeGeneratedJavaFile(GeneratedJavaFile gjf, ProgressCallback callback) throws InterruptedException, IOException {
        try {
            File directory = this.shellCallback.getDirectory(gjf.getTargetProject(), gjf.getTargetPackage());
            File targetFile = new File(directory, gjf.getFileName());
            String source;
            if (targetFile.exists()) {
                if (this.shellCallback.isMergeSupported()) {
                    source = this.shellCallback.mergeJavaFile(gjf.getFormattedContent(), targetFile, MergeConstants.getOldElementTags(), gjf.getFileEncoding());
                } else if (this.shellCallback.isOverwriteEnabled()) {
                    source = gjf.getFormattedContent();
                    this.warnings.add(Messages.getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gjf.getFormattedContent();
                    targetFile = this.getUniqueFileName(directory, gjf.getFileName());
                    this.warnings.add(Messages.getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gjf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(Messages.getString("Progress.15", targetFile.getName()));
            this.writeFile(targetFile, source, gjf.getFileEncoding());
        } catch (ShellException var6) {
            this.warnings.add(var6.getMessage());
        }

    }

    private void writeGeneratedKotlinFile(GeneratedKotlinFile gkf, ProgressCallback callback) throws InterruptedException, IOException {
        try {
            File directory = this.shellCallback.getDirectory(gkf.getTargetProject(), gkf.getTargetPackage());
            File targetFile = new File(directory, gkf.getFileName());
            String source;
            if (targetFile.exists()) {
                if (this.shellCallback.isOverwriteEnabled()) {
                    source = gkf.getFormattedContent();
                    this.warnings.add(Messages.getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gkf.getFormattedContent();
                    targetFile = this.getUniqueFileName(directory, gkf.getFileName());
                    this.warnings.add(Messages.getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gkf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(Messages.getString("Progress.15", targetFile.getName()));
            this.writeFile(targetFile, source, gkf.getFileEncoding());
        } catch (ShellException var6) {
            this.warnings.add(var6.getMessage());
        }

    }

    private void writeGeneratedXmlFile(GeneratedXmlFile gxf, ProgressCallback callback) throws InterruptedException, IOException {
        try {
            File directory = this.shellCallback.getDirectory(gxf.getTargetProject(), gxf.getTargetPackage());
            File targetFile = new File(directory, gxf.getFileName());
            String source;
            if (targetFile.exists()) {
                if (gxf.isMergeable()) {
                    source = XmlFileMergerJaxp.getMergedSource(gxf, targetFile);
                } else if (this.shellCallback.isOverwriteEnabled()) {
                    source = gxf.getFormattedContent();
                    this.warnings.add(Messages.getString("Warning.11", targetFile.getAbsolutePath()));
                } else {
                    source = gxf.getFormattedContent();
                    targetFile = this.getUniqueFileName(directory, gxf.getFileName());
                    this.warnings.add(Messages.getString("Warning.2", targetFile.getAbsolutePath()));
                }
            } else {
                source = gxf.getFormattedContent();
            }

            callback.checkCancel();
            callback.startTask(Messages.getString("Progress.15", targetFile.getName()));
            this.writeFile(targetFile, source, "UTF-8");
        } catch (ShellException var6) {
            this.warnings.add(var6.getMessage());
        }

    }

    private void writeFile(File file, String content, String fileEncoding) throws IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        OutputStreamWriter osw;
        if (fileEncoding == null) {
            osw = new OutputStreamWriter(fos);
        } else {
            osw = new OutputStreamWriter(fos, fileEncoding);
        }

        BufferedWriter bw = new BufferedWriter(osw);
        Throwable throwable = null;

        try {
            bw.write(content);
        } catch (Throwable var16) {
            throwable = var16;
            throw var16;
        } finally {
            if (throwable != null) {
                try {
                    bw.close();
                } catch (Throwable cl) {
                    throwable.addSuppressed(cl);
                }
            } else {
                bw.close();
            }

        }

    }

    private File getUniqueFileName(File directory, String fileName) {
        File answer = null;
        StringBuilder sb = new StringBuilder();

        for (int i = 1; i < 1000; ++i) {
            sb.setLength(0);
            sb.append(fileName);
            sb.append('.');
            sb.append(i);
            File testFile = new File(directory, sb.toString());
            if (!testFile.exists()) {
                answer = testFile;
                break;
            }
        }

        if (answer == null) {
            throw new RuntimeException(Messages.getString("RuntimeError.3", directory.getAbsolutePath()));
        } else {
            return answer;
        }
    }

    public List<GeneratedJavaFile> getGeneratedJavaFiles() {
        return this.generatedJavaFiles;
    }

    public List<GeneratedKotlinFile> getGeneratedKotlinFiles() {
        return this.generatedKotlinFiles;
    }

    public List<GeneratedXmlFile> getGeneratedXmlFiles() {
        return this.generatedXmlFiles;
    }
}
