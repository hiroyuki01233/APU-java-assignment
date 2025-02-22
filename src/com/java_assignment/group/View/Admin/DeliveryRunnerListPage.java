package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.DeliveryRunnerController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.DeliveryRunner;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class DeliveryRunnerListPage extends JPanel {
    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color BUTTON_COLOR = new Color(66, 139, 202);       // change the blue hue
    private static final Color BUTTON_HOVER_COLOR = Color.BLUE.darker();
    private static final Color LIST_ITEM_COLOR = Color.BLACK;
    private static final Color LIST_HOVER_COLOR = new Color(230, 230, 230);
    private static final Color LIST_SELECTED_COLOR = Color.LIGHT_GRAY;
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 28);
    private static final Font LIST_FONT = new Font("Arial", Font.PLAIN, 14);

    private MainFrame mainFrame;
    private DeliveryRunnerController deliveryRunnerController;
    private AuthController authController;
    private JList<DeliveryRunner> list;
    private DefaultListModel<DeliveryRunner> listModel = new DefaultListModel<>();
    private List<DeliveryRunner> deliveryRunners;

    private void onLoadDeliveryRunners() {
        try {
            this.deliveryRunnerController = new DeliveryRunnerController();
            this.authController = new AuthController();
            this.deliveryRunners = this.deliveryRunnerController.getAllDeliveryRunner();
            System.out.println(this.deliveryRunners);
            this.listModel.clear();
            for (DeliveryRunner runner : this.deliveryRunners) {
                this.listModel.addElement(runner);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading delivery runners: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadDeliveryRunners();
        System.out.println("Delivery Runner loaded");
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(300, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_COLOR);
            }
        });

        return button;
    }

    public DeliveryRunnerListPage(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout(20, 20));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("Delivery Runner List");
        titleLabel.setFont(TITLE_FONT);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // List Panel
        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(listModel);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.list.setFont(LIST_FONT);
        this.list.setBackground(BACKGROUND_COLOR);
        this.list.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        this.list.setSelectionBackground(LIST_SELECTED_COLOR);
        this.list.setSelectionForeground(Color.WHITE);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DeliveryRunner) {
                    DeliveryRunner runner = (DeliveryRunner) value;
                    BaseUser baseUser = (BaseUser) authController.getBaseUserById(runner.getBaseUserId());
                    String showingText =
                            baseUser.getEmailAddress() + " / " +
                                    runner.getFirstName() + " " +
                                    runner.getLastName() + " / " +
                                    baseUser.getId();
                    if (baseUser.getIsDeleted()) {
                        showingText = "DELETED " + showingText;
                        setForeground(Color.BLACK);
                        setBackground(isSelected ? LIST_SELECTED_COLOR : new Color(255, 240, 240));
                    } else {
                        setForeground(Color.BLACK);
                        setBackground(isSelected ? LIST_SELECTED_COLOR : 
                            cellHasFocus ? LIST_HOVER_COLOR : new Color(245, 245, 245));
                    }
                    setText(showingText);
                    setFont(LIST_FONT);
                }
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));

        JButton registerButton = createStyledButton("Register New Delivery Runner");
        registerButton.addActionListener(e -> mainFrame.switchTo("DeliveryRunnerRegisterPage"));

        JButton editButton = createStyledButton("Edit Selected Delivery Runner");
        editButton.addActionListener(e -> {
            DeliveryRunner selected = list.getSelectedValue();
            if (selected != null) {
                DeliveryRunnerEditPage editPage = new DeliveryRunnerEditPage(mainFrame, selected);
                mainFrame.addPanel("DeliveryRunnerEditPage", editPage);
                mainFrame.switchTo("DeliveryRunnerEditPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Select a delivery runner to edit.");
            }
        });

        JButton backButton = createStyledButton("Back");
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        editButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        buttonPanel.add(registerButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(editButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
