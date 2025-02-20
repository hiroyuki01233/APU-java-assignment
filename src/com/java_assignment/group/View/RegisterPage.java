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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class RegisterPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JLabel messageLabel;


    public RegisterPage(MainFrame frame) {
        this.mainFrame = frame;
        messageLabel = new JLabel("");

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading users");
        }

        // Set the layout to a vertical BoxLayout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        // Set padding for the panel
        setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        // Title label for the registration page
        JLabel titleLabel = new JLabel("Registration Page");
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // First Name label and text field
        JLabel firstNameLabel = new JLabel("First Name:");
        JTextField firstNameField = new JTextField(20);
        firstNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Last Name label and text field
        JLabel lastNameLabel = new JLabel("Last Name:");
        JTextField lastNameField = new JTextField(20);
        lastNameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Email Address label and text field
        JLabel emailLabel = new JLabel("Email Address:");
        JTextField emailField = new JTextField(20);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Address label and text area (with scroll pane)
        JLabel addressLabel = new JLabel("Address:");
        JTextArea addressArea = new JTextArea(3, 20);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        // Password label and password field
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Confirm Password label and password field
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        // Register button with action listener (add validation and registration logic as needed)
        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            // TODO: Validate inputs and perform registration operations here.
            // For demonstration, switch to "CustomerDashboard" upon success.

            if(authController.registerUser(
                    emailField.getText(),
                    passwordField.getText(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    addressArea.getText()
            )){
                mainFrame.switchTo("Login");
            }else{
                messageLabel.setText("Filed to create an account");
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(registerButton);

        // Label for navigation to the login page if the user already has an account
        JLabel loginLabel = new JLabel("Already have an account? Login");
        loginLabel.setForeground(Color.BLUE);
        loginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.switchTo("Login");
            }
        });

        // Arrange components vertically with spacing
        add(Box.createVerticalGlue());
        add(titleLabel);
        add(Box.createVerticalStrut(30));
        add(firstNameLabel);
        add(firstNameField);
        add(Box.createVerticalStrut(10));
        add(lastNameLabel);
        add(lastNameField);
        add(Box.createVerticalStrut(10));
        add(emailLabel);
        add(emailField);
        add(Box.createVerticalStrut(10));
        add(addressLabel);
        add(addressScroll);
        add(Box.createVerticalStrut(10));
        add(passwordLabel);
        add(passwordField);
        add(Box.createVerticalStrut(10));
        add(confirmPasswordLabel);
        add(confirmPasswordField);
        add(Box.createVerticalStrut(20));
        add(buttonPanel);
        add(messageLabel);
        add(Box.createVerticalStrut(20));
        add(loginLabel);
        add(Box.createVerticalGlue());
    }
}
