package com.java_assignment.group.View.Vender;

import com.java_assignment.group.Component.OrderList;
import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.OrderItem;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

public class VenderDashboard extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private OrderController orderController;
    private JList<Order> list;
    private DefaultListModel<Order> listModel = new DefaultListModel<>();
    private List<Order> orders;
    private VenderController venderController;
    private Vender vender;
    private BaseUser baseUser;

    private void onLoadDeliveryRunners() {
        try {
            orderController = new OrderController();
            authController = new AuthController();
            venderController = new VenderController();
            baseUser = authController.getCurrentUser();
            if (null == baseUser) {
                return;
            }
            vender = venderController.getVenderByBaseUserId(baseUser.getId());
            if (null == vender){
                return;
            }
            orders = orderController.getOrdersByVender(vender.getId());
            System.out.println(orders);
            listModel.clear();
            for (Order order : orders) {
                listModel.addElement(order);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading delivery runners: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadDeliveryRunners();
        System.out.println("Delivery Runner loaded");
    }

    public VenderDashboard(MainFrame frame) {
        this.mainFrame = frame;
        this.onLoadDeliveryRunners();

        if (vender == null || orders == null){
            return;
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("Vender Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(30));

        Dimension buttonSize = new Dimension(250, 30);

        JButton manageVenderButton = new JButton("Manage Menu");
        manageVenderButton.setAlignmentX(CENTER_ALIGNMENT);
        manageVenderButton.setMaximumSize(buttonSize);
        manageVenderButton.addActionListener(e -> mainFrame.switchTo("VenderMenuListPage"));
        add(manageVenderButton);
        add(Box.createVerticalStrut(10));

        ActionListener onChangeStatus = e -> onChangeStatus();
        add(new OrderList(vender, onChangeStatus));

        // Send Receipt (dummy)
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });
        add(logoutButton);
    }

    // Handle status change
    public void onChangeStatus() {
        System.out.println("button clicked");
    }

    // This function will be used to display order items in a popup dialog
    public void viewOrderItems(Order order) {
        try {
            List<OrderItem> orderItems = orderController.getOrderItemsByOrder(order.getId());
            StringBuilder itemsList = new StringBuilder("Order Items:\n");
            for (OrderItem item : orderItems) {
                itemsList.append("Menu ID: ").append(item.getMenuId())
                        .append(", Amount: ").append(item.getAmount()).append("\n");
            }

            // Show a popup with order items
            JOptionPane.showMessageDialog(mainFrame, itemsList.toString(), "Order Items", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading order items: " + e.getMessage());
        }
    }

    // Function to handle status update of the order
    public void updateOrderStatus(Order order, String newStatus) {
        boolean success = orderController.updateOrderStatus(order.getId(), newStatus);
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Order status updated to: " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            onLoadDeliveryRunners();  // Refresh the order list after status update
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update order status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
