package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class AdminDashboard extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;

    public AdminDashboard(MainFrame frame) {
        this.mainFrame = frame;

        try {
            authController = new AuthController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading admin data");
        }

        // Set layout for the panel
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title Label
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(45, 52, 54)); // Dark grey header
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new GridLayout(7, 1, 10, 10)); // 7 rows, 1 column
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        Dimension buttonSize = new Dimension(250, 40);
        Font buttonFont = new Font("Arial", Font.BOLD, 14);

        JButton manageVendorButton = createStyledButton("Manage Vendor", buttonSize, buttonFont);
        manageVendorButton.addActionListener(e -> mainFrame.switchTo("VenderListPage"));

        JButton manageCustomerButton = createStyledButton("Manage Customer", buttonSize, buttonFont);
        manageCustomerButton.addActionListener(e -> mainFrame.switchTo("CustomerListPage"));

        JButton manageDeliveryRunnerButton = createStyledButton("Manage Delivery Runner", buttonSize, buttonFont);
        manageDeliveryRunnerButton.addActionListener(e -> mainFrame.switchTo("DeliveryRunnerListPage"));

        JButton manageTopupButton = createStyledButton("Manage Topup & Credit", buttonSize, buttonFont);
        manageTopupButton.addActionListener(e -> mainFrame.switchTo("WalletManagePage"));

        JButton generateReceiptButton = createStyledButton("Generate Receipt", buttonSize, buttonFont);
        generateReceiptButton.addActionListener(e ->
                JOptionPane.showMessageDialog(mainFrame, "Generate Receipt not implemented yet."));

        JButton sendReceiptButton = createStyledButton("Send Receipt", buttonSize, buttonFont);
        sendReceiptButton.addActionListener(e ->
                JOptionPane.showMessageDialog(mainFrame, "Send Receipt not implemented yet."));

        JButton logoutButton = createStyledButton("Logout", buttonSize, buttonFont);
        logoutButton.setOpaque(true);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setBackground(new Color(214, 48, 49)); // Red color
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(e -> {
            authController.logout();
            mainFrame.switchTo("Login");
        });

        // Add buttons to the panel
        buttonPanel.add(manageVendorButton);
        buttonPanel.add(manageCustomerButton);
        buttonPanel.add(manageDeliveryRunnerButton);
        buttonPanel.add(manageTopupButton);
        buttonPanel.add(generateReceiptButton);
        buttonPanel.add(sendReceiptButton);
        buttonPanel.add(logoutButton);

        // Add components to main panel
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    // Utility method to create styled buttons
    private JButton createStyledButton(String text, Dimension size, Font font) {
        JButton button = new JButton(text);
        button.setPreferredSize(size);
        button.setFont(font);
        button.setFocusPainted(false);
        button.setBackground(new Color(52, 152, 219)); // Blue color
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        return button;
    }
}
