package com.java_assignment.group.Controller;

import com.java_assignment.group.Model.Wallet;
import com.java_assignment.group.Model.Transaction;
import com.java_assignment.group.Model.TxtModelRepository;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    public List<Transaction> getALlTransactionByBaseUserId(String baseUserId) {
        try {
            List<Transaction> result = new ArrayList<>();
            List<Transaction> transactions = transactionRepository.readAll();
            for (Transaction item : transactions) {
                if (item.getDestinationUser().getId().equals(baseUserId)) {
                    result.add(item);
                }
            }
            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public boolean transferFunds(String sourceUserId, String destinationUserId, double amount, String type, String relatedOrderId, String description) {
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
                    null,
                    description
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

                    // 取引を記録
                    String transactionId = UUID.randomUUID().toString();
                    Transaction transaction = new Transaction(
                            transactionId,
                            null,
                            wallet.getId(),
                            amount,
                            "Top-up",
                            true, // 成功
                            null,
                            LocalDateTime.now(),
                            LocalDateTime.now(),
                            null,
                            "Top up new credit"
                    );

                    List<Transaction> transactions = transactionRepository.readAll();
                    transactions.add(transaction);
                    transactionRepository.writeAll(transactions, false);

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

    // ------------------------------------------------------------
    // 新規追加メソッド
    // ------------------------------------------------------------

    /**
     * (1) 指定の期間内かつ指定ユーザー（destinationUser.userId）の取引をすべて返す
     *
     * @param userId    取得対象のユーザーID
     * @param startDate 期間の開始日時（含む）
     * @param endDate   期間の終了日時（含む）
     * @return 指定条件に合致するTransactionリスト
     */
    public List<Transaction> getTransactionsByDestinationUserAndDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        try {
            List<Transaction> transactions = transactionRepository.readAll();
            List<Transaction> result = new ArrayList<>();
            for (Transaction transaction : transactions) {
                if ((transaction.getDestinationUser() != null && transaction.getDestinationUser().getId().equals(userId)||
                        transaction.getSourceUser() != null && transaction.getSourceUser().getId().equals(userId))) {
                    LocalDateTime createdAt = transaction.getCreatedAt();
                    if ((createdAt.isEqual(startDate) || createdAt.isAfter(startDate)) &&
                            (createdAt.isEqual(endDate)   || createdAt.isBefore(endDate))) {
                        result.add(transaction);
                    }
                }
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * (2) 指定ユーザーの対象月における収益を計算して返す
     * destinationUserの場合は加算、sourceUserの場合は減算する
     *
     * @param userId  対象ユーザーID
     * @param year    対象年
     * @param month   対象月（1～12）
     * @return 計算結果の収益（正の値ならプラス、負の値ならマイナス）
     */
    public double calculateMonthlyRevenue(String userId, int year, int month) {
        double revenue = 0.0;
        try {
            List<Transaction> transactions = transactionRepository.readAll();
            for (Transaction transaction : transactions) {
                LocalDateTime createdAt = transaction.getCreatedAt();
                if (createdAt.getYear() == year && createdAt.getMonthValue() == month) {
                    if (transaction.getDestinationUser() != null && transaction.getDestinationUser().getId().equals(userId)) {
                        revenue += transaction.getAmount();
                    }
                    if (transaction.getSourceUser() != null && transaction.getSourceUser().getId().equals(userId)) {
                        revenue -= transaction.getAmount();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    /**
     * (3) 指定ユーザーのウォレットの金額推移（日別残高）を返す
     * 現在のウォレット残高から、過去の各日のネット取引額（destinationなら＋、sourceなら－）を逆算して算出する
     *
     * @param userId 対象ユーザーID
     * @return 古い日付から今日までの日別残高リスト
     */
    public List<Double> getDailyWalletBalanceTrend(String userId) {
        try {
            Wallet wallet = getWalletByBaseUserId(userId);
            if (wallet == null) {
                System.out.println("Wallet not found for user: " + userId);
                return new ArrayList<>();
            }

            // ユーザーが関与しているすべての取引を取得（source, destinationの両方）
            List<Transaction> allTransactions = transactionRepository.readAll();
            List<Transaction> userTransactions = new ArrayList<>();
            for (Transaction transaction : allTransactions) {
                if ((transaction.getSourceUser() != null && transaction.getSourceUser().getId().equals(userId)) ||
                        (transaction.getDestinationUser() != null && transaction.getDestinationUser().getId().equals(userId))) {
                    userTransactions.add(transaction);
                }
            }

            // 日付ごとのネット変動額を集計する
            // （destination: +amount, source: -amount）
            Map<LocalDate, Double> netEffectByDate = new HashMap<>();
            for (Transaction transaction : userTransactions) {
                LocalDate date = transaction.getCreatedAt().toLocalDate();
                double net = 0.0;
                if (transaction.getDestinationUser() != null && transaction.getDestinationUser().getId().equals(userId)) {
                    net += transaction.getAmount();
                }
                if (transaction.getSourceUser() != null && transaction.getSourceUser().getId().equals(userId)) {
                    net -= transaction.getAmount();
                }
                netEffectByDate.put(date, netEffectByDate.getOrDefault(date, 0.0) + net);
            }

            // 取引のあった最も古い日付を特定（取引がなければ本日を設定）
            LocalDate today = LocalDate.now();
            LocalDate earliestDate = today;
            for (LocalDate date : netEffectByDate.keySet()) {
                if (date.isBefore(earliestDate)) {
                    earliestDate = date;
                }
            }

            // 現在のウォレット残高から、各日の残高を逆算する
            // 当日の残高はwallet.getBalance()とし、当日の取引分を引くことで前日の残高を求める
            List<Double> reversedBalances = new ArrayList<>();
            double balance = wallet.getBalance();
            reversedBalances.add(balance); // 本日の残高
            LocalDate currentDate = today;

            while (currentDate.isAfter(earliestDate)) {
                // 当日(currentDate)の取引の合計を引く
                double netToday = netEffectByDate.getOrDefault(currentDate, 0.0);
                balance = balance - netToday;
                reversedBalances.add(balance);
                currentDate = currentDate.minusDays(1);
            }

            // reversedBalancesは本日から過去へ向かう順になっているので、昇順（最古→本日）に反転する
            Collections.reverse(reversedBalances);
            return reversedBalances;

        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
