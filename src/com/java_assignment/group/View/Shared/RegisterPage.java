package com.java_assignment.group.View.Shared;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RegisterPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JLabel messageLabel;
    private JLabel firstNameLabel;
    private JLabel lastNameLabel;
    private JLabel emailLabel;
    private JLabel addressLabel;
    private JLabel passwordLabel;
    private JLabel confirmPasswordLabel;

    private JComponent createStyledField(JComponent field) {
        field.setPreferredSize(new Dimension(300, 35));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private void addFormRow(JPanel panel, String labelText, JComponent field, GridBagConstraints gbc) {
        JLabel label = new JLabel(labelText, JLabel.RIGHT);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }

    public RegisterPage(MainFrame frame) {
        this.mainFrame = frame;
        messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        setBackground(Color.WHITE);

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading users");
        }

        // Initialize labels
        firstNameLabel = new JLabel("First Name:");
        lastNameLabel = new JLabel("Last Name:");
        emailLabel = new JLabel("Email Address:");
        addressLabel = new JLabel("Address:");
        passwordLabel = new JLabel("Password:");
        confirmPasswordLabel = new JLabel("Confirm Password:");

        // Set the layout to a vertical BoxLayout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Set padding for the panel
        setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Create Your Account");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));

        JLabel subtitleLabel = new JLabel("Join us to order your favorite food!");
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(102, 102, 102));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setAlignmentX(CENTER_ALIGNMENT);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        // Create form fields with consistent styling
        JTextField firstNameField = (JTextField) createStyledField(new JTextField());
        JTextField lastNameField = (JTextField) createStyledField(new JTextField());
        JTextField emailField = (JTextField) createStyledField(new JTextField());
        JPasswordField passwordField = (JPasswordField) createStyledField(new JPasswordField());
        JPasswordField confirmPasswordField = (JPasswordField) createStyledField(new JPasswordField());
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 60));

        // Add form components with right-aligned labels
        addFormRow(formPanel, "First Name:", firstNameField, gbc);
        addFormRow(formPanel, "Last Name:", lastNameField, gbc);
        addFormRow(formPanel, "Email Address:", emailField, gbc);
        addFormRow(formPanel, "Password:", passwordField, gbc);
        addFormRow(formPanel, "Confirm Password:", confirmPasswordField, gbc);
        addFormRow(formPanel, "Address:", addressScroll, gbc);

        // Register button styling
        JButton registerButton = new JButton("Create Account");
        registerButton.setPreferredSize(new Dimension(300, 40));
        registerButton.setFont(new Font("Arial", Font.BOLD, 14));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBackground(new Color(0, 150, 136));
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setOpaque(true);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        registerButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                registerButton.setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerButton.setBackground(new Color(0, 150, 136));
            }
        });

        registerButton.addActionListener(e -> {
            if(authController.registerUser(
                    emailField.getText(),
                    new String(passwordField.getPassword()),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    addressArea.getText()
            )){
                mainFrame.switchTo("Login");
            }else{
                messageLabel.setText("Failed to create an account");
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(registerButton);

        // Login link styling
        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setForeground(new Color(0, 150, 136));
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        loginLabel.setAlignmentX(CENTER_ALIGNMENT);
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.switchTo("Login");
            }
        });

        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // Add all components to content panel
        contentPanel.add(titlePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(formPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        contentPanel.add(buttonPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(loginLabel);

        // Add content panel to main panel
        removeAll();
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);
    }
}
