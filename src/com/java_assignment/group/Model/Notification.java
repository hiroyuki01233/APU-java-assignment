package com.java_assignment.group.Model;

import java.time.LocalDateTime;

public class Notification implements BaseModel {
    private String notificationId;
    private String baseUserId;
    private String title;
    private String description;
    private String pageName;
    private LocalDateTime createdAt;

    public Notification() {}

    public Notification(String notificationId, String baseUserId, String title, String description, String pageName, LocalDateTime createdAt) {
        this.notificationId = notificationId;
        this.baseUserId = baseUserId;
        this.title = title;
        this.description = description;
        this.pageName = pageName;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return notificationId;
    }

    @Override
    public void setId(String id) {
        this.notificationId = id;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPageName() {
        return pageName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * CSV形式にシリアライズ
     */
    public String toCsv() {
        return String.join(",",
                notificationId,
                baseUserId,
                title,
                description,
                pageName,
                createdAt.toString()
        );
    }

    /**
     * CSV文字列からNotificationインスタンスを生成
     */
    public static Notification fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV line for Notification: " + csvLine);
        }
        return new Notification(
                parts[0], parts[1], parts[2], parts[3], parts[4], LocalDateTime.parse(parts[5])
        );
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", baseUserId='" + baseUserId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", pageName='" + pageName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
