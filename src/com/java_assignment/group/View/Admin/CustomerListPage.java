package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.CustomerController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Customer;
import javafx.scene.layout.BorderRepeat;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

public class CustomerListPage extends JPanel {
    private MainFrame mainFrame;
    private CustomerController customerController;
    private AuthController authController;
    private JList<Customer> list;
    private DefaultListModel<Customer> listModel = new DefaultListModel<>();
    private List<Customer> customers;

    private void onLoadCustomer() {
        try {
            customerController = new CustomerController();
            authController = new AuthController();
            customers = customerController.getAllCustomers();

            // リストモデルをリセット
            listModel.clear();

            for (Customer customer : customers) {
                listModel.addElement(customer);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading customers: " + ex.getMessage());
        }
    }

    public void onPageDisplayed(){
        this.onLoadCustomer();
        System.out.println("loaded");
    }

    public CustomerListPage(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Customer List", SwingConstants.CENTER);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(listModel);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Customer) {
                    Customer customer = (Customer) value;
                    BaseUser baseUser = authController.getBaseUserById(((Customer) value).getId());
                    String showingText =
                            baseUser.getEmailAddress() + " / " +
                                    customer.getFirstName() + " " +
                                    customer.getLastName() + " / " +
                                    customer.getAddress() + " / " +
                                    baseUser.getId();
                    if (baseUser.getIsDeleted()){
                        showingText = "DELETED "+showingText;
                    }
                    setText(showingText);
                }
                return c;
            }
        });
        JScrollPane scrollPane = new JScrollPane(list);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel2 = new JPanel(new FlowLayout());
        JButton registerButton = new JButton("Register New Customer");
        registerButton.addActionListener(e -> mainFrame.switchTo("CustomerRegisterPage"));

        JButton editButton = new JButton("Edit Selected Customer");
        editButton.addActionListener(e -> {
            Customer selected = list.getSelectedValue();
            if (selected != null) {
                // Dynamically create and add an edit page for the selected customer
                CustomerEditPage editPage = new CustomerEditPage(mainFrame, selected);
                mainFrame.addPanel("CustomerEditPage", editPage);
                mainFrame.switchTo("CustomerEditPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Select a customer to edit.");
            }
        });

        JButton deleteButton = new JButton("Delete Selected Customer");
        deleteButton.addActionListener(e -> {
            Customer selected = list.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete this customer?");
                if (confirm == JOptionPane.YES_OPTION) {
                    if (authController.deleteBaseUser(selected.getId())) {
                        JOptionPane.showMessageDialog(mainFrame, "Customer deleted.");
                        listModel.removeElement(selected);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Failed to delete customer.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Select a customer to delete.");
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH, BorderRepeat.REPEAT.ordinal());
    }
}
