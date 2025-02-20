package com.java_assignment.group.View.DeliveryRunner;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.DeliveryRunnerController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.DeliveryRunner;
import com.java_assignment.group.Model.Order;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class DeliveryRunnerDashboard extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private OrderController orderController;
    private BaseUser baseUser;
    private Order currentOrder;
    private DeliveryRunnerController deliveryRunnerController;
    private DeliveryRunner deliveryRunner;

    private JLabel orderIdLabel;
    private JLabel venderAddressLabel;
    private JLabel deliveryAddressLabel;
    private JLabel deliveryFeeLabel;
    private JLabel statusLabel;
    private JPanel buttonPanel;

    public DeliveryRunnerDashboard(MainFrame frame) {
        this.mainFrame = frame;

        try {
            authController = new AuthController();
            orderController = new OrderController();
            deliveryRunnerController = new DeliveryRunnerController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading: " + e.getMessage());
        }

        baseUser = authController.getCurrentUser();
        if (baseUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "User not authenticated.");
            return;
        }
        deliveryRunner = deliveryRunnerController.getDeliveryRunnerByBaseId(baseUser.getId());


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

        JButton viewHistoryButton = new JButton("View History");
        viewHistoryButton.setAlignmentX(CENTER_ALIGNMENT);
        viewHistoryButton.addActionListener(e -> viewHistory());
        add(Box.createVerticalStrut(20));
        add(viewHistoryButton);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });
        add(logoutButton);
    }

    private void loadCurrentOrder() {
        List<Order> assignedOrders = orderController.getOrdersByDeliveryRunner(deliveryRunner.getId());
        if (!assignedOrders.isEmpty()) {
            currentOrder = assignedOrders.get(0); // 一番新しいオーダーを表示
            updateOrderDisplay();
        } else {
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
        if (currentOrder == null) return;

        orderIdLabel.setText("Order ID: " + currentOrder.getId());
        System.out.println(currentOrder);
        System.out.println(currentOrder.getVender());
        venderAddressLabel.setText("Vender Address: " + currentOrder.getVender().getStoreName());
        deliveryAddressLabel.setText("Delivery Address: " + currentOrder.getAddress());
        deliveryFeeLabel.setText("Delivery Fee: $" + currentOrder.getDeliveryFee());
        statusLabel.setText("Status: " + currentOrder.getCurrentStatus());

        buttonPanel.removeAll();

        String status = currentOrder.getCurrentStatus();
        if ("Preparing".equals(status)) {
            JButton acceptButton = new JButton("Accept");
            acceptButton.addActionListener(e -> updateOrderStatus("Ready"));

            JButton declineButton = new JButton("Decline");
            declineButton.addActionListener(e -> updateOrderStatus("Declined"));

            buttonPanel.add(acceptButton);
            buttonPanel.add(declineButton);
        } else if ("Ready".equals(status)) {
            JButton handedButton = new JButton("Handed");
            handedButton.addActionListener(e -> updateOrderStatus("Handed"));
            buttonPanel.add(handedButton);
        } else if ("Handed".equals(status)) {
            JButton deliveredButton = new JButton("Delivered");
            deliveredButton.addActionListener(e -> updateOrderStatus("Delivered"));
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
        }
    }

    private void viewHistory() {
        List<Order> pastOrders = orderController.getOrdersByDeliveryRunner(baseUser.getId());
        if (pastOrders.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "No past deliveries.");
            return;
        }

        StringBuilder history = new StringBuilder("Past Orders:\n");
        for (Order order : pastOrders) {
            history.append("Order ID: ").append(order.getId())
                    .append("\nVender Address: ").append(order.getVender().getStoreName())
                    .append("\nDelivery Address: ").append(order.getAddress())
                    .append("\nStatus: ").append(order.getCurrentStatus())
                    .append("\n----------------------\n");
        }

        JOptionPane.showMessageDialog(mainFrame, history.toString(), "Delivery History", JOptionPane.INFORMATION_MESSAGE);
    }
}
