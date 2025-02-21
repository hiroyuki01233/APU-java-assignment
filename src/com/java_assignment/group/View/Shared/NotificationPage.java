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
            this.hasOrder = orderController.getCurrentOrder(user.getId()) != null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(new Color(240, 240, 240));
    }

    // このメソッドはページ表示時に呼び出してください
    public void onPageDisplayed() {
        loadNotifications();
        System.out.println("Notification page loaded");
    }

    private void loadNotifications() {
        removeAll();

        JLabel titleLabel = new JLabel("Notification Page");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.CENTER);
        add(Box.createVerticalStrut(10));

        JButton backButton = new JButton("Go back");
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

        add(backButton);
        add(Box.createVerticalStrut(10));

        List<Notification> notifications = null;
        try {
            notifications = notificationController.getNotificationsByUser(user.getId());
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        if(notifications.isEmpty()){
            JLabel noNotificationLabel = new JLabel();
            noNotificationLabel.setText("No notification yet.");
            noNotificationLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            add(titleLabel, BorderLayout.CENTER);
        }

        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        for (Notification notification : notifications) {
            notificationPanel.add(createNotificationCard(notification));
        }

        JScrollPane scrollPane = new JScrollPane(notificationPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        scrollPane.setMaximumSize(new Dimension(800, 800));

        add(scrollPane);

        revalidate();
        repaint();
    }

    private JPanel createNotificationCard(Notification notification) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        card.setMaximumSize(new Dimension(350, 100));

        JLabel titleLabel = new JLabel(notification.getTitle());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        JTextArea descriptionLabel = new JTextArea(notification.getDescription());
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descriptionLabel.setLineWrap(true);
        descriptionLabel.setWrapStyleWord(true);
        descriptionLabel.setEditable(false);
        descriptionLabel.setBackground(Color.WHITE);

        JLabel dateLabel = new JLabel(notification.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm")));
        dateLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        dateLabel.setForeground(Color.GRAY);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.setBackground(Color.WHITE);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(descriptionLabel, BorderLayout.CENTER);

        card.add(textPanel, BorderLayout.WEST);
        card.add(dateLabel, BorderLayout.EAST);

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
