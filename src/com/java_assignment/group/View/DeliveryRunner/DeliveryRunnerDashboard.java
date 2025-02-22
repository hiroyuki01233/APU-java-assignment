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
import javax.swing.table.TableCellRenderer;
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

    private void initializeControllers() {
        try {
            this.authController = new AuthController();
            this.orderController = new OrderController();
            this.deliveryRunnerController = new DeliveryRunnerController();
            this.notificationController = new NotificationController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers: " + e.getMessage());
        }
    }

    public void onPageDisplayed() {
        try {
            initializeControllers();
            if (!initializeUser()) return;
            setupUI();
            loadCurrentOrder();
            viewHistory();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading page: " + e.getMessage());
        }
    }

    private boolean initializeUser() {
        this.baseUser = authController.getCurrentUser();
        if (baseUser == null) {
            JOptionPane.showMessageDialog(mainFrame, "User not authenticated.");
            return false;
        }
        this.deliveryRunner = deliveryRunnerController.getDeliveryRunnerByBaseId(baseUser.getId());
        return deliveryRunner != null;
    }

    private void setupUI() {
        removeAll();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));
        setBackground(Color.WHITE);

        add(createHeaderPanel());
        add(Box.createVerticalStrut(20));
        add(createCurrentOrderPanel());
        add(Box.createVerticalStrut(20));
        add(createActionButtonsPanel());
        add(Box.createVerticalStrut(20));
        add(createHistoryPanel());

        revalidate();
        repaint();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Delivery Runner Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

        // Create a wrapper panel to center the info panel
        JPanel centerWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centerWrapper.setBackground(Color.WHITE);

        // Create a styled panel for delivery runner info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));

        // Add a subtle shadow effect
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createLineBorder(new Color(0, 0, 0, 20), 1)
            ),
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
            )
        ));

        // Profile section
        JLabel profileLabel = new JLabel("Delivery Runner Profile");
        profileLabel.setFont(new Font("Arial", Font.BOLD, 18));
        profileLabel.setForeground(new Color(33, 37, 41));
        profileLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoPanel.add(profileLabel);
        infoPanel.add(Box.createVerticalStrut(15));

        // Runner details with icons and modern styling
        String fullName = deliveryRunner.getFirstName() + " " + deliveryRunner.getLastName();
        JLabel nameLabel = createInfoLabel("👤 " + fullName, Font.BOLD, 16);
        JLabel emailLabel = createInfoLabel("✉️ " + baseUser.getEmailAddress(), Font.PLAIN, 14);
        JLabel phoneLabel = createInfoLabel("📱 N/A", Font.PLAIN, 14);

        // Add styled labels with proper spacing
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(emailLabel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(phoneLabel);

        // Add the info panel to the center wrapper
        centerWrapper.add(infoPanel);

        // Add the wrapper to the header panel
        headerPanel.add(centerWrapper, BorderLayout.CENTER);

        return headerPanel;
    }

    private JLabel createInfoLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(new Color(33, 37, 41));
        label.setAlignmentX(LEFT_ALIGNMENT);
        
        // Add padding
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return label;
    }

    private JPanel createCurrentOrderPanel() {
        JPanel currentOrderPanel = new JPanel();
        currentOrderPanel.setLayout(new BoxLayout(currentOrderPanel, BoxLayout.Y_AXIS));
        currentOrderPanel.setBackground(Color.WHITE);
        currentOrderPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        orderIdLabel = createStyledLabel("", Font.BOLD, 16);
        venderAddressLabel = createStyledLabel("", Font.PLAIN, 14);
        deliveryAddressLabel = createStyledLabel("", Font.PLAIN, 14);
        deliveryFeeLabel = createStyledLabel("", Font.PLAIN, 14);
        statusLabel = createStyledLabel("", Font.BOLD, 16);

        currentOrderPanel.add(orderIdLabel);
        currentOrderPanel.add(Box.createVerticalStrut(10));
        currentOrderPanel.add(venderAddressLabel);
        currentOrderPanel.add(Box.createVerticalStrut(10));
        currentOrderPanel.add(deliveryAddressLabel);
        currentOrderPanel.add(Box.createVerticalStrut(10));
        currentOrderPanel.add(deliveryFeeLabel);
        currentOrderPanel.add(Box.createVerticalStrut(10));
        currentOrderPanel.add(statusLabel);
        currentOrderPanel.add(Box.createVerticalStrut(20));

        buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        currentOrderPanel.add(buttonPanel);

        return currentOrderPanel;
    }

    private JPanel createActionButtonsPanel() {
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionButtonsPanel.setBackground(Color.WHITE);

        JButton notificationButton = createStyledButton("View Notifications", new Color(0, 123, 255));
        notificationButton.addActionListener(e -> mainFrame.switchTo("NotificationPage"));

        JButton revenueButton = createStyledButton("View Revenue", new Color(40, 167, 69));
        revenueButton.addActionListener(e -> mainFrame.switchTo("RevenueDashboard"));

        JButton logoutButton = createStyledButton("Logout", new Color(220, 53, 69));
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });

        actionButtonsPanel.add(notificationButton);
        actionButtonsPanel.add(revenueButton);
        actionButtonsPanel.add(logoutButton);

        return actionButtonsPanel;
    }

    private JPanel createHistoryPanel() {
        JPanel historyContainer = new JPanel(new BorderLayout());
        historyContainer.setBackground(Color.WHITE);
        historyContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel historyTitle = new JLabel("Order History");
        historyTitle.setFont(new Font("Arial", Font.BOLD, 18));
        historyTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        historyContainer.add(historyTitle, BorderLayout.NORTH);

        historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBackground(Color.WHITE);
        historyPanel.setPreferredSize(new Dimension(600, 300));
        historyContainer.add(historyPanel, BorderLayout.CENTER);

        return historyContainer;
    }

    private void loadCurrentOrder() throws IOException {
        List<Order> orders = orderController.getOrdersByDeliveryRunner(baseUser.getId());
        this.currentOrder = null;
        
        for (Order order : orders) {
            String status = order.getCurrentStatus();
            if ("Preparing".equals(status) || "ReadyToPickup".equals(status) || "OnDelivery".equals(status)) {
                this.currentOrder = order;
                break;
            }
        }
        
        updateOrderDisplay();
    }

    private void updateOrderDisplay() {
        if (this.currentOrder == null) return;

        orderIdLabel.setText("Order ID: " + currentOrder.getId());
        venderAddressLabel.setText("Vendor: " + currentOrder.getVender().getStoreName());
        deliveryAddressLabel.setText("Delivery To: " + currentOrder.getAddress());
        deliveryFeeLabel.setText("Delivery Fee: $" + currentOrder.getDeliveryFee());
        statusLabel.setText("Status: " + currentOrder.getCurrentStatus());

        updateActionButtons();
    }

    private void updateActionButtons() {
        buttonPanel.removeAll();

        String status = currentOrder.getCurrentStatus();
        if ("Preparing".equals(status)) {
            addPreparingButtons();
        } else if ("ReadyToPickup".equals(status)) {
            addReadyToPickupButton();
        } else if ("OnDelivery".equals(status)) {
            addOnDeliveryButton();
        }

        buttonPanel.revalidate();
        buttonPanel.repaint();
    }

    private void addPreparingButtons() {
        JButton acceptButton = createStyledButton("Accept", new Color(40, 167, 69));
        acceptButton.addActionListener(e -> updateOrderStatus("Preparing-runnerWaiting"));

        JButton declineButton = createStyledButton("Decline", new Color(220, 53, 69));
        declineButton.addActionListener(e -> handleOrderDecline());

        buttonPanel.add(acceptButton);
        buttonPanel.add(declineButton);
    }

    private void addReadyToPickupButton() {
        JButton handedButton = createStyledButton("Start Delivery", new Color(0, 123, 255));
        handedButton.addActionListener(e -> updateOrderStatus("OnDelivery"));
        buttonPanel.add(handedButton);
    }

    private void addOnDeliveryButton() {
        JButton deliveredButton = createStyledButton("Complete Delivery", new Color(40, 167, 69));
        deliveredButton.addActionListener(e -> updateOrderStatus("Completed"));
        buttonPanel.add(deliveredButton);
    }

    private void handleOrderDecline() {
        try {
            DeliveryRunner runner = deliveryRunnerController.getAvailableDeliveryRunner();
            if (runner == null) {
                orderController.updateOrderStatus(currentOrder.getId(), "ForceCancelled");
            } else {
                processOrderReassignment(runner);
            }
            loadCurrentOrder();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error declining order: " + e.getMessage());
        }
    }

    private void processOrderReassignment(DeliveryRunner newRunner) {
        Notification declineNotification = new Notification(
            UUID.randomUUID().toString(),
            currentOrder.getDeliveryRunnerId(),
            "You declined the order",
            "You have declined the order successfully.",
            "DeliveryRunnerDashboard",
            LocalDateTime.now()
        );
        notificationController.addNotification(declineNotification);

        currentOrder.setDeliveryRunnerId(newRunner.getId());

        Notification reassignNotification = new Notification(
            UUID.randomUUID().toString(),
            currentOrder.getDeliveryRunnerId(),
            "You received new delivery",
            "You have received new delivery please accept or decline.",
            "DeliveryRunnerDashboard",
            LocalDateTime.now()
        );
        notificationController.addNotification(reassignNotification);
    }

    private void updateOrderStatus(String newStatus) {
        if (currentOrder == null) return;

        try {
            boolean success = orderController.updateOrderStatus(currentOrder.getId(), newStatus);
            if (success) {
                JOptionPane.showMessageDialog(mainFrame, "Order status updated to: " + newStatus);
                loadCurrentOrder();
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to update order status.");
                loadCurrentOrder();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error updating order status: " + e.getMessage());
        }
    }

    private void viewHistory() throws IOException {
        List<Order> pastOrders = orderController.getOrdersByDeliveryRunner(baseUser.getId());
        historyPanel.removeAll();

        if (pastOrders.isEmpty()) {
            historyPanel.add(new JLabel("No past deliveries."), BorderLayout.CENTER);
        } else {
            createHistoryTable(pastOrders);
        }

        historyPanel.revalidate();
        historyPanel.repaint();
    }

    private void createHistoryTable(List<Order> pastOrders) {
        String[] columnNames = {"Order ID", "Vender Address", "Delivery Address", "Status"};
        Object[][] data = new Object[pastOrders.size()][4];

        for (int i = 0; i < pastOrders.size(); i++) {
            Order order = pastOrders.get(i);
            data[i][0] = order.getId();
            data[i][1] = order.getVender().getStoreName();
            data[i][2] = order.getAddress();
            data[i][3] = order.getCurrentStatus();
        }

        JTable historyTable = new JTable(data, columnNames) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return getPreferredSize().width < getParent().getWidth();
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (isRowSelected(row)) {
                    comp.setBackground(new Color(200, 200, 200));
                } else {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return comp;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        historyTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(30);
        historyTable.setShowGrid(false);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        
        // Set column widths
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(100);

        JScrollPane scrollPane = new JScrollPane(historyTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        historyPanel.add(scrollPane, BorderLayout.CENTER);
    }

    private JLabel createStyledLabel(String text, int style, int size) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", style, size));
        label.setForeground(new Color(33, 37, 41));
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(baseColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }
}
