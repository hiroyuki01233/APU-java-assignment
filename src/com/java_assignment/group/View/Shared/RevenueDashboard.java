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

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JButton gobackButton = new JButton("go back");
        gobackButton.addActionListener((ActionEvent e) -> {
            if (user.getUserType().equals("vender")){
                mainFrame.switchTo("VenderDashboard");
            } else if (user.getUserType().equals("delivery_runner")) {
                mainFrame.switchTo("DeliveryRunnerDashboard");
            } else if (user.getUserType().equals("customer")) {
                mainFrame.switchTo("CustomerDashboard");
            }
        });

        currentRevenueLabel = new JLabel("Current Balance: RM--");
        currentRevenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentRevenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        comparisonLabel = new JLabel("(-- compared to last month)");
        comparisonLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        comparisonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(gobackButton);
        headerPanel.add(currentRevenueLabel);
        headerPanel.add(comparisonLabel);
        add(headerPanel, BorderLayout.NORTH);

        // フィルターパネル：直近1か月をデフォルトに
        JPanel filterPanel = new JPanel(new FlowLayout());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        filterPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField(10);
        startDateField.setText(oneMonthAgo.format(formatter));
        filterPanel.add(startDateField);

        filterPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField(10);
        endDateField.setText(today.format(formatter));
        filterPanel.add(endDateField);

        filterButton = new JButton("Filter");
        filterButton.addActionListener(e -> updateChart());
        filterPanel.add(filterButton);
        add(filterPanel, BorderLayout.CENTER);

        // チャートパネル
        chartPanel = new JPanel();
        add(chartPanel, BorderLayout.SOUTH);

        updateHeader();  // 更新：現在残高と月間収益比較
        updateChart();   // チャートの初期表示（直近1か月）
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
        chartPanel.setPreferredSize(new Dimension(330, 600)); // 必要に応じて変更

        String[] columnNames = {"Date", "Amount", "Description"};

        Object[][] data = new Object[transactions.size()][3];
        for (int i = 0; i < transactions.size(); i++) {
            Transaction transaction = transactions.get(i);
            Double amount = transaction.getAmount();
            if(transaction.getSourceUser().getId().equals(user.getId())){
                amount = transaction.getAmount() * -1;
            }

            data[i][0] = transaction.getCreatedAt().format(dateTimeFormatter);
            data[i][1] = amount;
            data[i][2] = transaction.getDescription();
        }

        JTable table = new JTable(data, columnNames);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 自動リサイズをオフ

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Date
        columnModel.getColumn(1).setPreferredWidth(80);  // Amount
        columnModel.getColumn(2).setPreferredWidth(150); // Description

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(330, 600)); // 必要に応じて調整

        chartPanel.add(scrollPane, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}
