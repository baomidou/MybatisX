package com.baomidou.plugin.idea.mybatisx.generate.setting;

import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateContext;
import com.baomidou.plugin.idea.mybatisx.generate.dto.TemplateSettingDTO;
import com.intellij.debugger.DebuggerBundle;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
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
    private JPanel treePanel;
    private JPanel treeConfigPanel;


    public JPanel getRootPanel() {
        return rootPanel;
    }

    public void loadBySettings(TemplatesSettings templatesSettings) {
        TemplateContext templateContext = templatesSettings.getTemplateConfigs();
        // 第一个版本只有一个不可更改的配置, 这里直接取默认就可以了
        Map<String, List<TemplateSettingDTO>> templateSettingMap = templatesSettings.getTemplateSettingMap();
        configTree.addTreeSelectionListener(new MyTreeSelectionListener(templateSettingMap));

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) configTree.getModel().getRoot();
        root.removeAllChildren();

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

        // 参考: com.intellij.tools.BaseToolsPanel.BaseToolsPanel
        GridConstraints gridConstraints = new GridConstraints();
        gridConstraints.setFill(GridConstraints.FILL_VERTICAL | GridConstraints.ALIGN_LEFT);
        gridConstraints.setHSizePolicy(GridConstraints.SIZEPOLICY_FIXED);
        rootPanel.add(ToolbarDecorator.createDecorator(configTree).setAddAction(new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {

                }
            }).setRemoveAction(new AnActionButtonRunnable() {
                @Override
                public void run(AnActionButton anActionButton) {

                }
            }).addExtraAction(new CopyAction())
                .setPreferredSize(new Dimension(220, -1))
                .createPanel(),
            gridConstraints);

    }

    private class CopyAction extends AnActionButton {
        CopyAction() {
            super(DebuggerBundle.message("button.copy"), DebuggerBundle.message("user.renderers.configurable.button.description.copy"), PlatformIcons.COPY_ICON);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
        }

        @Override
        public void updateButton(@NotNull AnActionEvent e) {
        }
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
        if (lastSelectedPathComponent == null) {
            return;
        }
        // 节点名称
        Object userObject = lastSelectedPathComponent.getUserObject();
        String key = userObject.toString();
        if (key == null) {
            return;
        }
        TemplateContext templateConfigs = templatesSettings.getTemplateConfigs();
        Map<String, List<TemplateSettingDTO>> templateSettingMap = templateConfigs.getTemplateSettingMap();
        String DEFAULT_TEMPLATE_NAME = "mybatis-plus3";
        List<TemplateSettingDTO> templateSettingDTOS = templateSettingMap.get(DEFAULT_TEMPLATE_NAME);

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


        templateSettingMap.put(DEFAULT_TEMPLATE_NAME, replacedSettings);

        templatesSettings.setTemplateConfigs(templateConfigs);
    }

    public boolean isModified() {
        return true;
    }

    private static final Logger logger = LoggerFactory.getLogger(MybatisXTemplateSettings.class);

    private class MyTreeSelectionListener implements TreeSelectionListener {
        private Map<String, List<TemplateSettingDTO>> templateSettingMap;

        public MyTreeSelectionListener(Map<String, List<TemplateSettingDTO>> templateSettingMap) {
            this.templateSettingMap = templateSettingMap;
        }

        @Override
        public void valueChanged(TreeSelectionEvent e) {
            final TreePath oldLeadSelectionPath = e.getOldLeadSelectionPath();
            if (oldLeadSelectionPath == null) {
                return;
            }
            if (oldLeadSelectionPath.getPathCount() != 3) {
                logger.info("路径错误, 无法映射正确的配置");
                return;
            }
            final String templatesName = oldLeadSelectionPath.getParentPath().getLastPathComponent().toString();
            final String templateName = oldLeadSelectionPath.getLastPathComponent().toString();
            TemplateSettingDTO foundDto = new TemplateSettingDTO();


            List<TemplateSettingDTO> templateSettingDTOS = templateSettingMap.get(templatesName);
            if (templateSettingDTOS == null) {
                // 没有找到配置
                logger.info("没有找到配置,templatesName: {}", templatesName);
                return;
            }
            for (TemplateSettingDTO templateSettingDTO : templateSettingDTOS) {
                if (templateSettingDTO.getConfigName().equals(templateName)) {
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
