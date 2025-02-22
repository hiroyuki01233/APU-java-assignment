package com.java_assignment.group.View.Vender;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
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

    public VenderDashboard(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(100, 149, 237)); // Cornflower blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 119, 207)); // Darker shade
            }
            public void mouseExited(MouseEvent e) {
                if (button.getText().equals("Logout")) {
                    button.setBackground(new Color(205, 92, 92)); // Indian Red
                } else {
                    button.setBackground(new Color(100, 149, 237)); // Cornflower blue
                }
            }
        });

        if (button.getText().equals("Logout")) {
            button.setBackground(new Color(205, 92, 92)); // Indian Red
        }
    }

    private void onLoadDashboard() {
        try {
            this.orderController = new OrderController();
            this.authController = new AuthController();
            this.venderController = new VenderController();
            this.baseUser = authController.getCurrentUser();
            if (null == baseUser) {
                return;
            }
            this.vender = venderController.getVenderByBaseUserId(baseUser.getId());
            if (null == vender){
                return;
            }
            this.orders = orderController.getOrdersByVender(vender.getId());
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
        onLoadDashboard();

        removeAll();
        setLayout(new BorderLayout());

        if (vender == null || orders == null){
            System.out.println("vender or orders are null");
            return;
        }

        // Main container with padding
        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new BoxLayout(mainContainer, BoxLayout.Y_AXIS));
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Vendor Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        mainContainer.add(headerPanel);
        mainContainer.add(Box.createVerticalStrut(20));

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        Dimension buttonSize = new Dimension(200, 40);

        JButton manageVenderButton = new JButton("Manage Menu");
        manageVenderButton.setPreferredSize(buttonSize);
        styleButton(manageVenderButton);
        manageVenderButton.addActionListener(e -> mainFrame.switchTo("VenderMenuListPage"));

        JButton revenueButton = new JButton("View Revenue");
        revenueButton.setPreferredSize(buttonSize);
        styleButton(revenueButton);
        revenueButton.addActionListener(e -> mainFrame.switchTo("RevenueDashboard"));

        JButton notificationButton = new JButton("View Notification");
        notificationButton.setPreferredSize(buttonSize);
        styleButton(notificationButton);
        notificationButton.addActionListener(e -> mainFrame.switchTo("NotificationPage"));

        buttonPanel.add(manageVenderButton);
        buttonPanel.add(revenueButton);
        buttonPanel.add(notificationButton);

        mainContainer.add(buttonPanel);
        mainContainer.add(Box.createVerticalStrut(30));

        // Orders Section with modern styling
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBackground(Color.WHITE);
        ordersPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Add a title for the orders section
        JLabel ordersTitle = new JLabel("Current Orders", SwingConstants.CENTER);
        ordersTitle.setFont(new Font("Arial", Font.BOLD, 20));
        ordersTitle.setForeground(new Color(51, 51, 51));
        ordersTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        ordersPanel.add(ordersTitle, BorderLayout.NORTH);

        // Create the orders list with modern styling
        JPanel ordersListPanel = new JPanel();
        ordersListPanel.setLayout(new BoxLayout(ordersListPanel, BoxLayout.Y_AXIS));
        ordersListPanel.setBackground(Color.WHITE);

        for (Order order : orders) {
            // Order card panel
            JPanel orderCard = new JPanel(new BorderLayout());
            orderCard.setBackground(Color.WHITE);
            orderCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            orderCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

            // Order info panel
            JPanel orderInfo = new JPanel(new GridLayout(3, 1, 5, 5));
            orderInfo.setBackground(Color.WHITE);

            JLabel orderIdLabel = new JLabel("Order #" + order.getId());
            orderIdLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            JLabel statusLabel = new JLabel("Status: " + order.getCurrentStatus());
            statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            
            JLabel dateLabel = new JLabel("Date: " + order.getCreatedAt());
            dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            orderInfo.add(orderIdLabel);
            orderInfo.add(statusLabel);
            orderInfo.add(dateLabel);

            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.setBackground(Color.WHITE);

            JButton viewButton = new JButton("View Details");
            viewButton.setPreferredSize(new Dimension(120, 30));
            styleActionButton(viewButton, "view");
            viewButton.addActionListener(e -> viewOrderItems(order));

            JButton acceptButton = new JButton("Accept");
            acceptButton.setPreferredSize(new Dimension(80, 30));
            styleActionButton(acceptButton, "accept");
            acceptButton.addActionListener(e -> updateOrderStatus(order, "Accepted"));

            JButton declineButton = new JButton("Decline");
            declineButton.setPreferredSize(new Dimension(80, 30));
            styleActionButton(declineButton, "decline");
            declineButton.addActionListener(e -> updateOrderStatus(order, "Declined"));

            // Only show accept/decline buttons for pending orders
            if (order.getCurrentStatus().equals("Pending")) {
                buttonsPanel.add(acceptButton);
                buttonsPanel.add(declineButton);
            }
            buttonsPanel.add(viewButton);

            orderCard.add(orderInfo, BorderLayout.CENTER);
            orderCard.add(buttonsPanel, BorderLayout.EAST);

            // Add some spacing between cards
            JPanel spacer = new JPanel();
            spacer.setPreferredSize(new Dimension(1, 10));
            spacer.setBackground(Color.WHITE);

            ordersListPanel.add(orderCard);
            ordersListPanel.add(spacer);
        }

        // Wrap the orders list in a scroll pane
        JScrollPane scrollPane = new JScrollPane(ordersListPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);

        mainContainer.add(ordersPanel);
        mainContainer.add(Box.createVerticalStrut(30));

        // Footer with Logout Button
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setBackground(Color.WHITE);

        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(buttonSize);
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });

        footerPanel.add(logoutButton);
        mainContainer.add(footerPanel);

        add(mainContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private void styleActionButton(JButton button, String type) {
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 12));

        Color backgroundColor;
        switch (type) {
            case "accept":
                backgroundColor = new Color(40, 167, 69); // Bootstrap green
                break;
            case "decline":
                backgroundColor = new Color(220, 53, 69); // Bootstrap red
                break;
            default:
                backgroundColor = new Color(0, 123, 255); // Bootstrap blue
                break;
        }

        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }
        });
    }

    public void viewOrderItems(Order order) {
        try {
            List<OrderItem> orderItems = orderController.getOrderItemsByOrder(order.getId());
            
            JDialog detailDialog = new JDialog(mainFrame, "Order Receipt", true);
            detailDialog.setLayout(new BorderLayout());

            JPanel receiptPanel = new JPanel();
            receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
            receiptPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            receiptPanel.setBackground(Color.WHITE);

            // Header
            JLabel headerLabel = new JLabel(vender.getStoreName());
            headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            receiptPanel.add(headerLabel);
            receiptPanel.add(Box.createVerticalStrut(20));

            // Order ID and Date
            JPanel orderInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            orderInfoPanel.setBackground(Color.WHITE);
            orderInfoPanel.add(new JLabel("Order #" + order.getId()));
            orderInfoPanel.add(new JLabel("Date: " + order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))));
            receiptPanel.add(orderInfoPanel);
            receiptPanel.add(Box.createVerticalStrut(20));

            // Items
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setBackground(Color.WHITE);
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            for (OrderItem item : orderItems) {
                JPanel itemRow = new JPanel(new BorderLayout());
                itemRow.setBackground(Color.WHITE);
                JLabel nameLabel = new JLabel(item.getMenu().getName());
                JLabel priceLabel = new JLabel(String.format("RM %.2f", item.getEachPrice() * item.getAmount()));
                JLabel qtyLabel = new JLabel("x" + item.getAmount());
                itemRow.add(nameLabel, BorderLayout.WEST);
                itemRow.add(qtyLabel, BorderLayout.CENTER);
                itemRow.add(priceLabel, BorderLayout.EAST);
                itemsPanel.add(itemRow);
                itemsPanel.add(Box.createVerticalStrut(5));
            }

            receiptPanel.add(itemsPanel);
            receiptPanel.add(Box.createVerticalStrut(20));

            // Summary
            JPanel summaryPanel = new JPanel();
            summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
            summaryPanel.setBackground(Color.WHITE);
            summaryPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(222, 226, 230)));

            addSummaryRow(summaryPanel, "Subtotal", String.format("RM %.2f", order.getTotalPrice()));
            addSummaryRow(summaryPanel, "Delivery Fee", String.format("RM %.2f", order.getDeliveryFee()));
            addSummaryRow(summaryPanel, "Service Fee", String.format("RM %.2f", order.getCommission()));
            addSummaryRow(summaryPanel, "Tax", String.format("RM %.2f", order.getTax()));
            addSummaryRow(summaryPanel, "Total", String.format("RM %.2f", order.getTotalPriceAllIncludes()));

            receiptPanel.add(summaryPanel);

            JButton closeButton = new JButton("Close");
            styleActionButton(closeButton, "view");
            closeButton.addActionListener(e -> detailDialog.dispose());
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            receiptPanel.add(Box.createVerticalStrut(20));
            receiptPanel.add(closeButton);

            detailDialog.add(new JScrollPane(receiptPanel));
            detailDialog.setSize(400, 600);
            detailDialog.setLocationRelativeTo(mainFrame);
            detailDialog.setVisible(true);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading order items: " + e.getMessage());
        }
    }

    private void addSummaryRow(JPanel panel, String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        row.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        JLabel labelComponent = new JLabel(label);
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Arial", Font.BOLD, 14));
        row.add(labelComponent, BorderLayout.WEST);
        row.add(valueComponent, BorderLayout.EAST);
        panel.add(row);
    }

    public void updateOrderStatus(Order order, String newStatus) {
        boolean success = orderController.updateOrderStatus(order.getId(), newStatus);
        if (success) {
            JOptionPane.showMessageDialog(mainFrame, "Order status updated to: " + newStatus, "Status Updated", JOptionPane.INFORMATION_MESSAGE);
            onLoadDashboard();
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update order status.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
