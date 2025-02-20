
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

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Edit Vender & User");
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

        createdAtField = new JTextField(vender.getCreatedAt(), 20);
        add(new JLabel("Created At:"));
        add(createdAtField);

        deletedAtField = new JTextField(baseUser.getDeletedAt() == null ? "" : baseUser.getDeletedAt(), 20);
        add(new JLabel("Deleted At:"));
        add(deletedAtField);

        isDeletedCheckBox = new JCheckBox();
        isDeletedCheckBox.setSelected(baseUser.getIsDeleted() != null && baseUser.getIsDeleted());
        add(new JLabel("Is Deleted:"));
        add(isDeletedCheckBox);

        storeNameField = new JTextField(vender.getStoreName(), 20);
        add(new JLabel("Store Name:"));
        add(storeNameField);

        storeBackgroundImageField = new JTextField(vender.getStoreBackgroundImage(), 20);
        add(new JLabel("Store Background Image:"));
        add(storeBackgroundImageField);

        storeIconImageField = new JTextField(vender.getStoreIconImage(), 20);
        add(new JLabel("Store Icon Image:"));
        add(storeIconImageField);

        storeDescriptionField = new JTextField(vender.getStoreDescription(), 20);
        add(new JLabel("Store Description:"));
        add(storeDescriptionField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> saveVender());
        add(saveButton);

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.switchTo("VenderListPage"));
        add(backButton);
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
            JOptionPane.showMessageDialog(mainFrame, "Vender and user details updated.");
            mainFrame.switchTo("VenderListPage");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update vender and user details.");
        }
    }
}
