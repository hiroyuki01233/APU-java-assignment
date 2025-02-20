package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * This page is used to register an admin account.
 */
public class AdminRegistrationPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JLabel messageLabel;

    public AdminRegistrationPage(MainFrame frame) {
        this.mainFrame = frame;
        messageLabel = new JLabel("");

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading admin data");
        }

        // Set layout and padding
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        // Title
        JLabel titleLabel = new JLabel("Admin Registration");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Email field
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // First name field
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Last name field
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        lastNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Confirm password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Register button
        JButton registerButton = new JButton("Register");
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
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);

        // Link to admin login page
        JLabel loginLabel = new JLabel("Already have an admin account? Login");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.switchTo("AdminLoginPage");
            }
        });

        // Add components vertically with spacing
        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(20));
        add(emailLabel);
        add(emailField);
        add(Box.createVerticalStrut(10));
        add(firstNameLabel);
        add(firstNameField);
        add(Box.createVerticalStrut(10));
        add(lastNameLabel);
        add(lastNameField);
        add(Box.createVerticalStrut(10));
        add(passwordLabel);
        add(passwordField);
        add(Box.createVerticalStrut(10));
        add(confirmPasswordLabel);
        add(confirmPasswordField);
        add(Box.createVerticalStrut(20));
        add(buttonPanel);
        add(Box.createVerticalStrut(10));
        add(messageLabel);
        add(Box.createVerticalStrut(20));
        add(loginLabel);
        add(Box.createVerticalGlue());
    }
}
