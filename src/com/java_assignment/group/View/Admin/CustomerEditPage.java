package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.CustomerController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Customer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Page to edit customer and user details.
 */
public class CustomerEditPage extends JPanel {
    private MainFrame mainFrame;
    private CustomerController customerController;
    private AuthController authController;
    private BaseUser baseUser;
    private Customer customer;
    private JTextField emailField, userTypeField, createdAtField, deletedAtField;
    private JPasswordField passwordField;
    private JCheckBox isDeletedCheckBox;
    private JTextField firstNameField, lastNameField, addressField;
    private JLabel messageLabel;

    public CustomerEditPage(MainFrame frame, Customer customer) {
        this.mainFrame = frame;
        this.customer = customer;

        try {
            customerController = new CustomerController();
            authController = new AuthController();
            this.baseUser = authController.getBaseUserById(customer.getId());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers: " + ex.getMessage());
        }

        // Set layout
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Title Label
        JLabel titleLabel = new JLabel("Edit Customer & User", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(45, 52, 54)); // Dark header
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Fields
        emailField = createTextField(baseUser.getEmailAddress());
        passwordField = createPasswordField();
        userTypeField = createTextField(baseUser.getUserType());
        userTypeField.setEditable(false);
        createdAtField = createTextField(customer.getCreatedAt());
        deletedAtField = createTextField(baseUser.getDeletedAt() == null ? "" : baseUser.getDeletedAt());

        isDeletedCheckBox = new JCheckBox("Mark as Deleted", baseUser.getIsDeleted() != null && baseUser.getIsDeleted());

        firstNameField = createTextField(customer.getFirstName());
        lastNameField = createTextField(customer.getLastName());
        addressField = createTextField(customer.getAddress());

        // Add fields to form panel
        addFormField(formPanel, gbc, "Email Address:", emailField);
        addFormField(formPanel, gbc, "Password:", passwordField);
        addFormField(formPanel, gbc, "User Type:", userTypeField);
        addFormField(formPanel, gbc, "Created At:", createdAtField);
        addFormField(formPanel, gbc, "Deleted At:", deletedAtField);

        // CheckBox row
        gbc.gridy++;
        gbc.gridwidth = 2;
        formPanel.add(isDeletedCheckBox, gbc);
        gbc.gridwidth = 1;

        addFormField(formPanel, gbc, "First Name:", firstNameField);
        addFormField(formPanel, gbc, "Last Name:", lastNameField);
        addFormField(formPanel, gbc, "Address:", addressField);

        // Message Label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(248, 249, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton saveButton = createStyledButton("Save Changes", new Color(52, 152, 219));
        JButton backButton = createStyledButton("Back", new Color(52, 152, 219));

        saveButton.addActionListener(e -> saveCustomer());
        backButton.addActionListener(e -> mainFrame.switchTo("CustomerListPage"));

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        // Create a wrapper panel for the form
        JPanel wrapperPanel = new JPanel(new BorderLayout(0, 20));
        wrapperPanel.setBackground(new Color(248, 249, 250));
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        wrapperPanel.add(formPanel, BorderLayout.CENTER);
        wrapperPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add components to the main panel
        add(titleLabel, BorderLayout.NORTH);
        add(wrapperPanel, BorderLayout.CENTER);
        add(messageLabel, BorderLayout.SOUTH);

        // Ensure UI updates
        SwingUtilities.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    /**
     * Adds a form field with a label to the panel.
     */
    private void addFormField(JPanel panel, GridBagConstraints gbc, String label, JComponent field) {
        gbc.gridy++;
        gbc.gridx = 0;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    /**
     * Creates a styled JTextField.
     */
    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    /**
     * Creates a styled JPasswordField.
     */
    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField(20);
        field.setPreferredSize(new Dimension(200, 30));
        return field;
    }

    /**
     * Creates a styled JButton with a background color.
     */
    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(background.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(background.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(background);
            }
        });

        return button;
    }

    /**
     * Saves the customer details.
     */
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
        boolean successUserUpdate = authController.updateBaseUser(baseUser);

        if (successCustomerUpdate && successUserUpdate) {
            JOptionPane.showMessageDialog(mainFrame, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.switchTo("CustomerListPage");
        } else {
            messageLabel.setText("Error updating customer details.");
        }
    }
}
