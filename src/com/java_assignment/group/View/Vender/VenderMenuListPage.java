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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel with modern styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(100, 149, 237));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(80, 119, 190)),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        JLabel titleLabel = new JLabel("Menu Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Main content panel with card-like styling
        JPanel contentPanel = new JPanel(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Table setup with modern styling
        String[] columnNames = {"Item ID", "Name", "Price", "Description", "Delete"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!comp.getBackground().equals(getSelectionBackground())) {
                    comp.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }
                return comp;
            }
        };

        table.setFillsViewportHeight(true);
        table.setRowHeight(45);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(100, 149, 237));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setSelectionBackground(new Color(100, 149, 237, 50));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(300);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);

        table.getColumn("Delete").setCellRenderer(new ButtonRenderer());
        table.getColumn("Delete").setCellEditor(new ButtonEditor(new JCheckBox(), "Delete"));

        // Scroll pane with modern styling
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Button Panel with modern styling
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton createButton = createStyledButton("Add New", new Color(40, 167, 69));
        JButton saveButton = createStyledButton("Save Changes", new Color(0, 123, 255));
        JButton backButton = createStyledButton("Back", new Color(108, 117, 125));

        backButton.addActionListener(e -> mainFrame.switchTo("VenderDashboard"));
        saveButton.addActionListener(e -> saveMenu());
        createButton.addActionListener(e -> addEmptyRow());

        buttonPanel.add(createButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }

    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(background.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(background);
            }
        });

        return button;
    }

    private void onLoadMenu() {
        try {
            authController = new AuthController();
            menuController = new MenuController();
            venderController = new VenderController();

            if (authController.getCurrentUser() == null) {
                return;
            }

            vender = venderController.getVenderByBaseUserId(authController.getCurrentUser().getId());
            menuItems = menuController.getMenusByVender(vender.getId());
            tableModel.setRowCount(0);

            for (Menu menuItem : menuItems) {
                addMenuRow(menuItem);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading menus: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadMenu();
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

    class ButtonRenderer extends JButton implements TableCellRenderer {
        private static final Color BUTTON_COLOR = new Color(220, 53, 69);
        private static final Color HOVER_COLOR = new Color(200, 35, 51);

        public ButtonRenderer() {
            setOpaque(true);
            setBackground(Color.WHITE);
            setForeground(BUTTON_COLOR);
            setBorderPainted(false);
            setFont(new Font("Arial", Font.BOLD, 12));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(BUTTON_COLOR);
                    setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(Color.WHITE);
                    setForeground(BUTTON_COLOR);
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            if (isSelected) {
                setBackground(BUTTON_COLOR);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(BUTTON_COLOR);
            }
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private boolean clicked;
        private int row;

        public ButtonEditor(JCheckBox checkBox, String label) {
            super(checkBox);
            button = new JButton(label);
            button.setOpaque(true);
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(220, 53, 69));
            button.setBorderPainted(false);
            button.setFont(new Font("Arial", Font.BOLD, 12));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.addActionListener(e -> fireEditingStopped());

            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(220, 53, 69));
                    button.setForeground(Color.WHITE);
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(Color.WHITE);
                    button.setForeground(new Color(220, 53, 69));
                }
            });
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
            if (row < 0 || row >= tableModel.getRowCount()) {
                JOptionPane.showMessageDialog(this.getComponent(), "Please select a row to delete.");
                return;
            }

            menuController.deleteMenu(menuItems.get(row).getId());
            SwingUtilities.invokeLater(() -> {
                if (row < tableModel.getRowCount()) {
                    tableModel.removeRow(row);
                }
            });
        }
    }
}
