package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

public class AdminRegistrationPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JLabel messageLabel;

    public AdminRegistrationPage(MainFrame frame) {
        this.mainFrame = frame;
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading admin data");
        }

        // Set layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Title Label
        JLabel titleLabel = new JLabel("Admin Registration", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(45, 52, 54));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;

        // Input Fields
        JTextField emailField = createTextField();
        JTextField firstNameField = createTextField();
        JTextField lastNameField = createTextField();
        JPasswordField passwordField = createPasswordField();
        JPasswordField confirmPasswordField = createPasswordField();

        formPanel.add(new JLabel("Email Address:"), gbc);
        gbc.gridy++;
        formPanel.add(emailField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridy++;
        formPanel.add(firstNameField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridy++;
        formPanel.add(lastNameField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);
        gbc.gridy++;
        formPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridy++;
        formPanel.add(confirmPasswordField, gbc);
        gbc.gridy++;

        // Create a separate panel for the button with light gray background
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(true);
        buttonPanel.setBackground(new Color(245, 245, 245));

        JButton registerButton = createStyledButton("Register");
        registerButton.setPreferredSize(new Dimension(200, 40));
        registerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match");
                return;
            }

            if (authController.registerAdmin(email, password, firstName, lastName)) {
                messageLabel.setText("Admin registered successfully!");
                mainFrame.switchTo("AdminLoginPage");
            } else {
                messageLabel.setText("Failed to register admin");
            }
        });

        buttonPanel.add(registerButton);

        // Add button panel to form with spacing
        gbc.insets = new Insets(20, 0, 10, 0);
        formPanel.add(buttonPanel, gbc);

        // Login Link
        JLabel loginLabel = new JLabel("Already have an admin account? Login");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.switchTo("AdminLoginPage");
            }
        });

        // Add components to the main panel
        add(titleLabel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        // Footer panel for login link and message
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.add(loginLabel);
        footerPanel.add(messageLabel);
        add(footerPanel, BorderLayout.SOUTH);
    }

    // Utility method to create text fields
    private JTextField createTextField() {
        JTextField textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(250, 30));
        return textField;
    }

    // Utility method to create password fields
    private JPasswordField createPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setPreferredSize(new Dimension(250, 30));
        return passwordField;
    }

    // Utility method to create styled buttons with fixed visibility
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 152, 219)); // Blue background
        button.setForeground(Color.WHITE);  // White text
        button.setOpaque(true);  // Make sure the background is painted
        button.setContentAreaFilled(true);  // Ensure the button's background is filled
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}