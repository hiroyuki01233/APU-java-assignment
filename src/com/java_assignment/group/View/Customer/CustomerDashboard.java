package com.java_assignment.group.View.Customer;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Component.FooterPanel;
import com.java_assignment.group.Component.HeaderPanel;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Order;
import com.java_assignment.group.Model.Vender;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class CustomerDashboard extends JPanel {
    private MainFrame mainFrame;
    private VenderController venderController;
    private List<Vender> venders;
    private OrderController orderController;
    private Order order;
    private AuthController authController;
    private BaseUser baseUser;

    public CustomerDashboard(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }

    private void onLoadDashboard() {
        try {
            this.authController = new AuthController();
            this.orderController = new OrderController();
            this.baseUser = authController.getCurrentUser();
            if (baseUser == null) {
                return;
            }
            this.venderController = new VenderController();
            this.venders = venderController.getAllVenders();
            this.order = orderController.getCurrentOrder(baseUser.getId());

            System.out.println("Current Order: " + order);
            System.out.println("Vender List: " + this.venders);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading: " + ex.getMessage());
        }
    }

    private void styleButton(JButton button) {
        button.setBackground(Color.BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                Color originalColor = button.getBackground();
                button.setBackground(originalColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                if (button.getText().equals("Logout")) {
                    button.setBackground(new Color(220, 53, 69)); // Bootstrap red
                } else if (button.getText().equals("Order Now")) {
                    button.setBackground(new Color(40, 167, 69)); // Bootstrap green
                } else {
                    button.setBackground(new Color(0, 123, 255)); // Bootstrap blue
                }
            }
        });

        if (button.getText().equals("Logout")) {
            button.setBackground(new Color(220, 53, 69)); // Bootstrap red
        } else if (button.getText().equals("Order Now")) {
            button.setBackground(new Color(40, 167, 69)); // Bootstrap green
        } else {
            button.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        }

        if (button.getText().equals("Logout")) {
            button.setBackground(new Color(220, 53, 69)); // Bootstrap red
        } else {
            button.setBackground(new Color(0, 123, 255)); // Bootstrap blue
        }
    }

    public void onPageDisplayed() {
        onLoadDashboard();

        removeAll();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel with modern styling
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Food Delivery");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(33, 37, 41)); // Bootstrap dark

        JButton logoutButton = new JButton("Logout");
        styleButton(logoutButton);
        logoutButton.addActionListener(e -> mainFrame.switchTo("Login"));

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);


        // Center Panel with modern styling
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        if (this.venders != null) {
            centerPanel.add(new VenderList(this.venders, mainFrame), BorderLayout.CENTER);
        }
        add(centerPanel, BorderLayout.CENTER);

        // Footer Panel with modern styling
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Current Order Panel with modern styling
        if (this.order != null) {
            JPanel currentOrderPanel = new JPanel();
            currentOrderPanel.setLayout(new BoxLayout(currentOrderPanel, BoxLayout.X_AXIS));
            currentOrderPanel.setBackground(new Color(248, 249, 250)); // Bootstrap light gray
            currentOrderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230), 1), // Bootstrap border color
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            JLabel text = new JLabel("Current Order: " + this.order.getCurrentStatus());
            text.setFont(new Font("Arial", Font.PLAIN, 14));
            text.setForeground(new Color(33, 37, 41)); // Bootstrap dark
            
            JButton viewOrderButton = new JButton("Track Order");
            styleButton(viewOrderButton);
            viewOrderButton.addActionListener(e -> mainFrame.switchTo("OrderProgressPage"));
            
            currentOrderPanel.add(text);
            currentOrderPanel.add(Box.createHorizontalGlue());
            currentOrderPanel.add(viewOrderButton);
            footerPanel.add(currentOrderPanel);
            footerPanel.add(Box.createVerticalStrut(20));
        }

        // Footer Buttons Panel with modern styling
        JPanel footerButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        footerButtonPanel.setBackground(Color.WHITE);

        String[] buttonLabels = {"Order History", "Transactions", "Notifications"};
        String[] targetPages = {"OrderHistoryPage", "RevenueDashboard", "NotificationPage"};

        for (int i = 0; i < buttonLabels.length; i++) {
            JButton button = new JButton(buttonLabels[i]);
            String targetPage = targetPages[i];
            styleButton(button);
            button.addActionListener(e -> mainFrame.switchTo(targetPage));
            footerButtonPanel.add(button);
        }

        footerPanel.add(footerButtonPanel);
        add(footerPanel, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }
}
