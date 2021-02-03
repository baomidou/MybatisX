package com.baomidou.plugin.idea.mybatisx.setting;

import com.intellij.openapi.options.SearchableConfigurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * The type Mybatis configurable.
 *
 * @author yanglin
 */
public class MybatisConfigurable implements SearchableConfigurable {

    private MybatisXSettings mybatisXSettings;

    private MybatisSettingForm mybatisSettingForm;

    /**
     * Instantiates a new Mybatis configurable.
     */
    public MybatisConfigurable() {
        mybatisXSettings = MybatisXSettings.getInstance();
    }

    @Override
    public String getId() {
        return "MybatisX";
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
        return  !mybatisXSettings.getInsertGenerator().equals(mybatisSettingForm.insertPatternTextField.getText())
            || !mybatisXSettings.getDeleteGenerator().equals(mybatisSettingForm.deletePatternTextField.getText())
            || !mybatisXSettings.getUpdateGenerator().equals(mybatisSettingForm.updatePatternTextField.getText())
            || !mybatisXSettings.getSelectGenerator().equals(mybatisSettingForm.selectPatternTextField.getText())
            || (mybatisSettingForm.defaultRadioButton.isSelected() ?
            MybatisXSettings.MapperIcon.BIRD.name().equals(mybatisXSettings.getMapperIcon())
            : MybatisXSettings.MapperIcon.DEFAULT.name().equals(mybatisXSettings.getMapperIcon()));
    }

    @Override
    public void apply() {
        mybatisXSettings.setInsertGenerator(mybatisSettingForm.insertPatternTextField.getText());
        mybatisXSettings.setDeleteGenerator(mybatisSettingForm.deletePatternTextField.getText());
        mybatisXSettings.setUpdateGenerator(mybatisSettingForm.updatePatternTextField.getText());
        mybatisXSettings.setSelectGenerator(mybatisSettingForm.selectPatternTextField.getText());

        String mapperIcon = mybatisSettingForm.defaultRadioButton.isSelected() ?
            MybatisXSettings.MapperIcon.DEFAULT.name() :
            MybatisXSettings.MapperIcon.BIRD.name();
        mybatisXSettings.setMapperIcon(mapperIcon);
    }

    @Override
    public void reset() {
        mybatisSettingForm.insertPatternTextField.setText(mybatisXSettings.getInsertGenerator());
        mybatisSettingForm.deletePatternTextField.setText(mybatisXSettings.getDeleteGenerator());
        mybatisSettingForm.updatePatternTextField.setText(mybatisXSettings.getUpdateGenerator());
        mybatisSettingForm.selectPatternTextField.setText(mybatisXSettings.getSelectGenerator());

        JRadioButton jRadioButton =  mybatisSettingForm.birdRadioButton;
        if (MybatisXSettings.MapperIcon.DEFAULT.name().equals(mybatisXSettings.getMapperIcon())) {
            jRadioButton = mybatisSettingForm.defaultRadioButton;
        }
        jRadioButton.setSelected(true);
    }

    @Override
    public void disposeUIResources() {
        mybatisSettingForm.mainPanel = null;
    }

}
