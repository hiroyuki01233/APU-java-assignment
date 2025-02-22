package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.CustomerController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Customer;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class CustomerListPage extends JPanel {
    private MainFrame mainFrame;
    private CustomerController customerController;
    private AuthController authController;
    private JList<Customer> customerList;
    private DefaultListModel<Customer> listModel;
    private List<Customer> customers;
    private JTextField searchField;
    private JPanel contentPanel;

    // Define colors for consistency
    private static final Color BUTTON_BLUE = new Color(52, 152, 219);
    private static final Color BUTTON_RED = new Color(231, 76, 60);

    public CustomerListPage(MainFrame frame) {
        this.mainFrame = frame;

        try {
            this.customerController = new CustomerController();
            this.authController = new AuthController();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error initializing controllers: " + e.getMessage());
            e.printStackTrace();
        }

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Color.WHITE);

        createHeader();
        createSearchPanel();
        createCustomerListPanel();
        createButtonPanel();
    }

    private void createHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(44, 62, 80));

        JLabel subtitleLabel = new JLabel("View and manage customer accounts");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(127, 140, 141));

        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);
    }

    private void createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        searchPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton searchButton = createStyledButton("Search", BUTTON_BLUE);
        searchButton.addActionListener(e -> performSearch());

        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.CENTER);
    }

    private void createCustomerListPanel() {
        listModel = new DefaultListModel<>();
        customerList = new JList<>(listModel);
        customerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerList.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        customerList.setBackground(Color.WHITE);

        customerList.setCellRenderer(new CustomerListCellRenderer());

        JScrollPane scrollPane = new JScrollPane(customerList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        listPanel.setBackground(Color.WHITE);
        listPanel.add(scrollPane, BorderLayout.CENTER);

        add(listPanel, BorderLayout.CENTER);
    }

    private void createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton registerButton = createStyledButton("Register New Customer", BUTTON_BLUE);
        JButton editButton = createStyledButton("Edit Selected", BUTTON_BLUE);
        JButton deleteButton = createStyledButton("Delete Selected", BUTTON_RED);
        JButton backButton = createStyledButton("Back to Dashboard", BUTTON_BLUE);

        registerButton.addActionListener(e -> mainFrame.switchTo("CustomerRegisterPage"));
        editButton.addActionListener(e -> editSelectedCustomer());
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 35));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true); // Make button opaque to show background color
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFont(new Font("Arial", Font.BOLD, 12));

        // Add hover effect
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

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        listModel.clear();

        for (Customer customer : customers) {
            BaseUser baseUser = authController.getBaseUserById(customer.getId());
            if (baseUser.getEmailAddress().toLowerCase().contains(searchTerm) ||
                    customer.getFirstName().toLowerCase().contains(searchTerm) ||
                    customer.getLastName().toLowerCase().contains(searchTerm)) {
                listModel.addElement(customer);
            }
        }
    }

    private void editSelectedCustomer() {
        Customer selected = customerList.getSelectedValue();
        if (selected != null) {
            CustomerEditPage editPage = new CustomerEditPage(mainFrame, selected);
            mainFrame.addPanel("CustomerEditPage", editPage);
            mainFrame.switchTo("CustomerEditPage");
        } else {
            showErrorMessage("Please select a customer to edit.");
        }
    }

    private void deleteSelectedCustomer() {
        Customer selected = customerList.getSelectedValue();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(
                    mainFrame,
                    "Are you sure you want to delete this customer?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (authController.deleteBaseUser(selected.getId())) {
                    showSuccessMessage("Customer successfully deleted.");
                    listModel.removeElement(selected);
                } else {
                    showErrorMessage("Failed to delete customer.");
                }
            }
        } else {
            showErrorMessage("Please select a customer to delete.");
        }
    }

    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onPageDisplayed() {
        try {
            customers = customerController.getAllCustomers();
            listModel.clear();
            for (Customer customer : customers) {
                listModel.addElement(customer);
            }
            searchField.setText("");
        } catch (IOException ex) {
            showErrorMessage("Error loading customers: " + ex.getMessage());
        }
    }

    private class CustomerListCellRenderer extends JPanel implements ListCellRenderer<Customer> {
        private JLabel nameLabel = new JLabel();
        private JLabel emailLabel = new JLabel();
        private JLabel idLabel = new JLabel();
        private JLabel statusLabel = new JLabel();

        public CustomerListCellRenderer() {
            setLayout(new GridLayout(2, 2, 10, 5));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            add(nameLabel);
            add(emailLabel);
            add(idLabel);
            add(statusLabel);

            setBackground(Color.WHITE);
        }

        @Override
        public Component getListCellRendererComponent(
                JList<? extends Customer> list, Customer customer, int index,
                boolean isSelected, boolean cellHasFocus) {

            BaseUser baseUser = authController.getBaseUserById(customer.getId());

            nameLabel.setText(customer.getFirstName() + " " + customer.getLastName());
            emailLabel.setText(baseUser.getEmailAddress());
            idLabel.setText("ID: " + baseUser.getId());

            if (baseUser.getIsDeleted()) {
                statusLabel.setText("DELETED");
                statusLabel.setForeground(Color.RED);
            } else {
                statusLabel.setText("Active");
                statusLabel.setForeground(new Color(46, 204, 113));
            }

            if (isSelected) {
                setBackground(new Color(52, 152, 219, 50));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
                        BorderFactory.createEmptyBorder(9, 9, 9, 9)
                ));
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }

            return this;
        }
    }
}