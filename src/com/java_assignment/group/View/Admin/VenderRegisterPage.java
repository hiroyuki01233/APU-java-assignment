package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class VenderRegisterPage extends JPanel {
    private MainFrame mainFrame;
    private VenderController venderController;
    private AuthController authController;

    public VenderRegisterPage(MainFrame frame) {
        this.mainFrame = frame;

        try {
            authController = new AuthController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing auth controller.");
        }

        try {
            venderController = new VenderController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing vender controller.");
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.CYAN);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Register New Vender", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Create a new vender account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(Color.CYAN);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;

        // Email Field
        JTextField emailField = createStyledTextField();
        addFormRow(formPanel, "Email:", emailField, gbc);

        // Password Field
        JPasswordField passwordField = createStyledPasswordField();
        addFormRow(formPanel, "Password:", passwordField, gbc);

        // Store Name Field
        JTextField storeNameField = createStyledTextField();
        addFormRow(formPanel, "Store Name:", storeNameField, gbc);

        // Store Background Image Field
        JTextField storeBackgroundImageField = createStyledTextField();
        addFormRow(formPanel, "Store Background Image URL (Optional):", storeBackgroundImageField, gbc);

        // Store Icon Image Field
        JTextField storeIconImageField = createStyledTextField();
        addFormRow(formPanel, "Store Icon Image URL (Optional):", storeIconImageField, gbc);

        // Store Description Field
        JTextArea storeDescriptionArea = new JTextArea(5, 20);
        storeDescriptionArea.setLineWrap(true);
        storeDescriptionArea.setWrapStyleWord(true);
        storeDescriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JScrollPane scrollPane = new JScrollPane(storeDescriptionArea);
        addFormRow(formPanel, "Store Description:", scrollPane, gbc);

        JScrollPane mainScrollPane = new JScrollPane(formPanel);
        mainScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainScrollPane.getViewport().setBackground(Color.WHITE);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton registerButton = createStyledButton("Register", Color.GREEN);
        JButton backButton = createStyledButton("Back", Color.CYAN);

        registerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String storeName = storeNameField.getText().trim();
            String storeBackgroundImage = storeBackgroundImageField.getText().trim();
            String storeIconImage = storeIconImageField.getText().trim();
            String storeDescription = storeDescriptionArea.getText().trim();

            if (email.isEmpty() || password.isEmpty() || storeName.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Email, Password, and Store Name are required.");
                return;
            }

            if (authController.registerVender(email, password, storeName, storeBackgroundImage, storeIconImage, storeDescription)) {
                JOptionPane.showMessageDialog(mainFrame, "Vender registered successfully.");
                mainFrame.switchTo("VenderListPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to register vender.");
            }
        });

        backButton.addActionListener(e -> mainFrame.switchTo("VenderListPage"));

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        add(headerPanel, BorderLayout.NORTH);
        add(mainScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(250, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
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

    private void addFormRow(JPanel panel, String label, JComponent field, GridBagConstraints gbc) {
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(jLabel, gbc);
        panel.add(field, gbc);
    }
}
