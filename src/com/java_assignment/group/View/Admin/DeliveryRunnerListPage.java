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

public class DeliveryRunnerListPage extends JPanel {
    private MainFrame mainFrame;
    private DeliveryRunnerController deliveryRunnerController;
    private AuthController authController;
    private JList<DeliveryRunner> list;
    private DefaultListModel<DeliveryRunner> listModel = new DefaultListModel<>();
    private List<DeliveryRunner> deliveryRunners;

    private void onLoadDeliveryRunners() {
        try {
            deliveryRunnerController = new DeliveryRunnerController();
            authController = new AuthController();
            deliveryRunners = deliveryRunnerController.getAllDeliveryRunner();
            System.out.println(deliveryRunners);
            listModel.clear();
            for (DeliveryRunner runner : deliveryRunners) {
                listModel.addElement(runner);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading delivery runners: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadDeliveryRunners();
        System.out.println("Delivery Runner loaded");
    }

    public DeliveryRunnerListPage(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Delivery Runner List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        this.listModel = new DefaultListModel<>();
        this.list = new JList<>(listModel);
        this.list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                                   boolean isSelected, boolean cellHasFocus) {
                java.awt.Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof DeliveryRunner) {
                    DeliveryRunner runner = (DeliveryRunner) value;
                    BaseUser baseUser = (BaseUser) authController.getBaseUserById(((DeliveryRunner) value).getBaseUserId());
                    String showingText =
                            baseUser.getEmailAddress() + " / " +
                                    runner.getFirstName() + " " +
                                    runner.getLastName() + " / " +
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
        JButton registerButton = new JButton("Register New Delivery Runner");
        registerButton.addActionListener(e -> mainFrame.switchTo("DeliveryRunnerRegisterPage"));

        JButton editButton = new JButton("Edit Selected Delivery Runner");
        editButton.addActionListener(e -> {
            DeliveryRunner selected = list.getSelectedValue();

            if (list.getSelectedValue() != null) {
                DeliveryRunnerEditPage editPage = new DeliveryRunnerEditPage(mainFrame, selected);
                mainFrame.addPanel("DeliveryRunnerEditPage", editPage);
                mainFrame.switchTo("DeliveryRunnerEditPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Select a delivery runner to edit.");
            }
        });

        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));
        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
