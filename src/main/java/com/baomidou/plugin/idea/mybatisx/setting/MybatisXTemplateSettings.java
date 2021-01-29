package com.baomidou.plugin.idea.mybatisx.setting;

import com.baomidou.plugin.idea.mybatisx.setting.template.TemplateSettingDTO;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.util.List;
import java.util.Map;

public class MybatisXTemplateSettings {
    private JTextField packageNameTextField;
    private JTextField fieldNameTextField;
    private JPanel rootPanel;
    private JPanel configPanel;
    private JTextField suffixTextField;
    private JTextField encodingTextField;
    private JTextArea templateText;
    private JTree configTree;


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void loadSettings(Map<String, List<TemplateSettingDTO>> templateSettingDTOMap) {
        // 第一个版本只有一个不可更改的配置, 这里直接取第一个就可以了
        List<TemplateSettingDTO> templateSettingDTOS = templateSettingDTOMap.values().iterator().next();
        configTree.addTreeSelectionListener(new MyTreeSelectionListener(templateSettingDTOS));

        DefaultTreeModel model = (DefaultTreeModel) configTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) configTree.getModel().getRoot();
        root.removeAllChildren();

        for (Map.Entry<String, List<TemplateSettingDTO>> stringListEntry : templateSettingDTOMap.entrySet()) {
            DefaultMutableTreeNode theme = new DefaultMutableTreeNode(stringListEntry.getKey());
            model.insertNodeInto(theme, root, root.getChildCount());

            for (TemplateSettingDTO templateSettingDTO : stringListEntry.getValue()) {
                DefaultMutableTreeNode template = new DefaultMutableTreeNode(templateSettingDTO.getConfigName());
                theme.add(template);
            }
        }
        configTree.updateUI();
    }



    private class MyTreeSelectionListener implements TreeSelectionListener {
        private final List<TemplateSettingDTO> templateSettingDTOS;

        public MyTreeSelectionListener(List<TemplateSettingDTO> templateSettingDTOS) {
            this.templateSettingDTOS = templateSettingDTOS;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            String text = path.getLastPathComponent().toString();
            TemplateSettingDTO foundDto = new TemplateSettingDTO();
            for (TemplateSettingDTO templateSettingDTO : templateSettingDTOS) {
                if (templateSettingDTO.getConfigName().equalsIgnoreCase(text)) {
                    foundDto = templateSettingDTO;
                    break;
                }
            }

            fieldNameTextField.setText(foundDto.getFileName());
            suffixTextField.setText(foundDto.getSuffix());
            packageNameTextField.setText(foundDto.getPackageName());
            encodingTextField.setText(foundDto.getEncoding());
            templateText.setText(foundDto.getTemplateText());
        }

    }
}
