package com.baomidou.plugin.idea.mybatisx.setting;

import com.baomidou.plugin.idea.mybatisx.generate.GenerateModel;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.DELETE_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.INSERT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.SELECT_GENERATOR;
import static com.baomidou.plugin.idea.mybatisx.generate.AbstractStatementGenerator.UPDATE_GENERATOR;

/**
 * The type Mybatis configurable.
 *
 * @author yanglin
 */
public class MybatisConfigurable implements SearchableConfigurable {

    private MybatisXSettings mybatisXSettings;

    private MybatisSettingForm mybatisSettingForm;

    private String separator = ";";

    private Splitter splitter = Splitter.on(separator).omitEmptyStrings().trimResults();

    private Joiner joiner = Joiner.on(separator);

    /**
     * Instantiates a new Mybatis configurable.
     */
    public MybatisConfigurable() {
        mybatisXSettings = MybatisXSettings.getInstance();
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
        return mybatisXSettings.getStatementGenerateModel().getIdentifier() != mybatisSettingForm.modelComboBox.getSelectedIndex()
                || !joiner.join(INSERT_GENERATOR.getPatterns()).equals(mybatisSettingForm.insertPatternTextField.getText())
                || !joiner.join(DELETE_GENERATOR.getPatterns()).equals(mybatisSettingForm.deletePatternTextField.getText())
                || !joiner.join(UPDATE_GENERATOR.getPatterns()).equals(mybatisSettingForm.updatePatternTextField.getText())
                || !joiner.join(SELECT_GENERATOR.getPatterns()).equals(mybatisSettingForm.selectPatternTextField.getText())
         || (mybatisSettingForm.defaultRadioButton.isSelected()?
            MybatisXSettings.MapperIcon.BIRD.name().equals(mybatisXSettings.getMapperIcon())
            : MybatisXSettings.MapperIcon.DEFAULT.name().equals(mybatisXSettings.getMapperIcon()));
    }

    @Override
    public void apply() throws ConfigurationException {
        mybatisXSettings.setStatementGenerateModel(GenerateModel.getInstance(mybatisSettingForm.modelComboBox.getSelectedIndex()));
        INSERT_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.insertPatternTextField.getText())));
        DELETE_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.deletePatternTextField.getText())));
        UPDATE_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.updatePatternTextField.getText())));
        SELECT_GENERATOR.setPatterns(Sets.newHashSet(splitter.split(mybatisSettingForm.selectPatternTextField.getText())));
        String mapperIcon = mybatisSettingForm.defaultRadioButton.isSelected() ?
            MybatisXSettings.MapperIcon.DEFAULT.name() :
            MybatisXSettings.MapperIcon.BIRD.name();
        mybatisXSettings.setMapperIcon(mapperIcon);
    }

    @Override
    public void reset() {
        mybatisSettingForm.modelComboBox.setSelectedIndex(mybatisXSettings.getStatementGenerateModel().getIdentifier());
        mybatisSettingForm.insertPatternTextField.setText(joiner.join(INSERT_GENERATOR.getPatterns()));
        mybatisSettingForm.deletePatternTextField.setText(joiner.join(DELETE_GENERATOR.getPatterns()));
        mybatisSettingForm.updatePatternTextField.setText(joiner.join(UPDATE_GENERATOR.getPatterns()));
        mybatisSettingForm.selectPatternTextField.setText(joiner.join(SELECT_GENERATOR.getPatterns()));

        String mapperIcon = mybatisXSettings.getMapperIcon();
        if(mapperIcon==null){
            mapperIcon = MybatisXSettings.MapperIcon.BIRD.name();
        }
        if(MybatisXSettings.MapperIcon.BIRD.name().equals(mapperIcon)){
            mybatisSettingForm.birdRadioButton.setSelected(true);
        }else{
            mybatisSettingForm.defaultRadioButton.setSelected(true);
        }
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingForm.mainPanel = null;
    }

}
