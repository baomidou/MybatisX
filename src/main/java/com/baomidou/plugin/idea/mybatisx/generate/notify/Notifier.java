package com.baomidou.plugin.idea.mybatisx.generate.notify;

import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nullable;

public class Notifier {
    private static final String GENERATE_CODE = "MybatisX Notification";
    private static final NotificationGroup NOTIFICATION_GROUP =
        new NotificationGroup(GENERATE_CODE, NotificationDisplayType.BALLOON, true);

    public static void notifyInformation(@Nullable Project project, String title, String content) {
        NOTIFICATION_GROUP.createNotification(title, content, NotificationType.INFORMATION, null)
           .notify(project);
    }

}
