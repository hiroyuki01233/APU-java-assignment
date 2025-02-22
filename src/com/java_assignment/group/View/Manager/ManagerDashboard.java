package com.java_assignment.group.View.Manager;

import com.java_assignment.group.Controller.*;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.DeliveryRunner;
import com.java_assignment.group.Model.Menu;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.Review;
import com.java_assignment.group.Model.Vender;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerDashboard extends JPanel {
    private MainFrame mainFrame;
    private OrderController orderController;
    private VenderController venderController;
    private DeliveryRunnerController deliveryRunnerController;
    private ReviewController reviewController;
    private MenuController menuController;
    private AuthController authController;

    // Modern color scheme
    private static final Color PRIMARY_COLOR = new Color(63, 81, 181);    // Deep Blue
    private static final Color ACCENT_COLOR = new Color(255, 64, 129);    // Material Pink
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(33, 33, 33);
    private static final Color TEXT_SECONDARY = new Color(117, 117, 117);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);    // Bootstrap Red
    private static final int PANEL_PADDING = 25;    // Consistent padding
    private static final int SPACING = 20;          // Consistent spacing

    public ManagerDashboard(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(SPACING, SPACING));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING));

        try {
            initializeControllers();
            setupUI();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers: " + ex.getMessage());
        }
    }

    private void initializeControllers() throws IOException {
        orderController = new OrderController();
        venderController = new VenderController();
        deliveryRunnerController = new DeliveryRunnerController();
        reviewController = new ReviewController();
        menuController = new MenuController();
        authController = new AuthController();
    }

    private void setupUI() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Main Content (Tabbed Pane)
        JTabbedPane tabbedPane = createStyledTabbedPane();
        tabbedPane.addTab("Revenue Dashboard", createRevenuePanel());
        tabbedPane.addTab("Delivery Performance", createDeliveryPerformancePanel());
        tabbedPane.addTab("Customer Complaints", createCustomerComplaintsPanel());
        tabbedPane.addTab("Vendor Items", createVendorItemsPanel());
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(SPACING, 0));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, PRIMARY_COLOR.darker()),
            new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING)
        ));

        // Title
        JLabel titleLabel = new JLabel("Manager Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Logout Button
        JButton logoutButton = createStyledButton("Logout", ACCENT_COLOR);
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });
        headerPanel.add(logoutButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JTabbedPane createStyledTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(BACKGROUND_COLOR);
        tabbedPane.setForeground(TEXT_PRIMARY);

        // Add custom tab styling
        tabbedPane.addChangeListener(e -> {
            int selectedIndex = tabbedPane.getSelectedIndex();
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component comp = tabbedPane.getTabComponentAt(i);
                JLabel label;
                if (comp instanceof JLabel) {
                    label = (JLabel) comp;
                } else {
                    label = new JLabel(tabbedPane.getTitleAt(i));
                    label.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                    label.setOpaque(true);
                }
                
                if (i == selectedIndex) {
                    label.setForeground(new Color(40, 167, 69));
                    label.setBackground(new Color(40, 167, 69, 20));
                } else {
                    label.setForeground(TEXT_PRIMARY);
                    label.setBackground(BACKGROUND_COLOR);
                }
                
                if (comp == null) {
                    tabbedPane.setTabComponentAt(i, label);
                }
            }
        });

        return tabbedPane;
    }

    private JButton createStyledButton(String text, Color baseColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(baseColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
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

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(245, 245, 245));
                }
                return comp;
            }
        };

        // Style the table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(header.getWidth(), 50));

        // Style the table
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(PRIMARY_COLOR.brighter());
        table.setSelectionForeground(Color.WHITE);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(15, 0));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        // Add more padding to cells
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                if (isSelected) {
                    setBackground(table.getSelectionBackground());
                    setForeground(table.getSelectionForeground());
                } else {
                    setBackground(row % 2 == 0 ? CARD_COLOR : new Color(245, 245, 245));
                    setForeground(table.getForeground());
                }
                return this;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);

        return table;
    }

    private JPanel createRevenuePanel() {
        JPanel panel = new JPanel(new BorderLayout(SPACING, SPACING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING)
        ));

        // Title
        JLabel titleLabel = new JLabel("Revenue Overview", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Table
        List<Vender> vendors = venderController.getAllVenders();
        String[] columnNames = {"Vendor ID", "Vendor Name", "Total Revenue"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        DecimalFormat df = new DecimalFormat("#,##0.00");
        for (Vender v : vendors) {
            List<Order> vendorOrders = orderController.getOrdersByVender(v.getId());
            double totalRevenue = vendorOrders.stream()
                    .filter(o -> !o.getCurrentStatus().equals("Cancel") && !o.getCurrentStatus().equals("ForceCancel"))
                    .mapToDouble(Order::getTotalPrice)
                    .sum();
            model.addRow(new Object[]{v.getId(), v.getStoreName(), "RM " + df.format(totalRevenue)});
        }

        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDeliveryPerformancePanel() {
        JPanel panel = new JPanel(new BorderLayout(SPACING, SPACING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING)
        ));

        JLabel titleLabel = new JLabel("Delivery Runner Performance", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<DeliveryRunner> runners = deliveryRunnerController.getAllDeliveryRunner();
        String[] columnNames = {"Runner ID", "Runner Name", "Average Rating", "Review Count"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        DecimalFormat df = new DecimalFormat("#.##");
        for (DeliveryRunner runner : runners) {
            double avgRating = runner.getAverageRating();
            int reviewCount = (runner.getReviews() != null) ? runner.getReviews().size() : 0;
            model.addRow(new Object[]{
                runner.getId(),
                runner.getFirstName() + " " + runner.getLastName(),
                df.format(avgRating) + " ★",
                reviewCount
            });
        }

        JTable table = createStyledTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCustomerComplaintsPanel() {
        JPanel panel = new JPanel(new BorderLayout(SPACING, SPACING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING)
        ));

        JLabel titleLabel = new JLabel("Customer Complaints", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Review> complaints = reviewController.getAllReviews().stream()
                .filter(r -> r.getRating() <= 2)
                .collect(Collectors.toList());

        String[] columnNames = {"Review ID", "User ID", "Target User", "Rating", "Review Text", "Created At", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };

        for (Review r : complaints) {
            model.addRow(new Object[]{
                r.getId(), r.getUserId(), r.getTargetUserId(),
                r.getRating() + " ★", r.getText(), r.getCreatedAt(), "Resolve"
            });
        }

        JTable table = createStyledTable(model);
        table.getColumn("Action").setCellRenderer(new ModernButtonRenderer());
        table.getColumn("Action").setCellEditor(new ModernButtonEditor(new JCheckBox()) {
            @Override
            public Object getCellEditorValue() {
                int row = table.getSelectedRow();
                String reviewId = (String) model.getValueAt(row, 0);
                if (reviewController.deleteReview(reviewId)) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(panel, "Complaint resolved successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                }
                return "Resolve";
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createVendorItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout(SPACING, SPACING));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            new EmptyBorder(PANEL_PADDING, PANEL_PADDING, PANEL_PADDING, PANEL_PADDING)
        ));

        JLabel titleLabel = new JLabel("Vendor Items Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        List<Menu> items = menuController.getMenus();
        String[] columnNames = {"Item ID", "Vendor ID", "Item Name", "Description", "Price", "Action"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        DecimalFormat df = new DecimalFormat("#,##0.00");
        for (Menu item : items) {
            model.addRow(new Object[]{
                item.getId(),
                item.getVenderId(),
                item.getName(),
                item.getDescription(),
                "RM " + df.format(item.getPrice()),
                "Remove"
            });
        }

        JTable table = createStyledTable(model);
        table.getColumn("Action").setCellRenderer(new ModernButtonRenderer());
        table.getColumn("Action").setCellEditor(new ModernButtonEditor(new JCheckBox()) {
            @Override
            public Object getCellEditorValue() {
                int row = table.getSelectedRow();
                String itemId = (String) model.getValueAt(row, 0);
                if (menuController.deleteMenu(itemId)) {
                    model.removeRow(row);
                    JOptionPane.showMessageDialog(panel, "Menu item removed successfully",
                            "Success", JOptionPane.INFORMATION_MESSAGE);
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
 * Modern styled button renderer for table cells
 */
class ModernButtonRenderer extends JButton implements TableCellRenderer {
    public ModernButtonRenderer() {
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(new Color(220, 53, 69));
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Segoe UI", Font.BOLD, 12));
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
 * Modern styled button editor for table cells
 */
class ModernButtonEditor extends DefaultCellEditor {
    protected JButton button;
    private String label;
    private boolean isPushed;

    public ModernButtonEditor(JCheckBox checkBox) {
        super(checkBox);
        button = new JButton();
        button.setOpaque(true);
        button.setBackground(Color.WHITE);
        button.setForeground(new Color(220, 53, 69));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        button.addActionListener(e -> fireEditingStopped());
        
        // Add hover effect
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
