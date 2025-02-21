package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.WalletController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Wallet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WalletManagePage extends JPanel {

    private MainFrame mainFrame;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JButton addBalanceButton;
    private JButton seeTransactionButton;
    private JButton backButton;

    private AuthController authController;
    private WalletController walletController;
    private List<BaseUser> baseUsers;

    public WalletManagePage(MainFrame mainFrame) {
        this.mainFrame = mainFrame;

        // Initialize controllers and load users
        try {
            authController = new AuthController();
            walletController = new WalletController();
            baseUsers = authController.getAllBaseusers();
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error initializing controllers", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Wallet Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        JLabel subtitleLabel = new JLabel("Manage user wallets and transactions", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.WHITE);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setBackground(new Color(52, 152, 219));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        headerPanel.add(titlePanel, BorderLayout.CENTER);

        // Set up table columns and styling
        String[] columns = {"Balance", "Role", "Email", "BaseUserId", "isDeleted"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        userTable.setRowHeight(40);
        userTable.setFont(new Font("Arial", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        userTable.getTableHeader().setBackground(new Color(52, 152, 219));
        userTable.getTableHeader().setForeground(Color.WHITE);
        userTable.setSelectionBackground(new Color(52, 152, 219));
        userTable.setSelectionForeground(Color.WHITE);
        userTable.setShowGrid(true);
        userTable.setGridColor(Color.LIGHT_GRAY);
        userTable.setIntercellSpacing(new Dimension(10, 10));
        
        // Add alternating row colors
        userTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(240, 240, 240));
                }
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        });

        populateTable();

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Create and style bottom buttons
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        addBalanceButton = createStyledButton("Add Balance", new Color(52, 152, 219));
        seeTransactionButton = createStyledButton("See Transaction", new Color(52, 152, 219));
        backButton = createStyledButton("Back", Color.RED);

        addBalanceButton.addActionListener(e -> openAddBalanceDialog());
        seeTransactionButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(WalletManagePage.this, "See transaction functionality is not implemented yet.");
        });
        backButton.addActionListener(e -> mainFrame.switchTo("AdminDashboard"));

        bottomPanel.add(addBalanceButton);
        bottomPanel.add(seeTransactionButton);
        bottomPanel.add(backButton);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });

        return button;
    }

    /**
     * Populate the table with the BaseUser list and their wallet balances.
     */
    private void populateTable() {
        tableModel.setRowCount(0);
        if (baseUsers == null) return;
        
        for (BaseUser user : baseUsers) {
            if (user == null) continue;
            
            double balance = 0.0;
            Wallet wallet = walletController.getWalletByBaseUserId(user.getId());
            if (wallet != null) {
                balance = wallet.getBalance();
            }
            Object[] row = { balance, user.getUserType(), user.getEmailAddress(), user.getId(), user.getIsDeleted() };
            tableModel.addRow(row);
        }
    }

    private void openAddBalanceDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table.", "No user selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Retrieve user info from the selected row
            Object balanceObj = tableModel.getValueAt(selectedRow, 0);
            String baseUserId = (String) tableModel.getValueAt(selectedRow, 3);
            double currentBalance = balanceObj instanceof Double ? (Double) balanceObj : Double.parseDouble(balanceObj.toString());

            // Create the dialog
            JDialog dialog = new JDialog(mainFrame, "Add Balance", true);
            dialog.setSize(300, 250);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Current balance label
            JLabel currentBalanceLabel = new JLabel(String.format("Current Balance: %.2f", currentBalance));
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            dialog.add(currentBalanceLabel, gbc);

            // Label for add amount input
            JLabel addAmountLabel = new JLabel("Add Amount:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 1;
            dialog.add(addAmountLabel, gbc);

            // Text field for the amount to add
            JTextField amountField = new JTextField();
            gbc.gridx = 1;
            gbc.gridy = 1;
            dialog.add(amountField, gbc);

            // Label to display the new balance in real time
            JLabel newBalanceLabel = new JLabel(String.format("New Balance: %.2f", currentBalance));
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            dialog.add(newBalanceLabel, gbc);

            // Add button to confirm the update
            JButton addButton = new JButton("Add");
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            dialog.add(addButton, gbc);

            // Update new balance in real time as the user types
            amountField.getDocument().addDocumentListener(new DocumentListener() {
                private void updateNewBalance() {
                    try {
                        String text = amountField.getText().trim();
                        double addAmount = text.isEmpty() ? 0 : Double.parseDouble(text);
                        double newBalance = currentBalance + addAmount;
                        newBalanceLabel.setText(String.format("New Balance: %.2f", newBalance));
                    } catch (NumberFormatException ex) {
                        newBalanceLabel.setText(String.format("New Balance: %.2f", currentBalance));
                    }
                }
                @Override
                public void insertUpdate(DocumentEvent e) { updateNewBalance(); }
                @Override
                public void removeUpdate(DocumentEvent e) { updateNewBalance(); }
                @Override
                public void changedUpdate(DocumentEvent e) { updateNewBalance(); }
            });

            // Action listener for the Add button
            addButton.addActionListener(e -> {
                try {
                    String text = amountField.getText().trim();
                    if (text.isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "Please enter an amount.");
                        return;
                    }
                    double addAmount = Double.parseDouble(text);
                    if (addAmount <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Please enter a positive amount.");
                        return;
                    }

                    // Update the wallet balance
                    boolean success = walletController.increaseBalance(baseUserId, addAmount);
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Balance updated successfully!");
                        dialog.dispose();
                        // Update the table row
                        tableModel.setValueAt(currentBalance + addAmount, selectedRow, 0);
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to update balance.");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid number format.");
                }
            });

            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error processing balance update: " + ex.getMessage());
        }
    }
}
