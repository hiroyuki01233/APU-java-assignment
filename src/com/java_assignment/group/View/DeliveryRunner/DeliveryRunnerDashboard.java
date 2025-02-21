package com.java_assignment.group.View.DeliveryRunner;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.DeliveryRunnerController;
import com.java_assignment.group.Controller.NotificationController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.DeliveryRunner;
import com.java_assignment.group.Model.Notification;
import com.java_assignment.group.Model.Order;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class DeliveryRunnerDashboard extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private OrderController orderController;
    private BaseUser baseUser;
    private Order currentOrder;
    private DeliveryRunnerController deliveryRunnerController;
    private DeliveryRunner deliveryRunner;
    private NotificationController notificationController;

    private JLabel orderIdLabel;
    private JLabel venderAddressLabel;
    private JLabel deliveryAddressLabel;
    private JLabel deliveryFeeLabel;
    private JLabel statusLabel;
    private JPanel buttonPanel;

    // New panel to display order history as a table
    private JPanel historyPanel;

    public DeliveryRunnerDashboard(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public Boolean onLoadDashboard(){
        try {
            this.authController = new AuthController();
            this.orderController = new OrderController();
            this.deliveryRunnerController = new DeliveryRunnerController();
            this.notificationController = new NotificationController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading: " + e.getMessage());
        }

        this.baseUser = authController.getCurrentUser();
        if (baseUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "User not authenticated.");
            return false;
        }
        this.deliveryRunner = deliveryRunnerController.getDeliveryRunnerByBaseId(baseUser.getId());
        if (deliveryRunner == null) {
            return false;
        }
        return true;
    }

    public void onPageDisplayed() {
        onLoadDashboard();
        removeAll();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("Delivery Runner Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(30));

        orderIdLabel = new JLabel();
        venderAddressLabel = new JLabel();
        deliveryAddressLabel = new JLabel();
        deliveryFeeLabel = new JLabel();
        statusLabel = new JLabel();

        orderIdLabel.setFont(new Font("Arial", Font.BOLD, 16));
        venderAddressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        deliveryAddressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        deliveryFeeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));

        add(orderIdLabel);
        add(Box.createVerticalStrut(10));
        add(venderAddressLabel);
        add(Box.createVerticalStrut(10));
        add(deliveryAddressLabel);
        add(Box.createVerticalStrut(10));
        add(deliveryFeeLabel);
        add(Box.createVerticalStrut(10));
        add(statusLabel);
        add(Box.createVerticalStrut(20));

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        add(buttonPanel);

        loadCurrentOrder();

        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });
        add(Box.createVerticalStrut(20));
        add(logoutButton);

        JButton revenueButton = new JButton("View Revenue");
        revenueButton.setAlignmentX(CENTER_ALIGNMENT);
        revenueButton.addActionListener(e -> {
            mainFrame.switchTo("RevenueDashboard");
        });
        add(Box.createVerticalStrut(20));
        add(revenueButton);

        // Initialize and add the history panel at the bottom
        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setPreferredSize(new Dimension(600, 300));
        add(Box.createVerticalStrut(20));
        add(historyPanel);
        viewHistory();
    }

    private void loadCurrentOrder() {
        List<Order> assignedOrders = orderController.getOrdersByDeliveryRunner(deliveryRunner.getId());
        if (!assignedOrders.isEmpty()) {
            System.out.println("there is order!!");
            for (Order order: assignedOrders){
                if(
                        "Preparing".equals(order.getCurrentStatus()) ||
                        "Preparing-runnerWaiting".equals(order.getCurrentStatus()) ||
                        "ReadyToPickup".equals(order.getCurrentStatus()) ||
                        "OnDelivery".equals(order.getCurrentStatus())
                ){
                    System.out.println(order);
                    this.currentOrder = order;
                }
            }
            updateOrderDisplay();
        }
        if(this.currentOrder == null){
            orderIdLabel.setText("No active orders.");
            venderAddressLabel.setText("");
            deliveryAddressLabel.setText("");
            deliveryFeeLabel.setText("");
            statusLabel.setText("");
            buttonPanel.removeAll();
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }
    }

    private void updateOrderDisplay() {
        if (this.currentOrder == null) return;

        orderIdLabel.setText("Order ID: " + currentOrder.getId());
        venderAddressLabel.setText("Vender Address: " + currentOrder.getVender().getStoreName());
        deliveryAddressLabel.setText("Delivery Address: " + currentOrder.getAddress());
        deliveryFeeLabel.setText("Delivery Fee: $" + currentOrder.getDeliveryFee());
        statusLabel.setText("Status: " + currentOrder.getCurrentStatus());

        buttonPanel.removeAll();

        String status = currentOrder.getCurrentStatus();
        if ("Preparing".equals(status)) {
            JButton acceptButton = new JButton("Accept");
            acceptButton.addActionListener(e -> updateOrderStatus("Preparing-runnerWaiting"));

            JButton declineButton = new JButton("Decline");
            declineButton.addActionListener(e -> {
                DeliveryRunner runner = deliveryRunnerController.getAvailableDeliveryRunner();

                if (runner == null){
                    orderController.updateOrderStatus(currentOrder.getId(), "ForceCancelled");
                } else {
                    Notification newNotificationForOldRunner = new Notification(
                            UUID.randomUUID().toString(), currentOrder.getDeliveryRunnerId(), "You declined the order", "You have declined the order successfully.",
                            "DeliveryRunnerDashboard", LocalDateTime.now());
                    notificationController.addNotification(newNotificationForOldRunner);

                    currentOrder.setDeliveryRunnerId(runner.getId());

                    Notification newNotificationForRunner = new Notification(
                            UUID.randomUUID().toString(), currentOrder.getDeliveryRunnerId(), "You received new delivery", "You have received new delivery please accept or decline.",
                            "DeliveryRunnerDashboard", LocalDateTime.now());
                    notificationController.addNotification(newNotificationForRunner);
                }
            });

            buttonPanel.add(acceptButton);
            buttonPanel.add(declineButton);
        } else if ("ReadyToPickup".equals(status)) {
            JButton handedButton = new JButton("OnDelivery");
            handedButton.addActionListener(e -> updateOrderStatus("OnDelivery"));
            buttonPanel.add(handedButton);
        } else if ("OnDelivery".equals(status)) {
            JButton deliveredButton = new JButton("Completed");
            deliveredButton.addActionListener(e -> updateOrderStatus("Completed"));
            buttonPanel.add(deliveredButton);
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void updateOrderStatus(String newStatus) {
        if (currentOrder == null) return;

        boolean success = orderController.updateOrderStatus(currentOrder.getId(), newStatus);
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Order status updated to: " + newStatus);
            loadCurrentOrder();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update order status.");
            loadCurrentOrder();
        }
    }

    private void viewHistory() {
        List<Order> pastOrders = orderController.getOrdersByDeliveryRunner(baseUser.getId());
        historyPanel.removeAll();

        if (pastOrders.isEmpty()) {
            historyPanel.add(new JLabel("No past deliveries."), BorderLayout.CENTER);
        } else {
            String[] columnNames = {"Order ID", "Vender Address", "Delivery Address", "Status"};
            Object[][] data = new Object[pastOrders.size()][4];

            for (int i = 0; i < pastOrders.size(); i++) {
                Order order = pastOrders.get(i);
                data[i][0] = order.getId();
                data[i][1] = order.getVender().getStoreName();
                data[i][2] = order.getAddress();
                data[i][3] = order.getCurrentStatus();
            }

            // JTable をサブクラス化して、ビューの幅に合わせてテーブルを伸ばす
            JTable historyTable = new JTable(data, columnNames) {
                @Override
                public boolean getScrollableTracksViewportWidth() {
                    // テーブルの推奨サイズが親コンテナの幅より小さい場合、全幅を埋める
                    return getPreferredSize().width < getParent().getWidth();
                }
            };

            // 必要に応じて各カラムの幅を設定することも可能です
            // 例:
            // historyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            // historyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
            // historyTable.getColumnModel().getColumn(2).setPreferredWidth(200);
            // historyTable.getColumnModel().getColumn(3).setPreferredWidth(100);

            // 自動リサイズをオフにして水平スクロールバーが必要なときだけ表示するようにする
            historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            JScrollPane scrollPane = new JScrollPane(historyTable,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            historyPanel.add(scrollPane, BorderLayout.CENTER);
        }

        historyPanel.revalidate();
        historyPanel.repaint();
    }

}
