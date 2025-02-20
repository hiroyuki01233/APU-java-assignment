package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.Wallet;
import com.java_assignment.group.Model.Transaction;
import com.java_assignment.group.Model.TxtModelRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class WalletController {
    private TxtModelRepository<Wallet> walletRepository;
    private TxtModelRepository<Transaction> transactionRepository;

    public WalletController() throws IOException {
        walletRepository = new TxtModelRepository<>("src/Data/wallet.txt", Wallet::fromCsv, Wallet::toCsv);
        transactionRepository = new TxtModelRepository<>("src/Data/transaction.txt", Transaction::fromCsv, Transaction::toCsv);
    }

    /**
     * BaseUserIdをもとにWalletを取得する
     */
    public Wallet getWalletByBaseUserId(String baseUserId) {
        try {
            List<Wallet> wallets = walletRepository.readAll();
            for (Wallet wallet : wallets) {
                if (wallet.getBaseUserId().equals(baseUserId)) {
                    return wallet;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 指定のBaseUserIdのWalletのBalanceを増やす
     */
    public boolean increaseBalance(String baseUserId, double amount) {
        return updateBalance(baseUserId, amount, true);
    }

    /**
     * 指定のBaseUserIdのWalletのBalanceを減らす
     */
    public boolean decreaseBalance(String baseUserId, double amount) {
        return updateBalance(baseUserId, amount, false);
    }

    /**
     * 資金を別のユーザーに移動し、Transaction を保存する
     */
    public boolean transferFunds(String sourceUserId, String destinationUserId, double amount, String type, String relatedOrderId) {
        try {
            Wallet sourceWallet = getWalletByBaseUserId(sourceUserId);
            Wallet destinationWallet = getWalletByBaseUserId(destinationUserId);

            if (sourceWallet == null || destinationWallet == null) {
                System.out.println("Either source or destination wallet not found.");
                return false;
            }

            if (sourceWallet.getBalance() < amount) {
                System.out.println("Insufficient balance in source wallet.");
                return false;
            }

            // 残高を更新
            boolean debited = decreaseBalance(sourceUserId, amount);
            boolean credited = increaseBalance(destinationUserId, amount);

            if (!debited || !credited) {
                System.out.println("Transaction failed due to balance update error.");
                return false;
            }

            // 取引を記録
            String transactionId = UUID.randomUUID().toString();
            Transaction transaction = new Transaction(
                    transactionId,
                    sourceWallet.getId(),
                    destinationWallet.getId(),
                    amount,
                    type,
                    true, // 成功
                    relatedOrderId,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null // 失敗していないため null
            );

            List<Transaction> transactions = transactionRepository.readAll();
            transactions.add(transaction);
            transactionRepository.writeAll(transactions, false);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定のBaseUserIdのWalletのBalanceを増減する
     */
    private boolean updateBalance(String baseUserId, double amount, boolean isIncrease) {
        try {
            List<Wallet> wallets = walletRepository.readAll();
            boolean updated = false;
            for (int i = 0; i < wallets.size(); i++) {
                Wallet wallet = wallets.get(i);
                if (wallet.getBaseUserId().equals(baseUserId)) {
                    double currentBalance = wallet.getBalance();
                    double newBalance = isIncrease ? currentBalance + amount : currentBalance - amount;

                    if (newBalance < 0) {
                        System.out.println("Insufficient balance for transaction.");
                        return false;
                    }

                    wallet.setBalance(newBalance);
                    wallets.set(i, wallet);
                    updated = true;
                    break;
                }
            }
            if (updated) {
                walletRepository.writeAll(wallets, false);
            }
            return updated;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
