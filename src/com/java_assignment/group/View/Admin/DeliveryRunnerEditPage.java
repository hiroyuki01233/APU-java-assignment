package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.DeliveryRunnerController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.DeliveryRunner;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;

public class DeliveryRunnerEditPage extends JPanel {
    private MainFrame mainFrame;
    private DeliveryRunner deliveryRunner;
    private DeliveryRunnerController deliveryRunnerController;
    private AuthController authController;
    private BaseUser baseUser;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField createdAtField;
    private JTextField deletedAtField;
    private JCheckBox isDeletedCheckBox;
    private JTextField firstNameField;
    private JTextField lastNameField;


    public DeliveryRunnerEditPage(MainFrame frame, DeliveryRunner deliveryRunner) {
        this.mainFrame = frame;
        this.deliveryRunner = deliveryRunner;
        try {
            deliveryRunnerController = new DeliveryRunnerController();
            authController = new AuthController();
            this.baseUser = authController.getBaseUserById(deliveryRunner.getBaseUserId());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers.");
        }

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(30, 20, 30, 20));

        JLabel titleLabel = new JLabel("Delivery Runner Edit Page");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(titleLabel);
        add(Box.createVerticalStrut(20));

        emailField = new JTextField(baseUser.getEmailAddress(), 20);
        add(new JLabel("Email Address:"));
        add(emailField);

        passwordField = new JPasswordField(baseUser.getPassword(), 20);
        add(new JLabel("Password:"));
        add(passwordField);

        createdAtField = new JTextField(deliveryRunner.getCreatedAt(), 20);
        add(new JLabel("Created At:"));
        add(createdAtField);

        deletedAtField = new JTextField(baseUser.getDeletedAt() == null ? "" : baseUser.getDeletedAt(), 20);
        add(new JLabel("Deleted At:"));
        add(deletedAtField);

        isDeletedCheckBox = new JCheckBox();
        isDeletedCheckBox.setSelected(baseUser.getIsDeleted() != null && baseUser.getIsDeleted());
        add(new JLabel("Is Deleted:"));
        add(isDeletedCheckBox);

        firstNameField = new JTextField(deliveryRunner.getFirstName(), 20);
        add(new JLabel("First Name:"));
        add(firstNameField);

        lastNameField = new JTextField(deliveryRunner.getLastName(), 20);
        add(new JLabel("Last Name:"));
        add(lastNameField);

        JButton saveButton = new JButton("Save Changes");
        saveButton.setAlignmentX(CENTER_ALIGNMENT);
        saveButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainFrame, "Delivery Runner details updated (dummy).");
            mainFrame.switchTo("DeliveryRunnerListPage");
        });
        add(saveButton);
        add(Box.createVerticalStrut(20));

        JButton backButton = new JButton("Back");
        backButton.setAlignmentX(CENTER_ALIGNMENT);
        backButton.addActionListener(e -> mainFrame.switchTo("DeliveryRunnerListPage"));
        add(backButton);
        add(Box.createVerticalGlue());
    }

    private void saveDeliveryRunner() {
        baseUser.setEmailAddress(emailField.getText().trim());
        baseUser.setPassword(new String(passwordField.getPassword()).trim());
        baseUser.setDeletedAt(deletedAtField.getText().trim());
        baseUser.setIsDeleted(isDeletedCheckBox.isSelected());

        deliveryRunner.setFirstName(firstNameField.getText().trim());
        deliveryRunner.setLastName(lastNameField.getText().trim());

        boolean successDeliveryRunnerUpdate = deliveryRunnerController.updateDeliveryRunner(deliveryRunner);
        boolean successBaseUserUpdate = authController.updateBaseUser(baseUser);

        if (successDeliveryRunnerUpdate && successBaseUserUpdate) {
            JOptionPane.showMessageDialog(mainFrame, "Vender and user details updated.");
            mainFrame.switchTo("VenderListPage");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update vender and user details.");
        }
    }
}
