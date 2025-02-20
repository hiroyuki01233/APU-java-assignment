package com.java_assignment.group.View;

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
        passwordField = new JPasswordField(15);
        loginButton = new JButton("Login");
        messageLabel = new JLabel("");
//        emailAddressField = new JTextField("");
        JTextField emailField = new JTextField(20);

        try {
            authController = new AuthController();
        } catch (IOException e) {
            messageLabel.setText("Error loading users");
        }

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(emailField.getText());
                System.out.println(passwordField.getPassword());

                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                System.out.println(authController.login(email, password));

                if (authController.login(email, password)) {
                    BaseUser user = authController.getCurrentUser();
                    System.out.println(user);
                    System.out.println(user.getUserType());

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
                    }
                    messageLabel.setText("Login successful!");
                } else {
                    messageLabel.setText("Invalid credentials!");
                }
            }
        });

        try {
            Initialize();
        } catch (IOException e) {
            e.printStackTrace();  // エラーを出力
            JOptionPane.showMessageDialog(this, "Error loading user data: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        this.mainFrame = frame;
        this.setLayout(new BoxLayout(this, 1));
        this.setBorder(BorderFactory.createEmptyBorder(50, 20, 50, 20));

        JLabel logoLabel = new JLabel("LOGO", 0);
        logoLabel.setAlignmentX(0.5F);
        logoLabel.setFont(new Font("Arial", 1, 24));

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setAlignmentX(0.5F);
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel passLabel = new JLabel("Password:");
        passLabel.setAlignmentX(0.5F);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JPanel buttonPanel = new JPanel(new FlowLayout(2));
        buttonPanel.add(loginButton);

        JLabel registerLabel = new JLabel("You don't have any account yet? Register", 0);
        registerLabel.setForeground(Color.BLUE);
        registerLabel.setCursor(new Cursor(12));

        JLabel forgotPasswordLabel = new JLabel("Forgot password?", 0);
        forgotPasswordLabel.setForeground(Color.BLUE);
        forgotPasswordLabel.setCursor(new Cursor(12));

        JLabel adminLoginLabel = new JLabel("Are you an admin? Click here to go admin login page", 0);
        adminLoginLabel.setForeground(Color.BLUE);
        adminLoginLabel.setCursor(new Cursor(12));

        adminLoginLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                LoginPage.this.mainFrame.switchTo("AdminLoginPage");
            }
        });

        registerLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                LoginPage.this.mainFrame.switchTo("RegisterPage");
            }
        });

        this.add(Box.createVerticalGlue());
        this.add(logoLabel);
        this.add(Box.createVerticalStrut(30));
        this.add(emailLabel);
        this.add(emailField);
        this.add(Box.createVerticalStrut(10));
        this.add(passLabel);
        this.add(passwordField);
        this.add(messageLabel);
        this.add(Box.createVerticalStrut(20));
        this.add(buttonPanel);
        this.add(Box.createVerticalStrut(20));
        this.add(registerLabel);
        this.add(Box.createVerticalStrut(10));
        this.add(forgotPasswordLabel);
        this.add(adminLoginLabel);
        this.add(Box.createVerticalGlue());
    }

    public void onPageDisplayed() {
        authController.logout();
    }
}

