package com.java_assignment.group.View.Shared;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.NotificationController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Notification;

public class NotificationPage extends JPanel {
    private MainFrame mainFrame;
    private NotificationController notificationController;
    private AuthController authController;
    private OrderController orderController;
    private BaseUser user;
    private Boolean hasOrder;

    public NotificationPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        try{
            this.notificationController = new NotificationController();
            this.authController = new AuthController();
            this.orderController = new OrderController();
            this.user = authController.getCurrentUser();
            if(user != null){
                this.hasOrder = orderController.getCurrentOrder(user.getId()) != null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    // このメソッドはページ表示時に呼び出してください
    public void onPageDisplayed() {
        loadNotifications();
        System.out.println("Notification page loaded");
    }

    private void loadNotifications() {
        removeAll();

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 0, 20, 0)
        ));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);

        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(100, 149, 237));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(100, 35));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        buttonPanel.add(backButton);

        backButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backButton.setBackground(new Color(70, 130, 220));
            }
            public void mouseExited(MouseEvent e) {
                backButton.setBackground(new Color(100, 149, 237));
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (user.getUserType().equals("vender")){
                    mainFrame.switchTo("VenderDashboard");
                } else if (user.getUserType().equals("delivery_runner")) {
                    mainFrame.switchTo("DeliveryRunnerDashboard");
                } else if (user.getUserType().equals("customer")) {
                    mainFrame.switchTo("CustomerDashboard");
                }
            }
        });

        headerPanel.add(buttonPanel, BorderLayout.WEST);
        headerPanel.add(titlePanel, BorderLayout.CENTER);
        add(headerPanel);
        add(Box.createVerticalStrut(20));

        List<Notification> notifications = null;
        try {
            notifications = notificationController.getNotificationsByUser(user.getId());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        if(notifications.isEmpty()){
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(50, 20, 50, 20)
            ));

            JLabel noNotificationLabel = new JLabel("No notifications yet");
            noNotificationLabel.setFont(new Font("Arial", Font.BOLD, 18));
            noNotificationLabel.setForeground(new Color(108, 117, 125));
            noNotificationLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyPanel.add(noNotificationLabel, BorderLayout.CENTER);
            contentPanel.add(emptyPanel);
        } else {
            JPanel notificationPanel = new JPanel();
            notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
            notificationPanel.setBackground(Color.WHITE);
            notificationPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            
            for (Notification notification : notifications) {
                notificationPanel.add(createNotificationCard(notification));
                notificationPanel.add(Box.createVerticalStrut(15));
            }

            JScrollPane scrollPane = new JScrollPane(notificationPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setBorder(BorderFactory.createEmptyBorder());
            scrollPane.getViewport().setBackground(Color.WHITE);
            contentPanel.add(scrollPane);
        }

        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        mainContentPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainContentPanel);

        revalidate();
        repaint();
    }

    private JPanel createNotificationCard(Notification notification) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setMaximumSize(new Dimension(800, 120));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(notification.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(33, 37, 41));

        JTextArea descriptionLabel = new JTextArea(notification.getDescription());
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(new Color(73, 80, 87));
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setEditable(false);
        descriptionLabel.setBackground(Color.WHITE);
        descriptionLabel.setBorder(null);

        JLabel dateLabel = new JLabel(notification.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        dateLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        dateLabel.setForeground(new Color(108, 117, 125));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descriptionLabel, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.CENTER);
        card.add(dateLabel, BorderLayout.EAST);

        // Add hover effect
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(248, 249, 250));
                textPanel.setBackground(new Color(248, 249, 250));
                descriptionLabel.setBackground(new Color(248, 249, 250));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                textPanel.setBackground(Color.WHITE);
                descriptionLabel.setBackground(Color.WHITE);
            }
        });

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(hasOrder){
                    mainFrame.switchTo(notification.getPageName());
                }
            }
        });

        return card;
    }
}
