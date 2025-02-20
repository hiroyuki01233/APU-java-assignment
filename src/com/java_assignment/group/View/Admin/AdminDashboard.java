package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;

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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("Admin Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(30));

        Dimension buttonSize = new Dimension(250, 30);

        JButton manageVenderButton = new JButton("Manage Vender");
        manageVenderButton.setAlignmentX(CENTER_ALIGNMENT);
        manageVenderButton.setMaximumSize(buttonSize);
        manageVenderButton.addActionListener(e -> mainFrame.switchTo("VenderListPage"));
        add(manageVenderButton);
        add(Box.createVerticalStrut(10));

        JButton manageCustomerButton = new JButton("Manage Customer");
        manageCustomerButton.setAlignmentX(CENTER_ALIGNMENT);
        manageCustomerButton.setMaximumSize(buttonSize);
        manageCustomerButton.addActionListener(e -> mainFrame.switchTo("CustomerListPage"));
        add(manageCustomerButton);
        add(Box.createVerticalStrut(10));

        JButton manageDeliveryRunnerButton = new JButton("Manage Delivery Runner");
        manageDeliveryRunnerButton.setAlignmentX(CENTER_ALIGNMENT);
        manageDeliveryRunnerButton.setMaximumSize(buttonSize);
        manageDeliveryRunnerButton.addActionListener(e -> mainFrame.switchTo("DeliveryRunnerListPage"));
        add(manageDeliveryRunnerButton);
        add(Box.createVerticalStrut(10));

        JButton manageTopupButton = new JButton("Manage Topup and Credit");
        manageTopupButton.setAlignmentX(CENTER_ALIGNMENT);
        manageTopupButton.setMaximumSize(buttonSize);
        manageTopupButton.addActionListener(e -> mainFrame.switchTo("WalletManagePage"));
        add(manageTopupButton);
        add(Box.createVerticalStrut(10));

        JButton generateReceiptButton = new JButton("Generate Receipt");
        generateReceiptButton.setAlignmentX(CENTER_ALIGNMENT);
        generateReceiptButton.setMaximumSize(buttonSize);
        generateReceiptButton.addActionListener(e ->
                JOptionPane.showMessageDialog(mainFrame, "Generate Receipt not implemented yet."));
        add(generateReceiptButton);
        add(Box.createVerticalStrut(10));

        JButton sendReceiptButton = new JButton("Send Receipt");
        sendReceiptButton.setAlignmentX(CENTER_ALIGNMENT);
        sendReceiptButton.setMaximumSize(buttonSize);
        sendReceiptButton.addActionListener(e ->
                JOptionPane.showMessageDialog(mainFrame, "Send Receipt not implemented yet."));
        add(sendReceiptButton);
        add(Box.createVerticalGlue());

        // Send Receipt (dummy)
        JButton logoutButton = new JButton("Logout");
        logoutButton.setAlignmentX(CENTER_ALIGNMENT);
        logoutButton.addActionListener(e -> {
                authController.logout();
                mainFrame.switchTo("Login");
        });
        add(logoutButton);
        add(Box.createVerticalGlue());
    }
}
