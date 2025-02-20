package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class DeliveryRunnerRegisterPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;

    public DeliveryRunnerRegisterPage(MainFrame frame) {
        try {
            authController = new AuthController();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing auth controller.");
        }

        this.mainFrame = frame;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("Delivery Runner Register Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        add(new JLabel("First Name:"));
        JTextField firstNameField = new JTextField(20);
        firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(firstNameField);
        add(Box.createVerticalStrut(10));

        add(new JLabel("Last Name:"));
        JTextField lastNameField = new JTextField(20);
        lastNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(lastNameField);
        add(Box.createVerticalStrut(10));

        add(new JLabel("Email:"));
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(emailField);
        add(Box.createVerticalStrut(10));

        add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(passwordField);
        add(Box.createVerticalStrut(20));

        add(new JLabel("Confirm Password:"));
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        add(confirmPasswordField);
        add(Box.createVerticalStrut(20));

        JButton registerButton = new JButton("Register Delivery Runner");
        registerButton.setAlignmentX(CENTER_ALIGNMENT);
        registerButton.addActionListener(e -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(mainFrame, "Passwords do not match.");
                return;
            }

            if (authController.registerDeliveryRunner(email, password, firstName, lastName)) {
                JOptionPane.showMessageDialog(mainFrame, "Delivery Runner registered successfully.");
                mainFrame.switchTo("DeliveryRunnerListPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Failed to register Delivery Runner.");
            }
        });
        add(registerButton);
        add(Box.createVerticalStrut(20));

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));
        add(backButton);
        add(Box.createVerticalGlue());
    }
}
