package com.java_assignment.group.View.Shared;

import com.java_assignment.group.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class RevenueDashboard extends JPanel {
    private JLabel currentRevenueLabel;
    private JLabel comparisonLabel;
    private JTextField startDateField;
    private JTextField endDateField;
    private JButton filterButton;
    private JPanel chartPanel;
    private double currentRevenue = 222.0; // Example revenue
    private double previousMonthRevenue = 200.0; // Example previous revenue

    public RevenueDashboard(MainFrame mainFrame) {
        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // Current revenue display
        currentRevenueLabel = new JLabel("Current Balance: RM" + currentRevenue);
        currentRevenueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        currentRevenueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Comparison with previous month
        double percentageChange = ((currentRevenue - previousMonthRevenue) / previousMonthRevenue) * 100;
        comparisonLabel = new JLabel(String.format("(%.2f%% compared to last month)", percentageChange));
        comparisonLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        if (percentageChange >= 0) {
            comparisonLabel.setForeground(Color.GREEN);
        } else {
            comparisonLabel.setForeground(Color.RED);
        }

        comparisonLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(currentRevenueLabel);
        headerPanel.add(comparisonLabel);

        add(headerPanel, BorderLayout.NORTH);

        // Date range filter panel
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout());

        filterPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        startDateField = new JTextField(10);
        filterPanel.add(startDateField);

        filterPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        endDateField = new JTextField(10);
        filterPanel.add(endDateField);

        filterButton = new JButton("Filter");
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateChart();
            }
        });
        filterPanel.add(filterButton);

        add(filterPanel, BorderLayout.CENTER);

        // Chart Panel
        chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(600, 400));
        add(chartPanel, BorderLayout.SOUTH);
    }

    private void updateChart() {
        String startDateStr = startDateField.getText();
        String endDateStr = endDateField.getText();

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateStr, formatter);
            LocalDate endDate = LocalDate.parse(endDateStr, formatter);

            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "End date must be after start date.", "Invalid Date Range", JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Map.Entry<LocalDate, Double>> revenueData = fetchRevenueData(startDate, endDate);
            renderChart(revenueData);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Map.Entry<LocalDate, Double>> fetchRevenueData(LocalDate start, LocalDate end) {
        // Placeholder function for fetching revenue data between start and end dates
        // This should be replaced with actual data fetching logic
        return List.of(
                Map.entry(start, 50.0),
                Map.entry(start.plusDays(1), 80.0),
                Map.entry(start.plusDays(2), 100.0)
        );
    }

    private void renderChart(List<Map.Entry<LocalDate, Double>> revenueData) {
        chartPanel.removeAll();
        chartPanel.setLayout(new GridLayout(1, 1));

        // Chart visualization (Placeholder: Use JFreeChart or another library for real graphs)
        JTextArea chartPlaceholder = new JTextArea();
        StringBuilder chartText = new StringBuilder("Date\tRevenue\n");
        for (Map.Entry<LocalDate, Double> entry : revenueData) {
            chartText.append(entry.getKey()).append("\tRM").append(entry.getValue()).append("\n");
        }
        chartPlaceholder.setText(chartText.toString());
        chartPlaceholder.setEditable(false);

        chartPanel.add(new JScrollPane(chartPlaceholder));
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}