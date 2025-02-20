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

import java.awt.BorderLayout;
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

    public void onPageDisplayed() {
        onLoadDashboard();

        // 既存のコンポーネントを削除して再描画
        removeAll();
        setLayout(new BorderLayout());

        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> mainFrame.switchTo("Login"));
        add(logoutButton, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        if (this.venders != null) {
            centerPanel.add(new VenderList(this.venders, mainFrame), BorderLayout.CENTER);
        }

        add(centerPanel, BorderLayout.CENTER);

        // 注文がある場合、"View Order" ボタンを表示
        if (this.order != null) {
            JPanel currentOrderPanel = new JPanel();
            JLabel text = new JLabel("Your current Order is: " + this.order.getCurrentStatus());
            JButton button = new JButton("View Order");
            button.addActionListener(e -> mainFrame.switchTo("OrderProgressPage"));
            currentOrderPanel.add(text);
            currentOrderPanel.add(button);

            add(currentOrderPanel, BorderLayout.SOUTH);
        }

        // UI を再描画
        revalidate();
        repaint();

        System.out.println("Customer dashboard loaded");
    }
}
