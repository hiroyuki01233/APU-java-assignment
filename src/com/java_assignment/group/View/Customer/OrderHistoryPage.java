package com.java_assignment.group.View.Customer;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.OrderItem;

// ※ MainFrameやOrder内のメソッド（getCreatedAt, getVender, getTotalPriceAllIncludes, getIdなど）は各自の実装に合わせてください。
public class OrderHistoryPage extends JPanel {
    private MainFrame mainFrame;
    private OrderController orderController;
    private AuthController authController;
    private BaseUser user;
    private Boolean disableNewOrder;

    public OrderHistoryPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
    }

    // このメソッドはページ表示時に呼び出してください
    public void onPageDisplayed() {
        initUI();
        System.out.println("Order History page loaded");
    }

    private void initUI() {
        removeAll();

        try {
            this.orderController = new OrderController();
            this.authController = new AuthController();
            this.user = this.authController.getCurrentUser();
            if (user != null){
                this.disableNewOrder = !(null == orderController.getCurrentOrder(user.getId()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//        setBackground(Color.WHITE);

        List<Order> orders = null;
        try {
            orders = orderController.getOrdersByUser(user.getId());
            if (orders != null) {
                orders.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(248, 249, 250));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Order History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(30));

        JButton goBackButton = new JButton("Back to Dashboard");
        styleButton(goBackButton, new Color(13, 110, 253));
        goBackButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));
        goBackButton.setAlignmentX(CENTER_ALIGNMENT);
        goBackButton.setPreferredSize(new Dimension(200, 40));
        centerPanel.add(goBackButton);
        centerPanel.add(Box.createVerticalStrut(40));

        JPanel cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(new Color(248, 249, 250));

        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                JPanel card = createOrderCard(order);
                cardListPanel.add(card);
                cardListPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }
        } else {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setBackground(Color.WHITE);
            emptyPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
                BorderFactory.createEmptyBorder(40, 20, 40, 20)
            ));

            JLabel emptyIcon = new JLabel("📋");
            emptyIcon.setFont(new Font("Arial", Font.PLAIN, 48));
            emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel emptyLabel = new JLabel("No orders found");
            emptyLabel.setFont(new Font("Arial", Font.BOLD, 20));
            emptyLabel.setForeground(new Color(108, 117, 125));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            emptyPanel.add(emptyIcon);
            emptyPanel.add(Box.createVerticalStrut(15));
            emptyPanel.add(emptyLabel);
            cardListPanel.add(emptyPanel);
        }

        JScrollPane scrollPane = new JScrollPane(cardListPanel);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(new Color(248, 249, 250));
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        centerPanel.add(scrollPane);
        add(centerPanel);

        revalidate();
        repaint();
    }

    private void styleButton(JButton button, Color baseColor) {
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(baseColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(baseColor);
            }
        });
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(250, 250, 250));
                g2.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 15, 15);
                g2.dispose();
            }
        };

        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(false);
        card.setBorder(new CompoundBorder(
            new RoundedBorder(15, 10),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setMaximumSize(new Dimension(800, 200));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        JLabel dateLabel = new JLabel(order.getCreatedAt().format(formatter));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(108, 117, 125));

        JLabel storeLabel = new JLabel(order.getVender().getStoreName());
        storeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        storeLabel.setForeground(new Color(33, 37, 41));

        JLabel totalLabel = new JLabel(String.format("RM %.2f", order.getTotalPriceAllIncludes()));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(13, 110, 253));

        detailsPanel.add(storeLabel);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createVerticalStrut(8));
        detailsPanel.add(totalLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton viewDetailButton = new JButton("View Detail");
        styleButton(viewDetailButton, new Color(13, 110, 253));
        viewDetailButton.addActionListener(e -> {
            JDialog detailDialog = new JDialog(mainFrame, "Order Receipt", true);
            detailDialog.setLayout(new BorderLayout());

            JPanel receiptPanel = new JPanel();
            receiptPanel.setLayout(new BoxLayout(receiptPanel, BoxLayout.Y_AXIS));
            receiptPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
            receiptPanel.setBackground(Color.WHITE);

            // Header
            JLabel headerLabel = new JLabel(order.getVender().getStoreName());
            headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
            headerLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            receiptPanel.add(headerLabel);
            receiptPanel.add(Box.createVerticalStrut(20));

            // Order ID and Date
            JPanel orderInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
            orderInfoPanel.setBackground(Color.WHITE);
            orderInfoPanel.add(new JLabel("Order #" + order.getId()));
            orderInfoPanel.add(new JLabel("Date: " + order.getCreatedAt().format(formatter)));
            receiptPanel.add(orderInfoPanel);
            receiptPanel.add(Box.createVerticalStrut(20));

            // Items
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setBackground(Color.WHITE);
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

            for (OrderItem item : order.getItems()) {
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
            styleButton(closeButton, new Color(108, 117, 125));
            closeButton.addActionListener(e1 -> detailDialog.dispose());
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            receiptPanel.add(Box.createVerticalStrut(20));
            receiptPanel.add(closeButton);

            detailDialog.add(new JScrollPane(receiptPanel));
            detailDialog.setSize(400, 600);
            detailDialog.setLocationRelativeTo(mainFrame);
            detailDialog.setVisible(true);
        });

        JButton reorderButton = new JButton("Reorder");
        styleButton(reorderButton, new Color(40, 167, 69));
        reorderButton.addActionListener(e -> {
            if (disableNewOrder) {
                String[] options = {"View Order"};
                int choice = JOptionPane.showOptionDialog(
                    mainFrame,
                    "You have an active order!",
                    "Active Order",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );
                if (choice == 0) {
                    mainFrame.switchTo("OrderProgressPage");
                }
            } else {
                boolean success = orderController.reorder(order.getId());
                if (success) {
                    String[] options = {"View Order"};
                    int choice = JOptionPane.showOptionDialog(
                        mainFrame,
                        "Order placed successfully!",
                        "Order Status",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                    );
                    if (choice == 0) {
                        mainFrame.switchTo("OrderProgressPage");
                    }
                } else {
                    JOptionPane.showMessageDialog(
                        mainFrame,
                        "Failed to place order",
                        "Order Status",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        buttonPanel.add(viewDetailButton);
        buttonPanel.add(reorderButton);
        card.add(buttonPanel, BorderLayout.SOUTH);

        return card;
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
    // カードの丸みのあるボーダー（内側のパディングも指定）
    private class RoundedBorder extends AbstractBorder {
        private int radius;
        private int padding;
        public RoundedBorder(int radius, int padding) {
            this.radius = radius;
            this.padding = padding;
        }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.GRAY);
            g2.drawRoundRect(x + padding, y + padding, width - 2 * padding - 1, height - 2 * padding - 1, radius, radius);
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2 + padding, radius / 2 + padding, radius / 2 + padding, radius / 2 + padding);
        }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius / 2 + padding;
            return insets;
        }
    }


    public static void showReviewDialog(JFrame mainFrame, String venderName, String runnerName) {
        JDialog reviewDialog = new JDialog(mainFrame, "Leave a Review", true);
        reviewDialog.setSize(350, 600);
        reviewDialog.setLayout(new BorderLayout());
        reviewDialog.setLocationRelativeTo(mainFrame);

        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel venderLabel = new JLabel("Vender Store: " + venderName);
        venderLabel.setFont(new Font("Arial", Font.BOLD, 16));
        reviewPanel.add(venderLabel);

        JSlider venderRating = new JSlider(1, 5, 3);
        venderRating.setMajorTickSpacing(1);
        venderRating.setPaintTicks(true);
        venderRating.setPaintLabels(true);
        reviewPanel.add(venderRating);

        JTextArea venderReviewText = new JTextArea(3, 20);
        venderReviewText.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        reviewPanel.add(venderReviewText);

        JLabel runnerLabel = new JLabel("Delivery Runner: " + runnerName);
        runnerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        reviewPanel.add(runnerLabel);

        JSlider runnerRating = new JSlider(1, 5, 3);
        runnerRating.setMajorTickSpacing(1);
        runnerRating.setPaintTicks(true);
        runnerRating.setPaintLabels(true);
        reviewPanel.add(runnerRating);

        JTextArea runnerReviewText = new JTextArea(3, 20);
        runnerReviewText.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        reviewPanel.add(runnerReviewText);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> reviewDialog.dispose());
        JButton postButton = new JButton("Post");
        postButton.addActionListener(e -> {
            reviewDialog.dispose();
            JOptionPane.showMessageDialog(mainFrame, "Review Submitted!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(postButton);
        reviewPanel.add(buttonPanel);

        reviewDialog.add(reviewPanel);
        reviewDialog.setVisible(true);
    }
}
