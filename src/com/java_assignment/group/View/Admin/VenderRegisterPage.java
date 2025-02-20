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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Register New Vender");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        // Email Field
        JTextField emailField = new JTextField(20);
        add(new JLabel("Email:"));
        add(emailField);

        // Password Field
        JPasswordField passwordField = new JPasswordField(20);
        add(new JLabel("Password:"));
        add(passwordField);

        // Store Name Field
        JTextField storeNameField = new JTextField(20);
        add(new JLabel("Store Name:"));
        add(storeNameField);

        // Store Background Image Field
        JTextField storeBackgroundImageField = new JTextField(20);
        add(new JLabel("Store Background Image URL (Optional):"));
        add(storeBackgroundImageField);

        // Store Icon Image Field
        JTextField storeIconImageField = new JTextField(20);
        add(new JLabel("Store Icon Image URL (Optional):"));
        add(storeIconImageField);

        // Store Description Field
        JTextArea storeDescriptionArea = new JTextArea(5, 20);
        storeDescriptionArea.setLineWrap(true);
        storeDescriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(storeDescriptionArea);
        add(new JLabel("Store Description:"));
        add(scrollPane);

        // Register Button
        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String storeName = storeNameField.getText().trim();
            String storeBackgroundImage = storeBackgroundImageField.getText().trim();
            String storeIconImage = storeIconImageField.getText().trim();
            String storeDescription = storeDescriptionArea.getText().trim();

            // Basic validation
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
        add(registerButton);
        add(Box.createVerticalStrut(20));

        // Back Button
        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.switchTo("VenderListPage"));
        add(backButton);
    }
}
