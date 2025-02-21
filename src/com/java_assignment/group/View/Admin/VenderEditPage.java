
package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Vender;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;


public class VenderEditPage extends JPanel {
    private MainFrame mainFrame;
    private VenderController venderController;
    private AuthController authController;
    private Vender vender;
    private BaseUser baseUser;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField userTypeField;
    private JTextField createdAtField;
    private JCheckBox isCurrentUserCheckBox;
    private JTextField deletedAtField;
    private JCheckBox isDeletedCheckBox;
    private JTextField storeNameField;
    private JTextField storeBackgroundImageField;
    private JTextField storeIconImageField;
    private JTextField storeDescriptionField;

    public VenderEditPage(MainFrame frame, Vender vender) {
        this.mainFrame = frame;
        this.vender = vender;
        try {
            venderController = new VenderController();
            authController = new AuthController();
            this.baseUser = authController.getBaseUserById(vender.getBaseUserId());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers.");
        }

        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(100, 149, 237)); // Cornflower blue instead of pure blue
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Edit Vender & User");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;

        // User Information Section
        addSectionLabel(formPanel, gbc, "User Information");
        emailField = createStyledTextField(baseUser.getEmailAddress());
        addFormField(formPanel, gbc, "Email Address:", emailField);

        passwordField = createStyledPasswordField(baseUser.getPassword());
        addFormField(formPanel, gbc, "Password:", passwordField);

        userTypeField = createStyledTextField(baseUser.getUserType());
        userTypeField.setEditable(false);
        addFormField(formPanel, gbc, "User Type:", userTypeField);

        createdAtField = createStyledTextField(vender.getCreatedAt());
        addFormField(formPanel, gbc, "Created At:", createdAtField);

        deletedAtField = createStyledTextField(baseUser.getDeletedAt() == null ? "" : baseUser.getDeletedAt());
        addFormField(formPanel, gbc, "Deleted At:", deletedAtField);

        isDeletedCheckBox = new JCheckBox("Mark as Deleted");
        isDeletedCheckBox.setSelected(baseUser.getIsDeleted() != null && baseUser.getIsDeleted());
        isDeletedCheckBox.setBackground(Color.WHITE);
        addFormField(formPanel, gbc, "", isDeletedCheckBox);

        // Store Information Section
        addSectionLabel(formPanel, gbc, "Store Information");
        storeNameField = createStyledTextField(vender.getStoreName());
        addFormField(formPanel, gbc, "Store Name:", storeNameField);

        storeBackgroundImageField = createStyledTextField(vender.getStoreBackgroundImage());
        addFormField(formPanel, gbc, "Store Background Image:", storeBackgroundImageField);

        storeIconImageField = createStyledTextField(vender.getStoreIconImage());
        addFormField(formPanel, gbc, "Store Icon Image:", storeIconImageField);

        storeDescriptionField = createStyledTextField(vender.getStoreDescription());
        addFormField(formPanel, gbc, "Store Description:", storeDescriptionField);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton saveButton = createStyledButton("Save Changes", new Color(100, 149, 237));
        JButton backButton = createStyledButton("Back", new Color(100, 149, 237));

        saveButton.addActionListener(e -> saveVender());
        backButton.addActionListener(e -> mainFrame.switchTo("VenderListPage"));

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        // Scroll Pane for Form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Add all components to the main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addSectionLabel(JPanel panel, GridBagConstraints gbc, String text) {
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 10, 5);
        JLabel sectionLabel = new JLabel(text);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sectionLabel.setForeground(new Color(100, 149, 237)); // Cornflower blue for section labels
        panel.add(sectionLabel, gbc);
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridy++;
        gbc.gridx = 0;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(label, gbc);

        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setPreferredSize(new Dimension(250, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField(String text) {
        JPasswordField field = new JPasswordField(text, 20);
        field.setPreferredSize(new Dimension(250, 30));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(background);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBorderPainted(true);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(background.darker(), 1),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
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

    private void saveVender() {
        baseUser.setEmailAddress(emailField.getText().trim());
        baseUser.setPassword(new String(passwordField.getPassword()).trim());
        baseUser.setDeletedAt(deletedAtField.getText().trim());
        baseUser.setIsDeleted(isDeletedCheckBox.isSelected());

        vender.setStoreName(storeNameField.getText().trim());
        vender.setStoreBackgroundImage(storeBackgroundImageField.getText().trim());
        vender.setStoreIconImage(storeIconImageField.getText().trim());
        vender.setStoreDescription(storeDescriptionField.getText().trim());

        boolean successVenderUpdate = venderController.updateVender(vender);
        boolean successBaseUserUpdate = authController.updateBaseUser(baseUser);

        if (successVenderUpdate && successBaseUserUpdate) {
            JOptionPane.showMessageDialog(mainFrame, "Vender and user details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.switchTo("VenderListPage");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update vender and user details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
