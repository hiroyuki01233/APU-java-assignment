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
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Order History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.DARK_GRAY);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        JButton goBackButton = new JButton("Back to Dashboard");
        styleButton(goBackButton, new Color(100, 181, 246));
        goBackButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));
        goBackButton.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(goBackButton);
        centerPanel.add(Box.createVerticalStrut(30));

        JPanel cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));
        cardListPanel.setBackground(Color.WHITE);

        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                JPanel card = createOrderCard(order);
                cardListPanel.add(card);
                cardListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        } else {
            JLabel emptyLabel = new JLabel("No orders found.");
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            emptyLabel.setForeground(Color.GRAY);
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardListPanel.add(emptyLabel);
        }

        JScrollPane scrollPane = new JScrollPane(cardListPanel);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

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
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setMaximumSize(new Dimension(400, 200));

        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JLabel dateLabel = new JLabel("Date: " + order.getCreatedAt().format(formatter));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JLabel storeLabel = new JLabel("Store: " + order.getVender().getStoreName());
        storeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel totalLabel = new JLabel("Total: RM" + order.getTotalPriceAllIncludes());
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(100, 181, 246));

        detailsPanel.add(storeLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createVerticalStrut(5));
        detailsPanel.add(totalLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setOpaque(false);

        JButton viewDetailButton = new JButton("View Detail");
        styleButton(viewDetailButton, new Color(100, 181, 246));
        viewDetailButton.addActionListener(e -> {

            // 注文詳細のポップアップダイアログを作成（モーダル）
            JDialog detailDialog = new JDialog(mainFrame, "Order Detail", true);
            detailDialog.setLayout(new BorderLayout());

            // 注文アイテム一覧表示用のパネル（縦並び）
            JPanel itemsPanel = new JPanel();
            itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // order.getItems() から各アイテムの情報を表示
            for (OrderItem item : order.getItems()) {
                String itemName = item.getMenu().getName();
                int amount = item.getAmount();
                double unitPrice = item.getMenu().getPrice();
                double totalItemPrice = unitPrice * amount;
                JLabel itemLabel = new JLabel(itemName + "  x" + amount
                        + " RM" + totalItemPrice);
                itemsPanel.add(itemLabel);
            }

            // 注文のサマリー情報表示用パネル
            JPanel summaryPanel = new JPanel();
            summaryPanel.setLayout(new GridLayout(0, 1, 5, 5));
            summaryPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            summaryPanel.add(new JLabel("Store: " + order.getVender().getStoreName()));
            summaryPanel.add(new JLabel("Order Date: " + order.getCreatedAt().toString()));
            summaryPanel.add(new JLabel("Food Price: RM" + order.getTotalPrice()));
            summaryPanel.add(new JLabel("Charge fee: RM" + order.getCommission()));
            summaryPanel.add(new JLabel("Delivery fee: RM" + order.getDeliveryFee()));
            summaryPanel.add(new JLabel("Tax: RM" + order.getTax()));
            summaryPanel.add(new JLabel("Total Price: RM" + order.getTotalPriceAllIncludes()));

            // ボタンパネルを作成
            JPanel buttonPanelPopup = new JPanel();
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e1 -> detailDialog.dispose()); // ダイアログを閉じる
            buttonPanelPopup.add(closeButton);

            // メインコンテンツパネルにスクロール可能な注文アイテム一覧とサマリーを配置
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.add(new JScrollPane(itemsPanel));
            contentPanel.add(summaryPanel);
            contentPanel.add(buttonPanelPopup);

            detailDialog.add(contentPanel);
            detailDialog.pack();
            detailDialog.setLocationRelativeTo(mainFrame);
            detailDialog.setVisible(true);
        });

        JButton reorderButton = new JButton("Reorder");
        styleButton(reorderButton, Color.GREEN);
        reorderButton.addActionListener(e -> {
            if (disableNewOrder){
                String[] options = {"View Order"};
                int choice = JOptionPane.showOptionDialog(
                        mainFrame,
                        "Your have other order currently!",
                        "You have an order",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]
                );
                // If "View Order" is clicked, switch the page
                if (choice == 0) {
                    mainFrame.switchTo("OrderProgressPage");
                }
            }else{
                boolean success = orderController.reorder(order.getId());
                if(success) {
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
                    // If "View Order" is clicked, switch the page
                    if (choice == 0) {
                        mainFrame.switchTo("OrderProgressPage");
                    }
                } else  {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "Order failed",
                            "Order Status",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        JButton reviewButton = new JButton("Review");
        styleButton(reviewButton, new Color(255, 193, 7)); // Yellow color for emphasis
        reviewButton.addActionListener(e -> {
            this.showReviewDialog(mainFrame, order.getVender().getStoreName(), "test");
        });
        buttonPanel.add(reviewButton);


        buttonPanel.add(viewDetailButton);
        buttonPanel.add(reorderButton);
        card.add(buttonPanel, BorderLayout.SOUTH);


        return card;
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
