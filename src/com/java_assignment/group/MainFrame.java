package com.java_assignment.group;

import com.java_assignment.group.Model.Admin;
import com.java_assignment.group.Model.TxtModelRepository;
import com.java_assignment.group.Model.Vender;
import com.java_assignment.group.View.*;
import com.java_assignment.group.View.Admin.*;
import com.java_assignment.group.View.Customer.CustomerDashboard;
import com.java_assignment.group.View.Customer.OrderProgressPage;
import com.java_assignment.group.View.DeliveryRunner.DeliveryRunnerDashboard;
import com.java_assignment.group.View.Vender.VenderDashboard;
import com.java_assignment.group.View.Vender.VenderMenuListPage;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;

    public MainFrame() {
        this.setTitle("Food Ordering System");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(400, 800);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        this.add(mainPanel);

        // Existing pages
        mainPanel.add(new LoginPage(this), "Login");
        mainPanel.add(new RegisterPage(this), "RegisterPage");
        mainPanel.add(new CustomerDashboard(this), "CustomerDashboard");

        // Admin Dashboard
        // New admin and management pages
        mainPanel.add(new AdminDashboard(this), "AdminDashboard");
        mainPanel.add(new AdminRegistrationPage(this), "AdminRegistrationPage");
        mainPanel.add(new AdminLoginPage(this), "AdminLoginPage");
        // Customer management pages
        mainPanel.add(new CustomerRegisterPage(this), "CustomerRegisterPage");
        mainPanel.add(new CustomerListPage(this), "CustomerListPage");
        // Vender management pages
        mainPanel.add(new VenderRegisterPage(this), "VenderRegisterPage");
        mainPanel.add(new VenderListPage(this), "VenderListPage");
        // Delivery Runner management pages
        mainPanel.add(new DeliveryRunnerRegisterPage(this), "DeliveryRunnerRegisterPage");
        mainPanel.add(new DeliveryRunnerListPage(this), "DeliveryRunnerListPage");

        mainPanel.add(new WalletManagePage(this), "WalletManagePage");

        // Add the panel and show the default page
        mainPanel.add(new VenderDashboard(this), "VenderDashboard");
        mainPanel.add(new VenderMenuListPage(this), "VenderMenuListPage");

        mainPanel.add(new OrderProgressPage(this), "OrderProgressPage");

        mainPanel.add(new DeliveryRunnerDashboard(this), "DeliveryRunnerDashboard");


        // At startup, check if there is an admin and vender record.
        try {
            // Create temporary repositories to check admin and vender data
            TxtModelRepository<Admin> adminRepo = new TxtModelRepository<>(
                    "src/Data/admin.txt",
                    Admin::fromCsv,
                    Admin::toCsv
            );
            List<Admin> admins = adminRepo.readAll();

            TxtModelRepository<Vender> venderRepo = new TxtModelRepository<>(
                    "src/Data/vender.txt",
                    Vender::fromCsv,
                    Vender::toCsv
            );
            List<Vender> venders = venderRepo.readAll();

            if (admins.isEmpty()) {
                // No admin exists: navigate to Admin Registration Page
                cardLayout.show(mainPanel, "AdminRegistrationPage");
            } else if (!admins.isEmpty() && venders.isEmpty()) {
                // Admin exists but no vender: navigate to Admin Login Page
                cardLayout.show(mainPanel, "AdminLoginPage");
            } else {
                // Default: show regular login page
                cardLayout.show(mainPanel, "Login");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // In case of error, show regular login page
            cardLayout.show(mainPanel, "Login");
        }

//        cardLayout.show(mainPanel, "Login");

//        cardLayout.show(mainPanel, "OrderProgressPage");

//        cardLayout.show(mainPanel, "OrderProgressPage");

    }

    public void addPanel(String key, JPanel panel) {
        mainPanel.add(panel, key);
    }

    public void switchTo(String pageName) {
        cardLayout.show(mainPanel, pageName);

        Component currentPanel = null;
        for (Component comp : mainPanel.getComponents()) {
            if (comp.isVisible()) {
                currentPanel = comp;
                break;
            }
        }

        if (currentPanel instanceof CustomerListPage) {
            ((CustomerListPage) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof VenderListPage) {
            ((VenderListPage) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof LoginPage) {
            ((LoginPage) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof  DeliveryRunnerListPage) {
            ((DeliveryRunnerListPage) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof VenderDashboard) {
            ((VenderDashboard) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof VenderMenuListPage) {
            ((VenderMenuListPage) currentPanel).onPageDisplayed();
        }
        if(currentPanel instanceof CustomerDashboard){
            ((CustomerDashboard) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof OrderProgressPage) {
            ((OrderProgressPage) currentPanel).onPageDisplayed();
        }
        if (currentPanel instanceof DeliveryRunnerDashboard) {
            ((DeliveryRunnerDashboard) currentPanel).onPageDisplayed();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
