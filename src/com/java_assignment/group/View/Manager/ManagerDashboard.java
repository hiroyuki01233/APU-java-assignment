package com.java_assignment.group.View.Manager;

import com.java_assignment.group.Controller.DeliveryRunnerController;
import com.java_assignment.group.Controller.MenuController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Controller.ReviewController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.DeliveryRunner;
import com.java_assignment.group.Model.Menu;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.Review;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerDashboard extends JPanel {
    private MainFrame mainFrame;
    private OrderController orderController;
    private VenderController venderController;
    private DeliveryRunnerController deliveryRunnerController;
    private ReviewController reviewController;
    private MenuController menuController;

    public ManagerDashboard(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // 各コントローラの初期化（例外処理は適宜実装）
        try {
            orderController = new OrderController();
            venderController = new VenderController();
            deliveryRunnerController = new DeliveryRunnerController();
            reviewController = new ReviewController();
            menuController = new MenuController();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers: " + ex.getMessage());
        }

        // タブ付きパネルにより各機能の画面を表示
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Revenue Dashboard", createRevenuePanel());
        tabbedPane.addTab("Delivery Performance", createDeliveryPerformancePanel());
        tabbedPane.addTab("Customer Complaints", createCustomerComplaintsPanel());
        tabbedPane.addTab("Vendor Items", createVendorItemsPanel());

        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * 【Revenue Dashboard】
     * 各ベンダーの受注データから売上合計を計算して表示するパネル
     */
    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 全ベンダーの取得
        List<Vender> vendors = venderController.getAllVenders();

        // テーブルのカラム設定（例：Vendor ID, Vendor Name, Total Revenue）
        String[] columnNames = {"Vendor ID", "Vendor Name", "Total Revenue"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (Vender v : vendors) {
            // 各ベンダーの注文情報から総売上を計算（Order.getTotalAmount() の実装が前提）
            List<Order> vendorOrders = orderController.getOrdersByVender(v.getId());
            double totalRevenue = 0.0;
            for (Order o : vendorOrders) {
                if (!(o.getCurrentStatus().equals("Cancel") || o.getCurrentStatus().equals("ForceCancel"))){
                    totalRevenue += o.getTotalPrice();
                }
            }
            model.addRow(new Object[]{v.getId(), v.getStoreName(), totalRevenue});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 【Delivery Runner Performance】
     * 配達員の平均評価やレビュー件数などを表示するパネル
     */
    private JPanel createDeliveryPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 全配達員の取得
        List<DeliveryRunner> runners = deliveryRunnerController.getAllDeliveryRunner();

        // テーブルのカラム設定（Runner ID, Runner Name, Average Rating, Review Count）
        String[] columnNames = {"Runner ID", "Runner Name", "Average Rating", "Review Count"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        for (DeliveryRunner runner : runners) {
            double avgRating = runner.getAverageRating(); // DeliveryRunner は averageRating を保持している前提
            int reviewCount = (runner.getReviews() != null) ? runner.getReviews().size() : 0;
            model.addRow(new Object[]{runner.getId(), runner.getFirstName() + " " + runner.getLastName(), avgRating, reviewCount});
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 【Customer Complaints】
     * 低評価レビュー（ここでは rating ≤ 2）を苦情として表示し、Resolve ボタン押下で削除（＝解決）するパネル
     */
    private JPanel createCustomerComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        List<Review> allReviews = reviewController.getAllReviews();
        // 低評価レビューを苦情として抽出
        List<Review> complaints = allReviews.stream()
                .filter(r -> r.getRating() <= 2)
                .collect(Collectors.toList());

        // テーブルカラム：Review ID, User ID, Target User, Rating, Text, Created At, Action
        String[] columnNames = {"Review ID", "User ID", "Target User", "Rating", "Review Text", "Created At", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Action カラムのみ編集可能とする
                return column == 6;
            }
        };

        for (Review r : complaints) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getUserId(),
                    r.getTargetUserId(),
                    r.getRating(),
                    r.getText(),
                    r.getCreatedAt(),
                    "Resolve"
            });
        }

        JTable table = new JTable(model);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            public Object getCellEditorValue() {
                int row = table.getSelectedRow();
                String reviewId = (String) model.getValueAt(row, 0);
                // 解決処理として対象レビューを削除（実際には状態更新などを実施可能）
                boolean success = reviewController.deleteReview(reviewId);
                if (success) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(panel, "Complaint resolved.");
                }
                return "Resolve";
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    /**
     * 【Vendor Items】
     * 各ベンダーの商品リスティングを表示し、不適切な商品を Remove ボタンで削除できるパネル
     */
    private JPanel createVendorItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // 全メニュー商品を取得（MenuController.getAllMenuItems() の実装が前提）
        List<Menu> items = menuController.getMenus();

        // テーブルカラム：Item ID, Vendor ID, Item Name, Description, Price, Action
        String[] columnNames = {"Item ID", "Vendor ID", "Item Name", "Description", "Price", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        for (Menu item : items) {
            model.addRow(new Object[]{
                    item.getId(),
                    item.getVenderId(),
                    item.getName(),
                    item.getDescription(),
                    item.getPrice(),
                    "Remove"
            });
        }

        JTable table = new JTable(model);
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox()) {
            @Override
            public Object getCellEditorValue() {
                int row = table.getSelectedRow();
                String itemId = (String) model.getValueAt(row, 0);
                boolean success = menuController.deleteMenu(itemId);
                if (success) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(panel, "Menu item removed.");
                }
                return "Remove";
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}

/**
 * JTable のセル内にボタンを表示するための Renderer
 */
class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer() {
        setOpaque(true);
    }
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        setText((value == null) ? "" : value.toString());
        return this;
    }
}

/**
 * JTable のセル内ボタンのエディター
 */
class ButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
        label = (value == null) ? "" : value.toString();
        button.setText(label);
        isPushed = true;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        isPushed = false;
        return label;
    }

    @Override
    public boolean stopCellEditing() {
        isPushed = false;
        return super.stopCellEditing();
    }
}
