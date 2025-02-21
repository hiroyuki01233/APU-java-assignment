package com.java_assignment.group.View.Vender;

import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Model.OrderItem;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class OrderList extends JPanel {
    private List<Order> allOrders;  // 全件の注文リスト
    private Vender vender;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private OrderController orderController;
    private String currentFilter = "All";  // 現在のフィルター (初期値：全件)

    public OrderList(Vender vender, ActionListener onChangeStatus) {
        this.vender = vender;

        try {
            orderController = new OrderController();
            this.allOrders = orderController.getOrdersByVender(vender.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setLayout(new BorderLayout());

        // タブパネルの作成（フィルター用）
        String[] filters = {"Today", "This week", "This month", "All"};
        JTabbedPane tabbedPane = new JTabbedPane();
        for (String filter : filters) {
            JPanel panel = new JPanel();
            panel.setPreferredSize(new Dimension(800, 1)); // 幅800px, 高さ30pxに指定
            tabbedPane.addTab(filter, panel);
        }

        // タブが変更された際にフィルターを更新
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                currentFilter = filters[selectedIndex];
                populateTable();
            }
        });
        add(tabbedPane, BorderLayout.NORTH);

        // テーブルのセットアップ
        String[] columns = {"Order ID", "Status", "Created At", "Accept", "Decline", "ReadyToPickup", "Detail"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 3; // ボタン部分のみ編集可能
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFillsViewportHeight(true);
        orderTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        // 各カラムの幅設定（必要に応じて調整してください）
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Order ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Status
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Created At
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Accept Button
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(90);  // Decline Button
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(110); // ReadyToPickup Button
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Detail Button

        // ボタン用のレンダラーとエディターを設定
        orderTable.getColumn("Accept").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Accept").setCellEditor(new ButtonEditor(new JCheckBox()));
        orderTable.getColumn("Decline").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Decline").setCellEditor(new ButtonEditor(new JCheckBox()));
        orderTable.getColumn("ReadyToPickup").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("ReadyToPickup").setCellEditor(new ButtonEditor(new JCheckBox()));
        orderTable.getColumn("Detail").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Detail").setCellEditor(new ButtonEditor(new JCheckBox()));

        populateTable();

        JScrollPane scrollPane = new JScrollPane(orderTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        add(scrollPane, BorderLayout.CENTER);
    }

    // 注文データを現在のフィルターに基づいてテーブルに表示
    private void populateTable() {
        tableModel.setRowCount(0); // 既存の行をクリア

        // 最新の注文データを再取得（必要に応じて）
        try {
            this.allOrders = orderController.getOrdersByVender(vender.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 現在時刻とフィルターのための開始／終了時刻を計算
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        // 今日の開始・終了時刻
        Calendar startCal = (Calendar) cal.clone();
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        Date startOfDay = startCal.getTime();

        Calendar endCal = (Calendar) cal.clone();
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Date endOfDay = endCal.getTime();

        // 今週の開始・終了時刻（カレンダーのfirstDayOfWeekを利用）
        Calendar weekStart = (Calendar) cal.clone();
        weekStart.set(Calendar.DAY_OF_WEEK, weekStart.getFirstDayOfWeek());
        weekStart.set(Calendar.HOUR_OF_DAY, 0);
        weekStart.set(Calendar.MINUTE, 0);
        weekStart.set(Calendar.SECOND, 0);
        weekStart.set(Calendar.MILLISECOND, 0);
        Date startOfWeek = weekStart.getTime();

        Calendar weekEnd = (Calendar) weekStart.clone();
        weekEnd.add(Calendar.DAY_OF_WEEK, 6);
        weekEnd.set(Calendar.HOUR_OF_DAY, 23);
        weekEnd.set(Calendar.MINUTE, 59);
        weekEnd.set(Calendar.SECOND, 59);
        weekEnd.set(Calendar.MILLISECOND, 999);
        Date endOfWeek = weekEnd.getTime();

        // 今月の開始・終了時刻
        Calendar monthStart = (Calendar) cal.clone();
        monthStart.set(Calendar.DAY_OF_MONTH, 1);
        monthStart.set(Calendar.HOUR_OF_DAY, 0);
        monthStart.set(Calendar.MINUTE, 0);
        monthStart.set(Calendar.SECOND, 0);
        monthStart.set(Calendar.MILLISECOND, 0);
        Date startOfMonth = monthStart.getTime();

        Calendar monthEnd = (Calendar) cal.clone();
        monthEnd.set(Calendar.DAY_OF_MONTH, monthStart.getActualMaximum(Calendar.DAY_OF_MONTH));
        monthEnd.set(Calendar.HOUR_OF_DAY, 23);
        monthEnd.set(Calendar.MINUTE, 59);
        monthEnd.set(Calendar.SECOND, 59);
        monthEnd.set(Calendar.MILLISECOND, 999);
        Date endOfMonth = monthEnd.getTime();

        // フィルターに合わせて注文を抽出
        List<Order> filteredOrders = new ArrayList<>();
        for (Order order : allOrders) {
            Date orderDate = Date.from(order.getCreatedAt().toInstant(ZoneOffset.UTC)); // 注文作成日時（Date型と仮定）
            boolean include = false;
            switch (currentFilter) {
                case "Today":
                    include = (orderDate.compareTo(startOfDay) >= 0 && orderDate.compareTo(endOfDay) <= 0);
                    break;
                case "This week":
                    include = (orderDate.compareTo(startOfWeek) >= 0 && orderDate.compareTo(endOfWeek) <= 0);
                    break;
                case "This month":
                    include = (orderDate.compareTo(startOfMonth) >= 0 && orderDate.compareTo(endOfMonth) <= 0);
                    break;
                case "All":
                default:
                    include = true;
                    break;
            }
            if (include) {
                filteredOrders.add(order);
            }
        }

        // フィルタリングされた注文をテーブルに追加
        for (Order order : filteredOrders) {
            // Acceptボタン（状態が "Ordered" の場合のみ有効）
            JButton acceptButton = new JButton("Accept");
            acceptButton.setPreferredSize(new Dimension(80, 25));
            acceptButton.setEnabled(order.getCurrentStatus().equals("Ordered"));
            acceptButton.addActionListener(e -> updateOrderStatus(order, "Preparing"));

            // Declineボタン（状態が "Ordered" の場合のみ有効）
            JButton declineButton = new JButton("Decline");
            declineButton.setPreferredSize(new Dimension(80, 25));
            declineButton.setEnabled(order.getCurrentStatus().equals("Ordered"));
            declineButton.addActionListener(e -> updateOrderStatus(order, "Declined"));

            // Readyボタン（状態が "Preparing" または "Preparing-runnerWaiting" の場合のみ有効）
            JButton readyButton = new JButton("Ready");
            readyButton.setPreferredSize(new Dimension(80, 25));
            readyButton.setEnabled(order.getCurrentStatus().equals("Preparing") ||
                    order.getCurrentStatus().equals("Preparing-runnerWaiting"));
            readyButton.addActionListener(e -> updateOrderStatus(order, "ReadyToPickup"));

            // Detailボタン（詳細表示用）
            JButton detailButton = new JButton("Detail");
            detailButton.setPreferredSize(new Dimension(80, 25));
            detailButton.addActionListener(e -> showOrderDetail(order));

            Object[] row = {
                    order.getId(),
                    order.getCurrentStatus(),
                    order.getCreatedAt(),
                    acceptButton,
                    declineButton,
                    readyButton,
                    detailButton
            };

            tableModel.addRow(row);
        }
    }

    // 注文ステータスの更新とテーブルの再描画
    private void updateOrderStatus(Order order, String newStatus) {
        try {
            boolean success = orderController.updateOrderStatus(order.getId(), newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, "Order status updated to: " + newStatus);
                order.setCurrentStatus(newStatus);
                populateTable();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating order status: " + ex.getMessage());
        }
    }

    // 注文詳細情報（注文アイテムの一覧を含む）の表示
    private void showOrderDetail(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(order.getId()).append("\n")
                .append("Status: ").append(order.getCurrentStatus()).append("\n")
                .append("Created At: ").append(order.getCreatedAt()).append("\n\n")
                .append("Order Items:\n");

        List<OrderItem> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                String menuDetail = "Menu: " + item.getMenu().getName();
                details.append(menuDetail)
                        .append(" | Amount: ").append(item.getAmount())
                        .append("\n");
            }
        } else {
            details.append("No items found.");
        }

        JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }

    // ボタンコンポーネント用のカスタムレンダラー
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JButton) {
                return (JButton) value;
            }
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // ボタンコンポーネント用のカスタムエディター
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            if (value instanceof JButton) {
                button = (JButton) value;
            }
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return button;
        }
    }
}
