package com.java_assignment.group.Model;

public class Payment implements BaseModel {
    private String paymentId;
    private String baseUserId;
    private String fullName;
    private String cardNumber;
    private String expiredDate;
    private String pinCode;
    private String createdAt;

    public Payment() {}

    public Payment(String paymentId, String baseUserId, String fullName, String cardNumber, String expiredDate, String pinCode, String createdAt) {
        this.paymentId = paymentId;
        this.baseUserId = baseUserId;
        this.fullName = fullName;
        this.cardNumber = cardNumber;
        this.expiredDate = expiredDate;
        this.pinCode = pinCode;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public void setId(String id) {
        this.paymentId = id;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getPinCode() {
        return pinCode;
    }

    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toCsv() {
        return String.join(",",
                paymentId,
                baseUserId,
                fullName,
                cardNumber,
                expiredDate,
                pinCode,
                createdAt
        );
    }

    public static Payment fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 7) {
            throw new IllegalArgumentException("Invalid CSV line for Payment: " + csvLine);
        }
        return new Payment(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId='" + paymentId + '\'' +
                ", baseUserId='" + baseUserId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", cardNumber='" + cardNumber + '\'' +
                ", expiredDate='" + expiredDate + '\'' +
                ", pinCode='" + pinCode + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
