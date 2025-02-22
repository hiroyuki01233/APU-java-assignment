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
            this.venderController = new VenderController();
            this.authController = new AuthController();
            this.venders = this.venderController.getAllVenders();
            this.listModel.clear();
            for (Vender vender : this.venders) {
                this.listModel.addElement(vender);
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
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(100, 149, 237));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JLabel titleLabel = new JLabel("Vender List", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

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
                    
                    // Add padding to the component
                    setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
                    
                    // Set alternating background colors
                    if (!isSelected) {
                        if (index % 2 == 0) {
                            setBackground(Color.WHITE);
                        } else {
                            setBackground(new Color(245, 245, 245));
                        }
                    }
                }
                return c;
            }
        });

        list.setBackground(Color.WHITE);
        list.setSelectionBackground(new Color(100, 149, 237));
        list.setSelectionForeground(Color.WHITE);
        list.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 50, 0));

        // Create styled buttons
        JButton registerButton = createStyledButton("Register New Vender", new Color(100, 149, 237));
        registerButton.addActionListener(e -> mainFrame.switchTo("VenderRegisterPage"));

        JButton editButton = createStyledButton("Edit Selected Vender", new Color(100, 149, 237));
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

        JButton deleteButton = createStyledButton("Delete Selected Vender", new Color(100, 149, 237));
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

        JButton backButton = createStyledButton("Back", new Color(100, 149, 237));
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        buttonPanel.add(registerButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color background) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(180, 40));
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
}
