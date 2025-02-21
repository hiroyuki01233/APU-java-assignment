package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;

public class CustomerRegisterPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JLabel messageLabel;

    public CustomerRegisterPage(MainFrame frame) {
        this.mainFrame = frame;

        try {
            authController = new AuthController();
        } catch (IOException e) {
            showErrorMessage("Error loading users");
        }

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 250));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        createHeaderPanel();
        createFormPanel();
    }

    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Register New Customer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Create a new customer account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(236, 240, 241));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;

        JTextField firstNameField = createStyledTextField();
        JTextField lastNameField = createStyledTextField();
        JTextField emailField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JPasswordField confirmPasswordField = createStyledPasswordField();

        addFormRow(formPanel, "First Name", firstNameField, gbc);
        addFormRow(formPanel, "Last Name", lastNameField, gbc);
        addFormRow(formPanel, "Email Address", emailField, gbc);
        addFormRow(formPanel, "Password", passwordField, gbc);
        addFormRow(formPanel, "Confirm Password", confirmPasswordField, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        scrollPane.getViewport().setBackground(new Color(248, 249, 250));

        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(new Color(248, 249, 250));
        wrapperPanel.add(scrollPane, BorderLayout.CENTER);

        add(wrapperPanel, BorderLayout.CENTER);

        createButtonPanel(firstNameField, lastNameField, emailField, passwordField, confirmPasswordField);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 195, 199)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void createButtonPanel(JTextField firstNameField, JTextField lastNameField, JTextField emailField, JPasswordField passwordField, JPasswordField confirmPasswordField) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(new Color(248, 249, 250));

        Color buttonColor = new Color(100, 181, 246);  // Light blue color
        JButton registerButton = createStyledButton("Register Customer", buttonColor);
        JButton backButton = createStyledButton("Back to Dashboard", buttonColor);

        registerButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                showErrorMessage("Passwords do not match.");
                return;
            }

            if (authController.registerUser(email, password, firstName, lastName, "")) {
                showSuccessMessage("Customer registered successfully.");
                mainFrame.switchTo("AdminDashboard");
            } else {
                showErrorMessage("Failed to register customer.");
            }
        });

        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        JPanel buttonWrapperPanel = new JPanel(new BorderLayout());
        buttonWrapperPanel.setBackground(new Color(248, 249, 250));
        buttonWrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        buttonWrapperPanel.add(buttonPanel, BorderLayout.CENTER);

        add(buttonWrapperPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(backgroundColor.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void showErrorMessage(String message) {
        messageLabel.setForeground(Color.RED);
        messageLabel.setText(message);
    }

    private void showSuccessMessage(String message) {
        messageLabel.setForeground(Color.GREEN);
        messageLabel.setText(message);
    }

    private void addFormRow(JPanel panel, String label, JComponent field, GridBagConstraints gbc) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(jLabel, gbc);
        panel.add(field, gbc);
    }
}
