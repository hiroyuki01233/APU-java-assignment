package com.java_assignment.group.View.Shared;

import com.java_assignment.group.Controller.*;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Transaction;
import com.java_assignment.group.Model.Wallet;
import com.java_assignment.group.MainFrame;
import javafx.beans.binding.DoubleExpression;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RevenueDashboard extends JPanel {
    private JLabel currentRevenueLabel;
    private JLabel comparisonLabel;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton filterButton;
    private JPanel chartPanel;

    MainFrame mainFrame;

    // Controllers and user initialization
    private WalletController walletController;
    private AuthController authController;
    private BaseUser user;

    public RevenueDashboard(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));}

    private void onLoadDashboard(){
        try {
            this.walletController = new WalletController();
            this.authController = new AuthController();
            this.user = authController.getCurrentUser();

            if (user == null) {
                System.out.println("user is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed。", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void onPageDisplayed() {
        onLoadDashboard();
        removeAll();

        // Main panel styling
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header Panel with modern styling
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Current Balance Card Panel
        JPanel balanceCardPanel = new JPanel();
        balanceCardPanel.setLayout(new BoxLayout(balanceCardPanel, BoxLayout.Y_AXIS));
        balanceCardPanel.setBackground(new Color(248, 249, 250));
        balanceCardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        // Current Balance section
        currentRevenueLabel = new JLabel("Current Balance: RM--");
        currentRevenueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        currentRevenueLabel.setForeground(new Color(66, 139, 202));
        currentRevenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        comparisonLabel = new JLabel("(-- compared to last month)");
        comparisonLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        comparisonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        balanceCardPanel.add(currentRevenueLabel);
        balanceCardPanel.add(Box.createVerticalStrut(10));
        balanceCardPanel.add(comparisonLabel);

        // Style go back button
        JButton gobackButton = new JButton("Back");
        gobackButton.setBackground(new Color(66, 139, 202));
        gobackButton.setForeground(Color.WHITE);
        gobackButton.setFocusPainted(false);
        gobackButton.setBorderPainted(false);
        gobackButton.setOpaque(true);
        gobackButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        gobackButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        gobackButton.setMaximumSize(new Dimension(100, 35));
        gobackButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                gobackButton.setBackground(new Color(51, 122, 183));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                gobackButton.setBackground(new Color(66, 139, 202));
            }
        });
        gobackButton.addActionListener((ActionEvent e) -> {
            if (user.getUserType().equals("vender")){
                mainFrame.switchTo("VenderDashboard");
            } else if (user.getUserType().equals("delivery_runner")) {
                mainFrame.switchTo("DeliveryRunnerDashboard");
            } else if (user.getUserType().equals("customer")) {
                mainFrame.switchTo("CustomerDashboard");
            }
        });

        headerPanel.add(balanceCardPanel);
        headerPanel.add(Box.createVerticalStrut(20));
        headerPanel.add(gobackButton);
        add(headerPanel, BorderLayout.NORTH);

        // Filter Panel with modern styling
        JPanel filterContainerPanel = new JPanel();
        filterContainerPanel.setLayout(new BoxLayout(filterContainerPanel, BoxLayout.Y_AXIS));
        filterContainerPanel.setBackground(Color.WHITE);
        filterContainerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        filterPanel.setBackground(new Color(248, 249, 250));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(222, 226, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        // Style date input fields and labels
        JLabel startLabel = new JLabel("Start Date (YYYY-MM-DD):");
        startLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        startDateField = new JTextField(10);
        startDateField.setText(oneMonthAgo.format(formatter));
        startDateField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel endLabel = new JLabel("End Date (YYYY-MM-DD):");
        endLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        endDateField = new JTextField(10);
        endDateField.setText(today.format(formatter));
        endDateField.setFont(new Font("Arial", Font.PLAIN, 14));

        // Style filter button
        filterButton = new JButton("Filter");
        filterButton.setBackground(new Color(66, 139, 202));
        filterButton.setForeground(Color.WHITE);
        filterButton.setFocusPainted(false);
        filterButton.setBorderPainted(false);
        filterButton.setOpaque(true);
        filterButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        filterButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                filterButton.setBackground(new Color(51, 122, 183));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                filterButton.setBackground(new Color(66, 139, 202));
            }
        });
        filterButton.addActionListener(e -> updateChart());

        filterPanel.add(startLabel);
        filterPanel.add(startDateField);
        filterPanel.add(endLabel);
        filterPanel.add(endDateField);
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.CENTER);

        // Chart Panel with modern styling
        chartPanel = new JPanel();
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(chartPanel, BorderLayout.SOUTH);

        updateHeader();
        updateChart();
    }

    /**
     * ヘッダー情報を更新する。
     * ・Walletの現在の残高
     * ・当月収益と先月収益から算出した変化率
     */
    private void updateHeader() {
        if (walletController == null) return;

        Wallet wallet = walletController.getWalletByBaseUserId(user.getId());
        double currentBalance = (wallet != null) ? wallet.getBalance() : 0.0;
        currentRevenueLabel.setText("Current Balance: RM" + currentBalance);

        LocalDate today = LocalDate.now();
        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();
        double currentMonthRevenue = walletController.calculateMonthlyRevenue(user.getId(), currentYear, currentMonth);

        LocalDate previousMonthDate = today.minusMonths(1);
        double previousMonthRevenue = walletController.calculateMonthlyRevenue(user.getId(), previousMonthDate.getYear(), previousMonthDate.getMonthValue());

        double percentageChange = 0.0;
        if (previousMonthRevenue != 0) {
            percentageChange = ((currentMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100;
        }
        comparisonLabel.setText(String.format("(%.2f%% compared to last month)", percentageChange));
        comparisonLabel.setForeground(percentageChange >= 0 ? Color.GREEN : Color.RED);
    }

    /**
     * 指定された日付範囲の取引情報を取得し、チャートを更新する。
     * 利用する項目：datetime, move amount, description
     */
    private void updateChart() {
        String startDateStr = startDateField.getText();
        String endDateStr = endDateField.getText();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 日時のフォーマット（チャート表示用）
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            LocalDate startDate = LocalDate.parse(startDateStr, dateFormatter);
            LocalDate endDate = LocalDate.parse(endDateStr, dateFormatter);
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 日付をLocalDateTimeに変換：開始は0:00、終了は23:59:59に設定
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

            // walletControllerのメソッドを使用して取引データを取得
            List<Transaction> transactions = walletController.getTransactionsByDestinationUserAndDateRange(user.getId(), startDateTime, endDateTime);
            System.out.println(transactions);
            System.out.println("transcations");
            renderChart(transactions, dateTimeFormatter);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * チャート領域に、取得した取引（datetime, move amount, description）を表示する。
     */
    private void renderChart(List<Transaction> transactions, DateTimeFormatter dateTimeFormatter) {
        chartPanel.removeAll();
        chartPanel.setPreferredSize(new Dimension(800, 400));

        String[] columnNames = {"Date", "Amount", "Description"};

        Object[][] data = new Object[transactions.size()][3];
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            Double amount = transaction.getAmount();
            if(transaction.getSourceUser() != null && transaction.getSourceUser().getId().equals(user.getId())){
                amount = transaction.getAmount() * -1;
            }

            data[i][0] = transaction.getCreatedAt().format(dateTimeFormatter);
            data[i][1] = amount;
            data[i][2] = transaction.getDescription();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setRowHeight(35);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(66, 139, 202));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionBackground(Color.LIGHT_GRAY);
        table.setSelectionForeground(Color.BLACK);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        // Add alternating row colors
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                return c;
            }
        });

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Date
        columnModel.getColumn(1).setPreferredWidth(100); // Amount
        columnModel.getColumn(2).setPreferredWidth(200); // Description

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(420, 400));
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        chartPanel.add(scrollPane, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
