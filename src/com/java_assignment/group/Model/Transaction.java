package com.java_assignment.group.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction implements BaseModel {
    private String transactionId;
    private String sourceWalletId;
    private String destinationWalletId;
    private double amount;
    private String type;
    private boolean isSuccess;
    private String relatedOrderId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private LocalDateTime failedAt;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Transaction() {}

    public Transaction(String transactionId, String sourceWalletId, String destinationWalletId,
                       double amount, String type, boolean isSuccess, String relatedOrderId,
                       LocalDateTime createdAt, LocalDateTime completedAt, LocalDateTime failedAt) {
        this.transactionId = transactionId;
        this.sourceWalletId = sourceWalletId;
        this.destinationWalletId = destinationWalletId;
        this.amount = amount;
        this.type = type;
        this.isSuccess = isSuccess;
        this.relatedOrderId = relatedOrderId;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.failedAt = failedAt;
    }

    @Override
    public String getId() {
        return transactionId;
    }

    @Override
    public void setId(String id) {
        this.transactionId = id;
    }

    public String getSourceWalletId() { return sourceWalletId; }
    public void setSourceWalletId(String sourceWalletId) { this.sourceWalletId = sourceWalletId; }
    public String getDestinationWalletId() { return destinationWalletId; }
    public void setDestinationWalletId(String destinationWalletId) { this.destinationWalletId = destinationWalletId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public boolean isSuccess() { return isSuccess; }
    public void setSuccess(boolean success) { isSuccess = success; }
    public String getRelatedOrderId() { return relatedOrderId; }
    public void setRelatedOrderId(String relatedOrderId) { this.relatedOrderId = relatedOrderId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public LocalDateTime getFailedAt() { return failedAt; }
    public void setFailedAt(LocalDateTime failedAt) { this.failedAt = failedAt; }

    /**
     * CSV形式にシリアライズ
     */
    public String toCsv() {
        return String.join(",",
                transactionId,
                sourceWalletId,
                destinationWalletId,
                String.valueOf(amount),
                type,
                String.valueOf(isSuccess),
                relatedOrderId != null ? relatedOrderId : "",
                createdAt != null ? createdAt.format(formatter) : "",
                completedAt != null ? completedAt.format(formatter) : "",
                failedAt != null ? failedAt.format(formatter) : ""
        );
    }

    /**
     * CSV文字列からTransactionインスタンスを生成
     */
    public static Transaction fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 10) {
            throw new IllegalArgumentException("Invalid CSV line for Transaction: " + csvLine);
        }

        return new Transaction(
                parts[0],   // transactionId
                parts[1],   // sourceWalletId
                parts[2],   // destinationWalletId
                Double.parseDouble(parts[3]), // amount
                parts[4],   // type
                Boolean.parseBoolean(parts[5]), // isSuccess
                parts[6].isEmpty() ? null : parts[6], // relatedOrderId
                parts[7].isEmpty() ? null : LocalDateTime.parse(parts[7], formatter), // createdAt
                parts[8].isEmpty() ? null : LocalDateTime.parse(parts[8], formatter), // completedAt
                parts[9].isEmpty() ? null : LocalDateTime.parse(parts[9], formatter)  // failedAt
        );
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId='" + transactionId + '\'' +
                ", sourceWalletId='" + sourceWalletId + '\'' +
                ", destinationWalletId='" + destinationWalletId + '\'' +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", isSuccess=" + isSuccess +
                ", relatedOrderId='" + relatedOrderId + '\'' +
                ", createdAt=" + createdAt +
                ", completedAt=" + completedAt +
                ", failedAt=" + failedAt +
                '}';
    }
}
