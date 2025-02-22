package com.java_assignment.group.View.Shared;

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
        messageLabel.setForeground(Color.RED);

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading admin data");
        }

        // Set layout and padding
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));
        setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.DARK_GRAY);

        // Email field
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setAlignmentX(CENTER_ALIGNMENT);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JTextField emailField = createStyledTextField();

        // Password field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setAlignmentX(CENTER_ALIGNMENT);
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        JPasswordField passwordField = createStyledPasswordField();

        // Login button
        JButton loginButton = new JButton("Login");
        styleButton(loginButton);
        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (authController.login(email, password)) {
                if ("admin".equals(authController.getCurrentUser().getUserType())) {
                    messageLabel.setText("Admin logged in successfully!");
                    messageLabel.setForeground(new Color(46, 125, 50));
                    mainFrame.switchTo("AdminDashboard");
                } else {
                    messageLabel.setText("Not an admin account");
                }
            } else {
                messageLabel.setText("Login failed");
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);

        // Link to admin registration page
        JLabel registerLabel = new JLabel("Don't have an admin account? Register");
        registerLabel.setAlignmentX(CENTER_ALIGNMENT);
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.switchTo("AdminRegistrationPage");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(Color.BLUE);
            }
        });

        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Arrange components vertically with spacing
        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(30));
        add(emailLabel);
        add(Box.createVerticalStrut(5));
        add(emailField);
        add(Box.createVerticalStrut(20));
        add(passwordLabel);
        add(Box.createVerticalStrut(5));
        add(passwordField);
        add(Box.createVerticalStrut(25));
        add(buttonPanel);
        add(Box.createVerticalStrut(15));
        add(messageLabel);
        add(Box.createVerticalStrut(25));
        add(registerLabel);
        add(Box.createVerticalGlue());
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setMaximumSize(new Dimension(300, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setMaximumSize(new Dimension(300, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setAlignmentX(CENTER_ALIGNMENT);
        return field;
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(200, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(Color.BLUE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Color.BLUE);
            }
        });
    }
}
