package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.VenderController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Vender;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class VenderListPage extends JPanel {
    private MainFrame mainFrame;
    private VenderController venderController;
    private AuthController authController;
    private JList<Vender> list;
    private DefaultListModel<Vender> listModel = new DefaultListModel<>();
    private List<Vender> venders;

    private void onLoadVenders() {
        try {
            venderController = new VenderController();
            authController = new AuthController();
            venders = venderController.getAllVenders();
            listModel.clear();
            for (Vender vender : venders) {
                listModel.addElement(vender);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(mainFrame, "Error loading venders: " + ex.getMessage());
        }
    }

    public void onPageDisplayed() {
        this.onLoadVenders();
        System.out.println("Venders loaded");
    }

    public VenderListPage(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Vender List", SwingConstants.CENTER);
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
                if (value instanceof Vender) {
                    Vender vender = (Vender) value;
                    BaseUser baseUser = (BaseUser) authController.getBaseUserById(((Vender) value).getBaseUserId());
                    String showingText =
                            baseUser.getEmailAddress() + " / " +
                                    vender.getStoreName() + " " +
                                    vender.getStoreDescription() + " / " +
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
        JButton registerButton = new JButton("Register New Vender");
        registerButton.addActionListener(e -> mainFrame.switchTo("VenderRegisterPage"));

        JButton editButton = new JButton("Edit Selected Vender");
        editButton.addActionListener(e -> {
            Vender selected = list.getSelectedValue();
            if (selected != null) {
                VenderEditPage editPage = new VenderEditPage(mainFrame, selected);
                mainFrame.addPanel("VenderEditPage", editPage);
                mainFrame.switchTo("VenderEditPage");
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Select a vender to edit.");
            }
        });

        JButton deleteButton = new JButton("Delete Selected Vender");
        deleteButton.addActionListener(e -> {
            Vender selected = list.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(mainFrame, "Are you sure you want to delete this vender?");
                if (confirm == JOptionPane.YES_OPTION) {
                    if (authController.deleteBaseUser(selected.getBaseUserId())) {
                        JOptionPane.showMessageDialog(mainFrame, "Vender deleted.");
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
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}
