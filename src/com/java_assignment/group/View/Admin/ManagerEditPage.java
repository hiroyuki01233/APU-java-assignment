package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;

import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ManagerEditPage extends JPanel {
    private MainFrame mainFrame;
    private AuthController authController;
    private BaseUser baseUser;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField deletedAtField;
    private JCheckBox isDeletedCheckBox;

    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color BUTTON_COLOR = new Color(66, 139, 202);
    private static final Color BUTTON_HOVER_COLOR = new Color(51, 122, 183);
    private static final Color SUCCESS_BUTTON_COLOR = new Color(92, 184, 92);
    private static final Color SUCCESS_BUTTON_HOVER_COLOR = new Color(68, 157, 68);
    private static final Color TEXT_COLOR = Color.WHITE;
    private static final Color LABEL_COLOR = new Color(73, 80, 87);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);
    private static final int FIELD_HEIGHT = 35;
    private static final int VERTICAL_GAP = 15;

    public ManagerEditPage(MainFrame frame, BaseUser user) {
        this.mainFrame = frame;
        try {
            this.authController = new AuthController();
            this.baseUser = user;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(mainFrame, "Error initializing controllers.");
        }

        // Main panel setup
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        // Create a container panel for the form with proper padding
        JPanel containerPanel = new JPanel();
        containerPanel.setLayout(new BoxLayout(containerPanel, BoxLayout.Y_AXIS));
        containerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        containerPanel.setBackground(BACKGROUND_COLOR);

        // Title section
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = createStyledLabel("Manager Edit Page", TITLE_FONT);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(25));

        // Form section
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(BACKGROUND_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // User Information Group
        JPanel userInfoPanel = createFormGroup("User Information");
        addFormField("Email Address:", emailField = createStyledTextField(baseUser.getEmailAddress()), userInfoPanel);
        addFormField("Password:", passwordField = createStyledPasswordField(baseUser.getPassword()), userInfoPanel);

        // Add checkbox with proper styling
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.setBackground(Color.WHITE);
        JLabel deleteLabel = createStyledLabel("Is Deleted:", LABEL_FONT);
        isDeletedCheckBox = new JCheckBox();
        isDeletedCheckBox.setSelected(baseUser.getIsDeleted() != null && baseUser.getIsDeleted());
        isDeletedCheckBox.setBackground(Color.WHITE);
        checkboxPanel.add(deleteLabel);
        checkboxPanel.add(isDeletedCheckBox);
        userInfoPanel.add(checkboxPanel);

        formPanel.add(userInfoPanel);
        formPanel.add(Box.createVerticalStrut(20));


        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton saveButton = createStyledButton("Save Changes");
        saveButton.setBackground(new Color(46, 204, 113));
        saveButton.setMaximumSize(new Dimension(250, 40));
        saveButton.setPreferredSize(new Dimension(250, 40));
        saveButton.addActionListener(e -> saveManager());

        JButton backButton = createStyledButton("Back");
        backButton.setBackground(new Color(52, 152, 219));
        backButton.setMaximumSize(new Dimension(250, 40));
        backButton.setPreferredSize(new Dimension(250, 40));
        backButton.addActionListener(e -> mainFrame.switchTo("ManagerListPage"));

        buttonPanel.add(saveButton);
        buttonPanel.add(backButton);

        // Add all panels to container
        containerPanel.add(titlePanel);
        containerPanel.add(formPanel);
        containerPanel.add(buttonPanel);

        // Add container to main panel
        add(containerPanel, BorderLayout.CENTER);
    }

    private JPanel createFormGroup(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), title),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        return panel;
    }

    private void addFormField(String labelText, JComponent field, JPanel container) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel label = createStyledLabel(labelText, LABEL_FONT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);

        container.add(panel);
        container.add(Box.createVerticalStrut(VERTICAL_GAP));
    }

    private void addFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel label = createStyledLabel(labelText, LABEL_FONT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);

        add(panel);
        add(Box.createVerticalStrut(VERTICAL_GAP));
    }

    private JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(LABEL_COLOR);
        return label;
    }

    private JTextField createStyledTextField(String text) {
        JTextField field = new JTextField(text, 20);
        field.setMaximumSize(new Dimension(400, FIELD_HEIGHT));
        field.setPreferredSize(new Dimension(400, FIELD_HEIGHT));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JPasswordField createStyledPasswordField(String text) {
        JPasswordField field = new JPasswordField(text, 20);
        field.setMaximumSize(new Dimension(400, FIELD_HEIGHT));
        field.setPreferredSize(new Dimension(400, FIELD_HEIGHT));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 35));
        button.setPreferredSize(new Dimension(200, 35));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    private void saveManager() {
        baseUser.setEmailAddress(emailField.getText().trim());
        baseUser.setPassword(new String(passwordField.getPassword()).trim());
        baseUser.setIsDeleted(isDeletedCheckBox.isSelected());

        boolean successBaseUserUpdate = authController.updateBaseUser(baseUser);

        if (successBaseUserUpdate) {
            JOptionPane.showMessageDialog(mainFrame, "Manager details updated successfully.");
            mainFrame.switchTo("ManagerListPage");
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Failed to update manager details.");
        }
    }
}
