package com.java_assignment.group.Component;

import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Model.OrderItem;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

public class OrderList extends JPanel {
    private List<Order> orders;
    private Vender vender;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private OrderController orderController;

    public OrderList(Vender vender, ActionListener onChangeStatus) {
        this.vender = vender;

        try {
            orderController = new OrderController();
            this.orders = orderController.getOrdersByVender(vender.getId());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        setLayout(new BorderLayout());

        // Updated columns array to include "Detail" column
        String[] columns = {"Order ID", "Status", "Created At", "Accept", "Decline", "Ready", "Detail"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only button columns (from index 3 onward) are editable to capture button clicks.
                return column >= 3;
            }
        };

        orderTable = new JTable(tableModel);
        // Set custom renderer and editor for button columns so that buttons are displayed
        orderTable.getColumn("Accept").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Accept").setCellEditor(new ButtonEditor(new JCheckBox()));
        orderTable.getColumn("Decline").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Decline").setCellEditor(new ButtonEditor(new JCheckBox()));
        orderTable.getColumn("Ready").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Ready").setCellEditor(new ButtonEditor(new JCheckBox()));
        orderTable.getColumn("Detail").setCellRenderer(new ButtonRenderer());
        orderTable.getColumn("Detail").setCellEditor(new ButtonEditor(new JCheckBox()));

        populateTable();

        JScrollPane scrollPane = new JScrollPane(orderTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void populateTable() {
        tableModel.setRowCount(0); // Clear existing rows

        try{
            this.orders = orderController.getOrdersByVender(vender.getId());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Order order : orders) {
            // Create Accept button; enabled only when status is "NEW"
            JButton acceptButton = new JButton("Accept");
            acceptButton.setPreferredSize(new Dimension(80, 25));
            acceptButton.setEnabled(order.getCurrentStatus().equals("NEW"));
            acceptButton.addActionListener(e -> updateOrderStatus(order, "Preparing"));

            // Create Decline button; enabled only when status is "NEW"
            JButton declineButton = new JButton("Decline");
            declineButton.setPreferredSize(new Dimension(80, 25));
            declineButton.setEnabled(order.getCurrentStatus().equals("NEW"));
            declineButton.addActionListener(e -> updateOrderStatus(order, "Declined"));

            // Create Ready button; enabled only when status is "Preparing"
            JButton readyButton = new JButton("Ready");
            readyButton.setPreferredSize(new Dimension(80, 25));
            readyButton.setEnabled(order.getCurrentStatus().equals("Preparing"));
            readyButton.addActionListener(e -> updateOrderStatus(order, "Ready"));

            // Create Detail button to view order details
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

    // Updates the order status and refreshes the table.
    private void updateOrderStatus(Order order, String newStatus) {
        try {
            boolean success = orderController.updateOrderStatus(order.getId(), newStatus);
            if (success) {
                JOptionPane.showMessageDialog(this, "Order status updated to: " + newStatus);
                order.setCurrentStatus(newStatus);
                populateTable();  // Refresh the table to reflect the updated status
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update order status.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating order status: " + ex.getMessage());
        }
    }

    // Displays a popup with detailed order information including a list of Order Items.
    private void showOrderDetail(Order order) {
        StringBuilder details = new StringBuilder();
        details.append("Order ID: ").append(order.getId()).append("\n")
                .append("Status: ").append(order.getCurrentStatus()).append("\n")
                .append("Created At: ").append(order.getCreatedAt()).append("\n\n")
                .append("Order Items:\n");

        List<OrderItem> items = order.getItems();
        if (items != null && !items.isEmpty()) {
            for (OrderItem item : items) {
                // Option 1: Display Menu ID and Amount
                String menuDetail = "Menu: " + item.getMenu().getName();
                // Option 2 (if you have a getter for the Menu name):
                // String menuDetail = "Menu: " + item.getMenu().getName();
                details.append(menuDetail)
                        .append(" | Amount: ").append(item.getAmount())
                        .append("\n");
            }
        } else {
            details.append("No items found.");
        }

        JOptionPane.showMessageDialog(this, details.toString(), "Order Details", JOptionPane.INFORMATION_MESSAGE);
    }


    // Custom renderer to display JButton components in table cells.
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

    // Custom editor to allow interaction with JButton components in table cells.
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
