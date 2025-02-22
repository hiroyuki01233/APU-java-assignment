package com.java_assignment.group.View.Shared;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.TxtModelRepository;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class LoginPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JTextField emailAddressField;
    private JLabel messageLabel;


    public void Initialize() throws IOException {
        String filePath = "src\\Data\\base_user.txt";

        TxtModelRepository<BaseUser> repository = new TxtModelRepository<>(
                filePath,
                BaseUser::fromCsv,
                BaseUser::toCsv
        );

        List<BaseUser> users = repository.readAll();
        System.out.println("All BaseUser records:");
        for (BaseUser u : users) {
            u.getEmailAddress();
            System.out.println(u);
        }
    }

    public LoginPage(MainFrame frame) {
        removeAll();
        setBackground(Color.WHITE);

        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        messageLabel = new JLabel("");
        JTextField emailField = new JTextField(20);

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading users");
            messageLabel.setForeground(Color.RED);
        }

        // Maintain existing action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                if (authController.login(email, password)) {
                    BaseUser user = authController.getCurrentUser();
                    messageLabel.setForeground(new Color(46, 125, 50));
                    switch (user.getUserType()){
                        case "customer":
                            frame.switchTo("CustomerDashboard");
                            break;
                        case "admin":
                            frame.switchTo("AdminDashboard");
                            break;
                        case "vender":
                            frame.switchTo("VenderDashboard");
                            break;
                        case "delivery_runner":
                            frame.switchTo("DeliveryRunnerDashboard");
                            break;
                        case "manager":
                            frame.switchTo("ManagerDashboard");
                            break;
                    }
                    messageLabel.setText("Login successful!");
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Invalid credentials!");
                }
            }
        });

        try {
            Initialize();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        this.mainFrame = frame;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(50, 40, 50, 40));

        // Logo and Title Section
        JLabel logoLabel = new JLabel("Munch Time", SwingConstants.CENTER);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(new Color(51, 51, 51));

        JLabel subtitleLabel = new JLabel("All your favourite food in one app!", SwingConstants.CENTER);
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(102, 102, 102));

        // Input Fields Styling
        emailField.setMaximumSize(new Dimension(300, 40));
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        emailField.setAlignmentX(CENTER_ALIGNMENT);

        passwordField.setMaximumSize(new Dimension(300, 40));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passwordField.setAlignmentX(CENTER_ALIGNMENT);

        // Labels Styling
        JLabel emailLabel = new JLabel("Email Address:");
        emailLabel.setAlignmentX(CENTER_ALIGNMENT);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setAlignmentX(CENTER_ALIGNMENT);
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Button Styling
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setMaximumSize(new Dimension(200, 40));
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(new Color(144, 238, 144));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setAlignmentX(CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(124, 218, 124));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(new Color(144, 238, 144));
            }
        });

        // Message Label Styling
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        // Navigation Links Styling
        JLabel registerLabel = new JLabel("Don't have an account? Register now", SwingConstants.CENTER);
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        registerLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel forgotPasswordLabel = new JLabel("Forgot password?", SwingConstants.CENTER);
        forgotPasswordLabel.setForeground(Color.BLUE);
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        forgotPasswordLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel adminLoginLabel = new JLabel("Admin Login", SwingConstants.CENTER);
        adminLoginLabel.setForeground(Color.BLUE);
        adminLoginLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        adminLoginLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        adminLoginLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Maintain existing mouse listeners
        adminLoginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                LoginPage.this.mainFrame.switchTo("AdminLoginPage");
            }
            public void mouseEntered(MouseEvent e) {
                adminLoginLabel.setForeground(Color.DARK_GRAY);
            }
            public void mouseExited(MouseEvent e) {
                adminLoginLabel.setForeground(Color.BLUE);
            }
        });

        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                LoginPage.this.mainFrame.switchTo("RegisterPage");
            }
            public void mouseEntered(MouseEvent e) {
                registerLabel.setForeground(Color.DARK_GRAY);
            }
            public void mouseExited(MouseEvent e) {
                registerLabel.setForeground(Color.BLUE);
            }
        });

        // Layout Assembly
        this.add(Box.createVerticalGlue());
        this.add(logoLabel);
        this.add(Box.createVerticalStrut(5));
        this.add(subtitleLabel);
        this.add(Box.createVerticalStrut(40));
        this.add(emailLabel);
        this.add(Box.createVerticalStrut(5));
        this.add(emailField);
        this.add(Box.createVerticalStrut(15));
        this.add(passLabel);
        this.add(Box.createVerticalStrut(5));
        this.add(passwordField);
        this.add(Box.createVerticalStrut(10));
        this.add(messageLabel);
        this.add(Box.createVerticalStrut(25));
        this.add(loginButton);
        this.add(Box.createVerticalStrut(30));
        this.add(registerLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(forgotPasswordLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(adminLoginLabel);
        this.add(Box.createVerticalGlue());
    }

    public void onPageDisplayed() {
        authController.logout();
    }
}

