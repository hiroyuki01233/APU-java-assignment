package com.java_assignment.group.View.Customer;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.CompoundBorder;
import java.awt.*;
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
        try {
            this.orderController = new OrderController();
            this.authController = new AuthController();
            this.user = authController.getCurrentUser();
            if (user != null){
                this.disableNewOrder = !(null == orderController.getCurrentOrder(user.getId()));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // このメソッドはページ表示時に呼び出してください
    public void onPageDisplayed() {
        initUI();
        System.out.println("Order History page loaded");
    }

    private void initUI() {
        removeAll();
        // カードを縦並びに配置するためBoxLayoutを利用
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // 背景は薄いグレーなど、カードとのコントラストをつける
        setBackground(new Color(240, 240, 240));

        List<Order> orders = null;
        try {
            orders = orderController.getOrdersByUser(user.getId());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Order History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));

        JButton goBackButton = new JButton();
        goBackButton.setText("go back");
        goBackButton.addActionListener(e -> mainFrame.switchTo("CustomerDashboard"));
        goBackButton.setAlignmentX(CENTER_ALIGNMENT);
        centerPanel.add(goBackButton);
        centerPanel.add(Box.createVerticalStrut(30));

        JPanel cardListPanel = new JPanel();
        cardListPanel.setLayout(new BoxLayout(cardListPanel, BoxLayout.Y_AXIS));

        if (orders != null && !orders.isEmpty()) {
            for (Order order : orders) {
                JPanel card = createOrderCard(order);
                cardListPanel.add(card);
                // カード間にスペースを追加
                cardListPanel.add(Box.createRigidArea(new Dimension(0, 15)));
            }
        } else {
            JLabel emptyLabel = new JLabel("No orders found.");
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            cardListPanel.add(emptyLabel);
        }

        JScrollPane scrollPane = new JScrollPane(cardListPanel);
        scrollPane.setPreferredSize(new Dimension(330, 600)); // 必要に応じて調整

        centerPanel.add(scrollPane);

        add(centerPanel);

        revalidate();
        repaint();
    }

    private JPanel createOrderCard(Order order) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        // 丸みのある角と影の効果をボーダーで実現
        card.setBorder(new CompoundBorder(new ShadowBorder(), new RoundedBorder(15, 10)));

        // 注文内容の詳細パネル
        JPanel detailsPanel = new JPanel();
        detailsPanel.setOpaque(false);
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        JLabel dateLabel = new JLabel("Date: " + order.getCreatedAt().format(formatter));
        JLabel storeLabel = new JLabel("Store: " + order.getVender().getStoreName());
        JLabel totalLabel = new JLabel("Total: RM" + order.getTotalPriceAllIncludes());
        detailsPanel.add(dateLabel);
        detailsPanel.add(storeLabel);
        detailsPanel.add(totalLabel);
        card.add(detailsPanel, BorderLayout.CENTER);

        // ボタンパネル（「View Detail」と「Reorder」ボタン）
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        JButton viewDetailButton = new JButton("View Detail");

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

    // カードに影をつけるためのカスタムボーダー
    private class ShadowBorder extends AbstractBorder {
        private int shadowSize = 5;
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 半透明の黒色で影を描画
            g2d.setColor(new Color(0, 0, 0, 50));
            g2d.fillRoundRect(x + shadowSize, y + shadowSize, width - shadowSize, height - shadowSize, 15, 15);
            g2d.dispose();
        }
        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = shadowSize;
            return insets;
        }
    }
}
