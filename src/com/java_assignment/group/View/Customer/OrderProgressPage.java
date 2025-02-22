package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.OrderItem;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class OrderProgressPage extends JPanel {
    private MainFrame mainFrame;
    private OrderController orderController;
    private AuthController authController;
    private Order order;
    private BaseUser baseUser;
    private Timer refreshTimer;

    public OrderProgressPage(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Setup auto-refresh timer (every 10 seconds)
        refreshTimer = new Timer(10000, e -> {
            loadCurrentOrder();
            refreshOrderUI();
        });
        refreshTimer.start();
    }

    private void loadCurrentOrder() {
        try {
            this.authController = new AuthController();
            this.baseUser = authController.getCurrentUser();
            this.orderController = new OrderController();
            this.order = orderController.getCurrentOrder(baseUser.getId());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading order: " + e.getMessage());
        }
    }

    public void onPageDisplayed() {
        loadCurrentOrder();
        refreshOrderUI();
        System.out.println("Order progress page loaded");
    }

    private void showNoOrderPanel() {
        JPanel noOrderPanel = new JPanel();
        noOrderPanel.setLayout(new BoxLayout(noOrderPanel, BoxLayout.Y_AXIS));
        noOrderPanel.setBackground(Color.WHITE);
        noOrderPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 0, 0));

        JLabel messageLabel = new JLabel("No active order found");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton backButton = createStyledButton("Back to Dashboard", new Color(108, 117, 125));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));

        noOrderPanel.add(messageLabel);
        noOrderPanel.add(Box.createVerticalStrut(20));
        noOrderPanel.add(backButton);

        add(noOrderPanel, BorderLayout.CENTER);
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

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });

        return button;
    }

    private void refreshOrderUI() {
        removeAll();

        if (order == null) {
            showNoOrderPanel();
            return;
        }

        // Header Panel with modern styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Order Progress");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41));

        JButton backButton = createStyledButton("Back to Dashboard", new Color(108, 117, 125));
        backButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);

        // Main Content Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Status Panel with modern progress indicator
        JPanel statusPanel = createStatusPanel();
        mainPanel.add(statusPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Order Details Panel
        JPanel detailsPanel = createCostPanel();
        mainPanel.add(detailsPanel);

        // Add all components
        add(headerPanel, BorderLayout.NORTH);
        add(new JScrollPane(mainPanel), BorderLayout.CENTER);

        // Footer Panel with Cancel Button if order is in 'Ordered' status
        if (order.getCurrentStatus().equals("Ordered")) {
            JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            footerPanel.setBackground(Color.WHITE);
            footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

            JButton cancelButton = createStyledButton("Cancel Order", new Color(220, 53, 69));
            cancelButton.addActionListener(e -> {
                int result = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to cancel this order?",
                    "Cancel Order",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                if (result == JOptionPane.YES_OPTION) {
                    orderController.updateOrderStatus(order.getId(), "Cancel");
                    loadCurrentOrder();
                    refreshOrderUI();
                }
            });
            footerPanel.add(cancelButton);
            add(footerPanel, BorderLayout.SOUTH);
        }

        revalidate();
        repaint();
    }

    // 注文ステータスのパネル作成
    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
//        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Order Status"));

        JLabel currentStatus = new JLabel();
        currentStatus.setText(order.getCurrentStatus());
        panel.add(currentStatus);

        return panel;
    }

    // 金額詳細のパネル作成
    private JPanel createCostPanel() {
        Integer rowCount = 6;
        rowCount += order.getItems().size();
        if (order.getOrderType().equals("Delivery")) {rowCount++;};
        System.out.println(rowCount);

        JPanel panel = new JPanel(new GridLayout(rowCount, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Order Detail"));

        for (OrderItem item: order.getItems()){
            panel.add(new JLabel(item.getMenu().getName()+" : "));
            panel.add(new JLabel("x" + item.getAmount()));
            panel.add(new JLabel("RM"+item.getEachPrice()*item.getAmount()));
        }

        panel.add(new JLabel("Tax:"));
        panel.add(new JLabel());
        panel.add(new JLabel("RM"+ order.getTax()));

        if (order.getOrderType().equals("Delivery")){
            panel.add(new JLabel("Delivery:"));
            panel.add(new JLabel());
            panel.add(new JLabel("RM"+ order.getCommission()));
        }

        panel.add(new JLabel("Charge fee:"));
        panel.add(new JLabel());
        panel.add(new JLabel("RM"+ order.getCommission()));

        panel.add(new JLabel("Total : "));
        panel.add(new JLabel());
        panel.add(new JLabel("RM"+order.getTotalPriceAllIncludes()));

        panel.add(new JLabel("Address : "));
        panel.add(new JLabel());
        panel.add(new JLabel(order.getAddress()));

        panel.add(new JLabel("Order type:"));
        panel.add(new JLabel());
        panel.add(new JLabel(order.getOrderType()));


        return panel;
    }

    // 配送情報のパネル作成（配送先住所、ドライバーへのコンタクト）
    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("配送情報"));

        // ダミーの配送先住所とドライバーコンタクト
        JLabel addressLabel = new JLabel("配送先住所: 東京都渋谷区〇〇-〇〇-〇〇");
        addressLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel contactLabel = new JLabel("ドライバーへのコンタクト: 090-1234-5678");
        contactLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        panel.add(addressLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(contactLabel);

        return panel;
    }
}
