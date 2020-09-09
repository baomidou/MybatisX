package com.baomidou.plugin.idea.mybatisx.intention;

import com.baomidou.plugin.idea.mybatisx.dom.model.Mapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.ConditionFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.common.iftest.NeverContainsFieldWrapper;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.TypeDescriptor;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolver;
import com.baomidou.plugin.idea.mybatisx.smartjpa.component.mapping.EntityMappingResolverFactory;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.CommonGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.MybatisXmlGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.operate.generate.PlatformGenerator;
import com.baomidou.plugin.idea.mybatisx.smartjpa.util.Importer;
import com.baomidou.plugin.idea.mybatisx.util.MapperUtils;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.database.Dbms;
import com.intellij.database.model.DasTable;
import com.intellij.database.psi.DbDataSource;
import com.intellij.database.psi.DbPsiFacade;
import com.intellij.database.util.DasUtil;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiTypeElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.sql.dialects.SqlLanguageDialect;
import com.intellij.sql.psi.SqlPsiFacade;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.JBIterable;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * 在mapper类中通过名字生成方法和xml内容
 *
 * @author ls9527
 */
public class GenerateMapperMethodSmartJpaAction extends GenerateMapperMethodSmartJpaAdvanceAction {


    private static final Logger logger = Logger.getInstance(GenerateMapperMethodSmartJpaAction.class);


    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Generate Mybatis Sql";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Statement Complete";
    }


    @Override
    protected ConditionFieldWrapper getConditionFieldWrapper(@NotNull Project project, PlatformGenerator platformGenerator) {
        return new NeverContainsFieldWrapper();
    }
}
