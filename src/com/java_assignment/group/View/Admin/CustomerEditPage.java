package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.CustomerController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Customer;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
public class CustomerEditPage extends JPanel {
    private MainFrame mainFrame;
    private CustomerController customerController;
    private AuthController authController;
    private BaseUser baseUser;
    private Customer customer;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField userTypeField;
    private JTextField createdAtField;
    private JCheckBox isCurrentUserCheckBox;
    private JTextField deletedAtField;
    private JCheckBox isDeletedCheckBox;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField addressField;

    public CustomerEditPage(MainFrame frame, Customer customer) {
        this.mainFrame = frame;
        this.customer = customer;
        this.baseUser = baseUser;

        try {
            customerController = new CustomerController();
            authController = new AuthController();
            this.baseUser = authController.getBaseUserById(customer.getId());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers: " + ex.getMessage());
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Edit Customer & User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        emailField = new JTextField(baseUser.getEmailAddress(), 20);
        add(new JLabel("Email Address:"));
        add(emailField);

        passwordField = new JPasswordField(baseUser.getPassword(), 20);
        add(new JLabel("Password:"));
        add(passwordField);

        userTypeField = new JTextField(baseUser.getUserType(), 20);
        userTypeField.setEditable(false);
        add(new JLabel("User Type:"));
        add(userTypeField);

        createdAtField = new JTextField(customer.getCreatedAt(), 20);
        add(new JLabel("Created At:"));
        add(createdAtField);
//
//        isCurrentUserCheckBox = new JCheckBox();
//        isCurrentUserCheckBox.setSelected(baseUser.isCurrentUser());
//        isCurrentUserCheckBox.setEnabled(false);
//        add(new JLabel("Is Current User:"));
//        add(isCurrentUserCheckBox);

        deletedAtField = new JTextField(baseUser.getDeletedAt() == null ? "" : baseUser.getDeletedAt(), 20);
        add(new JLabel("Deleted At:"));
        add(deletedAtField);

        isDeletedCheckBox = new JCheckBox();
        isDeletedCheckBox.setSelected(baseUser.getIsDeleted() != null && baseUser.getIsDeleted());
        add(new JLabel("Is Deleted:"));
        add(isDeletedCheckBox);

        firstNameField = new JTextField(this.customer != null ? this.customer.getFirstName() : "", 20);
        add(new JLabel("First Name:"));
        add(firstNameField);

        lastNameField = new JTextField(this.customer != null ? this.customer.getLastName() : "", 20);
        add(new JLabel("Last Name:"));
        add(lastNameField);

        addressField = new JTextField(this.customer != null ? this.customer.getAddress() : "", 20);
        add(new JLabel("Address:"));
        add(addressField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> saveCustomer());
        add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.switchTo("CustomerListPage"));
        add(backButton);
    }

    private void saveCustomer() {
        baseUser.setEmailAddress(emailField.getText().trim());
        baseUser.setPassword(new String(passwordField.getPassword()).trim());
        baseUser.setDeletedAt(deletedAtField.getText().trim());
        baseUser.setIsDeleted(isDeletedCheckBox.isSelected());

        customer.setCreatedAt(createdAtField.getText().trim());
        customer.setFirstName(firstNameField.getText().trim());
        customer.setLastName(lastNameField.getText().trim());
        customer.setAddress(addressField.getText().trim());

        boolean successCustomerUpdate = customerController.updateCustomer(customer);
        boolean successBaseUserUpdate = authController.updateBaseUser(baseUser);

        if (successCustomerUpdate && successBaseUserUpdate) {
            JOptionPane.showMessageDialog(mainFrame, "Customer and user details updated.");
            mainFrame.switchTo("CustomerListPage");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update customer and user details.");
        }
    }
}
