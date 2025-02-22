package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class ManagerRegisterPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color PRIMARY_COLOR = new Color(100, 181, 246);  // Lighter blue
    private static final Color SUCCESS_COLOR = new Color(129, 199, 132);  // Lighter green
    private static final Color FORM_GROUP_BG = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

    public ManagerRegisterPage(MainFrame frame) {
        try {
            authController = new AuthController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing auth controller.");
        }

        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create main container panel
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        containerPanel.setBackground(BACKGROUND_COLOR);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(20, 20, 20, 20),
                BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(31, 97, 141))
        ));

        JLabel titleLabel = new JLabel("Register New Manager Account");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Create a new manager account");
        subtitleLabel.setFont(LABEL_FONT);
        subtitleLabel.setForeground(new Color(236, 240, 241));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);

        // Form Panel with white background and shadow effect
        JPanel formWrapper = new JPanel();
        formWrapper.setLayout(new BorderLayout());
        formWrapper.setBackground(FORM_GROUP_BG);
        formWrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 218, 218), 1),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(FORM_GROUP_BG);
        formPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Form Fields
        JTextField emailField = createStyledTextField();
        JPasswordField passwordField = createStyledPasswordField();
        JPasswordField confirmPasswordField = createStyledPasswordField();

        addFormField("Email:", emailField, formPanel);
        addFormField("Password:", passwordField, formPanel);
        addFormField("Confirm Password:", confirmPasswordField, formPanel);

        formWrapper.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(FORM_GROUP_BG);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton registerButton = createStyledButton("Register Manager", SUCCESS_COLOR);
        JButton backButton = createStyledButton("Back", PRIMARY_COLOR);

        registerButton.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.");
                return;
            }

            if (authController.registerManager(email, password)) {
                JOptionPane.showMessageDialog(mainFrame, "Manager registered successfully.");
                mainFrame.switchTo("ManagerListPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to register Manager.");
            }
        });

        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        formWrapper.add(buttonPanel, BorderLayout.SOUTH);

        // Add all panels to container
        containerPanel.add(headerPanel);
        containerPanel.add(Box.createVerticalStrut(20));
        containerPanel.add(formWrapper);

        add(containerPanel, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setMaximumSize(new Dimension(400, 35));
        field.setPreferredSize(new Dimension(400, 35));
        field.setMinimumSize(new Dimension(400, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(LABEL_FONT);
        field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setMaximumSize(new Dimension(400, 35));
        field.setPreferredSize(new Dimension(400, 35));
        field.setMinimumSize(new Dimension(400, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(LABEL_FONT);
        field.setBackground(Color.WHITE);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);
        return field;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 40));
        button.setMaximumSize(new Dimension(200, 40));
        button.setMinimumSize(new Dimension(200, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(
                        (int)(backgroundColor.getRed() * 0.8),
                        (int)(backgroundColor.getGreen() * 0.8),
                        (int)(backgroundColor.getBlue() * 0.8)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    private void addFormField(String labelText, JComponent field, JPanel container) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(FORM_GROUP_BG);
        fieldPanel.setAlignmentX(CENTER_ALIGNMENT);
        fieldPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(new Color(73, 80, 87));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        field.setAlignmentX(Component.CENTER_ALIGNMENT);

        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(5));
        fieldPanel.add(field);

        container.add(fieldPanel);
    }
}
