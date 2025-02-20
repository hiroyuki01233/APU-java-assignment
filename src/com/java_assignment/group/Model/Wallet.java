package com.java_assignment.group.Model;

public class Wallet implements BaseModel {
    private String walletId;
    private String baseUserId;
    private Double balance;
    private String createdAt;

    public Wallet() {}

    public Wallet(String walletId, String baseUserId, Double balance, String createdAt) {
        this.walletId = walletId;
        this.baseUserId = baseUserId;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return walletId;
    }

    @Override
    public void setId(String id) {
        this.walletId = id;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toCsv() {
        return String.join(",",
                walletId,
                baseUserId,
                balance.toString(),
                createdAt
        );
    }

    public static Wallet fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line for Wallet: " + csvLine);
        }
        return new Wallet(parts[0], parts[1], Double.parseDouble(parts[2]), parts[3]);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "walletId='" + walletId + '\'' +
                ", baseUserId='" + baseUserId + '\'' +
                ", balance='" + balance + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
