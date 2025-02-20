package com.java_assignment.group.View;

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
 * This page allows an admin to log in.
 */
public class AdminLoginPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JLabel messageLabel;

    public AdminLoginPage(MainFrame frame) {
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
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Email field
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Login button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (authController.login(email, password)) {
                if ("admin".equals(authController.getCurrentUser().getUserType())) {
                    messageLabel.setText("Admin logged in successfully!");
                    // Switch to Admin Dashboard (if implemented)
                    mainFrame.switchTo("AdminDashboard");
                } else {
                    messageLabel.setText("Not an admin account");
                }
            } else {
                messageLabel.setText("Login failed");
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);

        // Link to admin registration page
        JLabel registerLabel = new JLabel("Don't have an admin account? Register");
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.switchTo("AdminRegistrationPage");
            }
        });

        // Arrange components vertically with spacing
        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(20));
        add(emailLabel);
        add(emailField);
        add(Box.createVerticalStrut(10));
        add(passwordLabel);
        add(passwordField);
        add(Box.createVerticalStrut(20));
        add(buttonPanel);
        add(Box.createVerticalStrut(10));
        add(messageLabel);
        add(Box.createVerticalStrut(20));
        add(registerLabel);
        add(Box.createVerticalGlue());
    }
}
