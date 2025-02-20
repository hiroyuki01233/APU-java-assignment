package com.java_assignment.group.Component;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class FooterPanel extends JPanel {
    private AuthController authController;

    public FooterPanel(MainFrame mainFrame, String south) {
        try {
            authController = new AuthController();
        } catch (IOException e) {
            System.out.println("Error loading users");
        }

        // フッターのレイアウト設定
        this.setLayout(new FlowLayout(FlowLayout.CENTER)); // 中央揃え
        this.setPreferredSize(new Dimension(400, 50)); // 適切なサイズを設定
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // 余白を設定
        JButton homeButton = new JButton("Home");
        JButton historyButton = new JButton("History");
        JButton notificationButton = new JButton("Notification");
        JButton logoutButton = new JButton("Logout");
        homeButton.addActionListener((e) -> mainFrame.switchTo("CustomerDashboard"));
        historyButton.addActionListener((e) -> mainFrame.switchTo("HistoryPage"));
        notificationButton.addActionListener((e) -> mainFrame.switchTo("NotificationPage"));
        logoutButton.addActionListener((e) -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });
        this.add(homeButton);
        this.add(historyButton);
        this.add(notificationButton);
        this.add(logoutButton);
    }
}
