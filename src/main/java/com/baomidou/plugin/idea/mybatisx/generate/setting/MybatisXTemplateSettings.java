package com.baomidou.plugin.idea.mybatisx.generate.setting;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.intellij.ui.treeStructure.Tree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MybatisXTemplateSettings {
    private JTextField packageNameTextField;
    private JTextField fieldNameTextField;
    private JPanel rootPanel;
    private JPanel configPanel;
    private JTextField suffixTextField;
    private JTextField encodingTextField;
    private JTextArea templateText;
    private JTree configTree;
    private JTextField basePathTextField;
    private JPanel treeConfigPanel;


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void loadBySettings(TemplatesSettings templatesSettings) {
        TemplateContext templateContext = templatesSettings.getTemplateConfigs();
        // 第一个版本只有一个不可更改的配置, 这里直接取默认就可以了
        configTree.addTreeSelectionListener(new MyTreeSelectionListener(templateContext));

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) configTree.getModel().getRoot();
        root.removeAllChildren();

        Map<String, List<TemplateSettingDTO>> templateSettingMap = templateContext.getTemplateSettingMap();
        for (Map.Entry<String, List<TemplateSettingDTO>> stringListEntry : templateSettingMap.entrySet()) {
            DefaultMutableTreeNode theme = new DefaultMutableTreeNode(stringListEntry.getKey());
            root.add(theme);
            for (TemplateSettingDTO templateSettingDTO : stringListEntry.getValue()) {
                DefaultMutableTreeNode template = new DefaultMutableTreeNode(templateSettingDTO.getConfigName());
                theme.add(template);
            }

        }
        configTree.updateUI();
        expandTree(configTree);
    }

    public static void expandTree(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();
        expandTree(tree, new TreePath(root));
    }

    public static void expandTree(JTree tree, TreePath path) {
        TreeNode node = (TreeNode) path.getLastPathComponent();

        // Go to leaf
        if (node.getChildCount() > 0) {
            Enumeration<TreeNode> children = node.children();

            while (children.hasMoreElements()) {
                TreeNode n = children.nextElement();
                TreePath newPath = path.pathByAddingChild(n);
                expandTree(tree, newPath);
            }
        }

        tree.expandPath(path);
    }

    public void apply(TemplatesSettings templatesSettings) {
        // 第一个版本只有一个不可更改的配置, 这里直接取默认就可以了
        DefaultMutableTreeNode lastSelectedPathComponent = (DefaultMutableTreeNode) configTree.getLastSelectedPathComponent();
        // 节点名称
        Object userObject = lastSelectedPathComponent.getUserObject();
        String key = userObject.toString();
        if (key == null) {
            return;
        }
        TemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
        Map<String, List<TemplateSettingDTO>> templateSettingMap = templateConfigs.getTemplateSettingMap();
        List<TemplateSettingDTO> templateSettingDTOS = templateSettingMap.get(TemplatesSettings.DEFAULT_TEMPLATE_NAME);

        TemplateSettingDTO templateSettingDTO = new TemplateSettingDTO();
        templateSettingDTO.setFileName(fieldNameTextField.getText());
        templateSettingDTO.setSuffix(suffixTextField.getText());
        templateSettingDTO.setPackageName(packageNameTextField.getText());
        templateSettingDTO.setBasePath(basePathTextField.getText());
        templateSettingDTO.setConfigName(userObject.toString());
        templateSettingDTO.setEncoding(encodingTextField.getText());
        templateSettingDTO.setTemplateText(templateText.getText());


        List<TemplateSettingDTO> replacedSettings = templateSettingDTOS.stream().map(x -> {
            if (x.getConfigName().equalsIgnoreCase(key)) {
                return templateSettingDTO;
            }
            return x;
        }).collect(Collectors.toList());


        templateSettingMap.put(TemplatesSettings.DEFAULT_TEMPLATE_NAME, replacedSettings);

        templatesSettings.setTemplateConfigs(templateConfigs);
    }

    public boolean isModified() {
        return true;
    }


    private class MyTreeSelectionListener implements TreeSelectionListener {
        private TemplateContext templateContext;

        public MyTreeSelectionListener(TemplateContext templateContext) {
            this.templateContext = templateContext;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            TreePath path = e.getPath();
            String text = path.getLastPathComponent().toString();
            TemplateSettingDTO foundDto = new TemplateSettingDTO();

            Map<String, List<TemplateSettingDTO>> templateSettingMap = templateContext.getTemplateSettingMap();
            List<TemplateSettingDTO> templateSettingDTOS = templateSettingMap.get(TemplatesSettings.DEFAULT_TEMPLATE_NAME);
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
            basePathTextField.setText(foundDto.getBasePath());
        }

    }
}
