package com.baomidou.plugin.idea.mybatisx.setting;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.baomidou.plugin.idea.mybatisx.generate.GenerateModel;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.DELETE_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.INSERT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.SELECT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.UPDATE_GENERATOR;

/**
 * @author yanglin
 */
public class MybatisConfigurable implements SearchableConfigurable {

    private MybatisSetting mybatisSetting;

    private MybatisSettingForm mybatisSettingForm;

    private String separator = ";";

    private Splitter splitter = Splitter.on(separator).omitEmptyStrings().trimResults();

    private Joiner joiner = Joiner.on(separator);

    public MybatisConfigurable() {
        mybatisSetting = MybatisSetting.getInstance();
    }

    @Override
    public String getId() {
        return "Mybatis";
    }

    @Override
    public Runnable enableSearch(String option) {
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return getId();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (null == mybatisSettingForm) {
            this.mybatisSettingForm = new MybatisSettingForm();
        }
        return mybatisSettingForm.mainPanel;
    }

    @Override
    public boolean isModified() {
        return mybatisSetting.getStatementGenerateModel().getIdentifier() != mybatisSettingForm.modelComboBox.getSelectedIndex()
                || !joiner.join(INSERT_GENERATOR.getPatterns()).equals(mybatisSettingForm.insertPatternTextField.getText())
                || !joiner.join(DELETE_GENERATOR.getPatterns()).equals(mybatisSettingForm.deletePatternTextField.getText())
                || !joiner.join(UPDATE_GENERATOR.getPatterns()).equals(mybatisSettingForm.updatePatternTextField.getText())
                || !joiner.join(SELECT_GENERATOR.getPatterns()).equals(mybatisSettingForm.selectPatternTextField.getText());
    }

    @Override
    public void apply() throws ConfigurationException {
        mybatisSetting.setStatementGenerateModel(GenerateModel.getInstance(mybatisSettingForm.modelComboBox.getSelectedIndex()));
        INSERT_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.insertPatternTextField.getText())));
        DELETE_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.deletePatternTextField.getText())));
        UPDATE_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.updatePatternTextField.getText())));
        SELECT_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.selectPatternTextField.getText())));
    }

    @Override
    public void reset() {
        mybatisSettingForm.modelComboBox.setSelectedIndex(mybatisSetting.getStatementGenerateModel().getIdentifier());
        mybatisSettingForm.insertPatternTextField.setText(joiner.join(INSERT_GENERATOR.getPatterns()));
        mybatisSettingForm.deletePatternTextField.setText(joiner.join(DELETE_GENERATOR.getPatterns()));
        mybatisSettingForm.updatePatternTextField.setText(joiner.join(UPDATE_GENERATOR.getPatterns()));
        mybatisSettingForm.selectPatternTextField.setText(joiner.join(SELECT_GENERATOR.getPatterns()));
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingForm.mainPanel = null;
    }

}
