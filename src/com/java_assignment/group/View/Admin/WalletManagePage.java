package com.java_assignment.group.View.Admin;

import com.java_assignment.group.Controller.AuthController;
import com.java_assignment.group.Controller.WalletController;
import com.java_assignment.group.MainFrame;
import com.java_assignment.group.Model.BaseUser;
import com.java_assignment.group.Model.Wallet;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
//        setVisible(true);

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

        // Set up table columns: Balance, Role, Email, BaseUserId, isDeleted
        String[] columns = {"Balance", "Role", "Email", "BaseUserId", "isDeleted"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // make table non-editable
            }
        };
        userTable = new JTable(tableModel);
        populateTable();

        JScrollPane scrollPane = new JScrollPane(userTable);

        // Create bottom buttons
        addBalanceButton = new JButton("Add balance");
        seeTransactionButton = new JButton("See transaction");
        backButton = new JButton("Back");

        addBalanceButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                openAddBalanceDialog();
            }
        });

        seeTransactionButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                // For demonstration – implement transaction viewing as needed
                JOptionPane.showMessageDialog(WalletManagePage.this, "See transaction functionality is not implemented yet.");
            }
        });

        backButton.addActionListener(e -> {mainFrame.switchTo("AdminDashboard");});

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(addBalanceButton);
        bottomPanel.add(seeTransactionButton);
        bottomPanel.add(backButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Populate the table with the BaseUser list and their wallet balances.
     */
    private void populateTable() {
        tableModel.setRowCount(0);
        for (BaseUser user : baseUsers) {
            double balance = 0.0;
            Wallet wallet = walletController.getWalletByBaseUserId(user.getId());
            if (wallet != null) {
                balance = wallet.getBalance();
            }
            Object[] row = { balance, user.getUserType(), user.getEmailAddress(), user.getId(), user.getIsDeleted() };
            tableModel.addRow(row);
        }
    }

    /**
     * Opens a popup dialog that allows adding balance to the selected user's wallet.
     */
    private void openAddBalanceDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user from the table.", "No user selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Retrieve user info from the selected row
        System.out.println(tableModel.getValueAt(selectedRow, 0));
        System.out.println("current balance");
        String baseUserId = (String) tableModel.getValueAt(selectedRow, 3);
        double currentBalance = (double) tableModel.getValueAt(selectedRow, 0);

        // Create the dialog
        JDialog dialog = new JDialog(mainFrame, "Add Balance", true);
        dialog.setSize(300, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Current balance label
        JLabel currentBalanceLabel = new JLabel("Current Balance: " + currentBalance);
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
        JLabel newBalanceLabel = new JLabel("New Balance: " + currentBalance);
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
                String text = amountField.getText().trim();
                double addAmount = 0;
                if (!text.isEmpty()) {
                    try {
                        addAmount = Double.parseDouble(text);
                    } catch (NumberFormatException ex) {
                        // Ignore non-numeric input for real-time calculation
                    }
                }
                double newBalance = currentBalance + addAmount;
                newBalanceLabel.setText("New Balance: " + newBalance);
            }
            @Override
            public void insertUpdate(DocumentEvent e) { updateNewBalance(); }
            @Override
            public void removeUpdate(DocumentEvent e) { updateNewBalance(); }
            @Override
            public void changedUpdate(DocumentEvent e) { updateNewBalance(); }
        });

        // Action listener for the Add button
        addButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = amountField.getText().trim();
                if (text.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter an amount.");
                    return;
                }
                double addAmount;
                try {
                    addAmount = Double.parseDouble(text);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid number format.");
                    return;
                }

                // Update the wallet balance
                boolean success = walletController.increaseBalance(baseUserId, addAmount);
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Balance updated successfully!");
                    dialog.dispose();
                    // Update the table row (you might also reload all data if necessary)
                    tableModel.setValueAt(currentBalance + addAmount, selectedRow, 0);
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update balance.");
                }
            }
        });

        dialog.setVisible(true);
    }
}
