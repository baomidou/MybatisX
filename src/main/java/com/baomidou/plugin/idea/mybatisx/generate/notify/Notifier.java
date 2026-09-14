package com.baomidou.plugin.idea.mybatisx.generate.notify;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class Notifier {
    private static final String GENERATE_CODE = "MybatisX Notification";

    public static void notifyInformation(@Nullable Project project, String title, String content) {
        NotificationGroup notificationGroup = NotificationGroupManager.getInstance().getNotificationGroup(GENERATE_CODE);
        notificationGroup.createNotification(title, content, NotificationType.INFORMATION)
            .notify(project);
    }
}
