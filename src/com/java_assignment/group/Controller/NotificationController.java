package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.Notification;
import com.java_assignment.group.Model.TxtModelRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NotificationController {
    private TxtModelRepository<Notification> notificationRepository;

    public NotificationController() throws IOException {
        notificationRepository = new TxtModelRepository<>("Data/notification.txt", Notification::fromCsv, Notification::toCsv);
    }

    /**
     * 指定したユーザーの通知を取得
     */
    public List<Notification> getNotificationsByUser(String baseUserId) throws IOException {
        List<Notification> allNotifications = notificationRepository.readAll();
        return allNotifications.stream()
                .filter(notification -> notification.getBaseUserId().equals(baseUserId))
                .collect(Collectors.toList());
    }

    /**
     * 指定した通知IDの通知を取得
     */
    public Notification getNotificationById(String notificationId) {
        try {
            for (Notification notification : notificationRepository.readAll()) {
                if (notification.getId().equals(notificationId)) {
                    return notification;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 新しい通知を追加
     */
    public boolean addNotification(Notification notification) {
        try {
            List<Notification> notifications = notificationRepository.readAll();
            String notificationId = UUID.randomUUID().toString();
            notification.setId(notificationId);
            notification.setCreatedAt(LocalDateTime.now());
            notifications.add(notification);
            notificationRepository.writeAll(notifications, false);

            System.out.println("New Notification was sent");
            System.out.println("To: "+notification.getBaseUserId());
            System.out.println("Title: "+notification.getTitle());
            System.out.println("Description: "+notification.getDescription());
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 通知の編集
     */
    public boolean updateNotification(Notification updatedNotification) {
        try {
            List<Notification> notifications = notificationRepository.readAll();
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getId().equals(updatedNotification.getId())) {
                    notifications.set(i, updatedNotification);
                    break;
                }
            }
            notificationRepository.writeAll(notifications, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 通知の削除
     */
    public boolean deleteNotification(String notificationId) {
        try {
            List<Notification> notifications = notificationRepository.readAll();
            notifications = notifications.stream()
                    .filter(notification -> !notification.getId().equals(notificationId))
                    .collect(Collectors.toList());
            notificationRepository.writeAll(notifications, false);
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
