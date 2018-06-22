package com.baomidou.plugin.idea.mybatisx.ui;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopup;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author yanglin
 */
public final class UiComponentFacade {

    private Project project;

    private FileEditorManager fileEditorManager;

    private UiComponentFacade(Project project) {
        this.project = project;
        this.fileEditorManager = FileEditorManager.getInstance(project);
    }

    public static UiComponentFacade getInstance(@NotNull Project project) {
        return new UiComponentFacade(project);
    }

    public VirtualFile showSingleFolderSelectionDialog(@NotNull String title,
                                                       @Nullable VirtualFile toSelect,
                                                       @Nullable VirtualFile... roots) {
        final FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
        descriptor.setTitle(title);
        if (null != roots) {
            descriptor.setRoots(roots);
        }
        return FileChooser.chooseFile(descriptor, project, toSelect);
    }

    public JBPopup showListPopupWithSingleClickable(@NotNull String popupTitle,
                                                    @NotNull ListSelectionListener popupListener,
                                                    @NotNull String clickableTitle,
                                                    @Nullable final ClickableListener clickableListener,
                                                    @NotNull Object[] objs) {
        PopupChooserBuilder builder = createListPopupBuilder(popupTitle, popupListener, objs);
        JBCheckBox checkBox = new JBCheckBox(clickableTitle);
        builder.setSouthComponent(checkBox);
        final JBPopup popup = builder.createPopup();
        if (null != clickableListener) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    clickableListener.clicked();
                }
            };
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    popup.dispose();
                    setActionForExecutableListener(runnable, clickableListener);
                }
            });
        }
        setPositionForShown(popup);
        return popup;
    }

    public JBPopup showListPopup(@NotNull String title,
                                 @Nullable final ListSelectionListener listener,
                                 @NotNull Object[] objs) {
        PopupChooserBuilder builder = createListPopupBuilder(title, listener, objs);
        JBPopup popup = builder.createPopup();
        setPositionForShown(popup);
        return popup;
    }

    private void setPositionForShown(JBPopup popup) {
        Editor editor = fileEditorManager.getSelectedTextEditor();
        if (null != editor) {
            popup.showInBestPositionFor(editor);
        } else {
            popup.showCenteredInCurrentWindow(project);
        }
    }

    private void setActionForExecutableListener(Runnable runnable, ExecutableListener listener) {
        final Application application = ApplicationManager.getApplication();
        if (listener.isWriteAction()) {
            application.runWriteAction(runnable);
        } else {
            application.runReadAction(runnable);
        }
    }

    public PopupChooserBuilder createListPopupBuilder(@NotNull String title,
                                                      @Nullable final ListSelectionListener listener,
                                                      @NotNull Object... objs) {
        final JBList list = new JBList(objs);
        PopupChooserBuilder builder = new PopupChooserBuilder(list);
        builder.setTitle(title);
        if (null != listener) {
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    listener.selected(list.getSelectedIndex());
                }
            };
            builder.setItemChoosenCallback(new Runnable() {
                @Override
                public void run() {
                    setActionForExecutableListener(runnable, listener);
                }
            });
        }
        return builder;
    }

}
