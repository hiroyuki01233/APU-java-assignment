package com.java_assignment.group.View.Vender;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.MenuController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.*;
import com.java_assignment.group.Model.Menu;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.util.List;


public class VenderMenuListPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private MenuController menuController;
    private Vender vender;
    private VenderController venderController;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Menu> menuItems;


    public VenderMenuListPage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout());

        // テーブルのカラム設定
        String[] columnNames = {"Item ID", "Name", "Price", "Description", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 上部のボタンパネル
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.switchTo("VenderDashboard"));
        topPanel.add(backButton);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveMenu());
        topPanel.add(saveButton);

        JButton createButton = new JButton("Add new");
        createButton.addActionListener(e -> addEmptyRow());
        topPanel.add(createButton);

        add(topPanel, BorderLayout.NORTH);
    }

    private void onLoadMenu() {
        try {
            authController = new AuthController();
            menuController = new MenuController();
            venderController = new VenderController();

            System.out.println(authController.getCurrentUser());

            if (authController.getCurrentUser() == null) {
                return;
            }

            vender = venderController.getVenderByBaseUserId(authController.getCurrentUser().getId());
            menuItems = menuController.getMenusByVender(vender.getId());
            tableModel.setRowCount(0); // 既存のデータをクリア

            System.out.println(menuItems);
            System.out.println("menu items here");

            for (Menu menuItem : menuItems) {
                addMenuRow(menuItem);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading menus: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadMenu();
        System.out.println("Menu loaded");
    }

    private void addMenuRow(Menu menu) {
        tableModel.addRow(new Object[]{menu.getId(), menu.getName(), menu.getPrice(), menu.getDescription(), "Delete"});
    }

    private void addEmptyRow() {
        tableModel.addRow(new Object[]{"", "", "", "", "Delete"});
    }

    private void saveMenu() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String menuId = (String) tableModel.getValueAt(i, 0);
            Menu menu = menuController.getMenuById(menuId);

            if (menu == null) {
                menu = new Menu();
                menu.setVenderId(vender.getId());
                menu.setName((String) tableModel.getValueAt(i, 1));
                menu.setPrice(Double.valueOf((String) tableModel.getValueAt(i, 2)));
                menu.setDescription((String) tableModel.getValueAt(i, 3));
                menuController.addMenu(menu);
            }

            menu.setName((String) tableModel.getValueAt(i, 1));
            menu.setPrice(Double.valueOf((String) tableModel.getValueAt(i, 2)));
            menu.setDescription((String) tableModel.getValueAt(i, 3));

            if (menu.getId() != null) {
                menuController.updateMenu(menu);
            }

        }

        JOptionPane.showMessageDialog(this, "Menu saved successfully.");
    }

    // JButtonを正しく表示するためのカスタムレンダラー
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    // JButtonをクリック可能にするためのエディター
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            button = new JButton(label);
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            button.setText("Delete");
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                deleteRow(row);
            }
            clicked = false;
            return "Delete";
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        private void deleteRow(int row) {
            menuController.deleteMenu(menuItems.get(row).getId());

            if (row < 0 || row >= tableModel.getRowCount()) {
                JOptionPane.showMessageDialog(this.getComponent(), "削除する行を選択してください。");
                return;
            }

            SwingUtilities.invokeLater(() -> {
                if (row < tableModel.getRowCount()) {
                    tableModel.removeRow(row);
                }
            });
        }
    }
}
