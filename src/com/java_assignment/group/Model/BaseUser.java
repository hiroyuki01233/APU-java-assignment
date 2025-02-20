package com.java_assignment.group.Model;

import com.java_assignment.group.Controller.WalletController;

import java.io.IOException;

/**
 * BaseUser model
 * File: base_user.txt
 * Fields:
 *   - base_user_id
 *   - email address
 *   - password
 *   - user_type
 *   - created_at
 *   - isCurrentUser
 *   - deleted_at
 *   - is_deleted
 */
public class BaseUser implements BaseModel {
    private String baseUserId;
    private String emailAddress;
    private String password;
    private String userType;
    private String createdAt;
    private Boolean isCurrentUser;
    private String deletedAt;
    private Boolean isDeleted;

    private WalletController walletController;
    private Wallet wallet;

    public BaseUser() {}

    /**
     * Constructor with all fields.
     *
     * @param baseUserId     the unique ID of the base user
     * @param emailAddress   the email address
     * @param password       the password
     * @param userType       the type of user (e.g., customer, admin, etc.)
     * @param createdAt      the creation timestamp
     * @param isCurrentUser  flag indicating if this is the currently logged-in user
     * @param deletedAt      timestamp when the record was deleted (or empty/null if not deleted)
     * @param isDeleted      flag indicating if the user is deleted
     */
    public BaseUser(String baseUserId, String emailAddress, String password, String userType, String createdAt, Boolean isCurrentUser, String deletedAt, Boolean isDeleted) {
        this.baseUserId = baseUserId;
        this.emailAddress = emailAddress;
        this.password = password;
        this.userType = userType;
        this.createdAt = createdAt;
        this.isCurrentUser = isCurrentUser;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;

        try{
            this.walletController = new WalletController();
            this.wallet = this.walletController.getWalletByBaseUserId(this.baseUserId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return baseUserId;
    }

    @Override
    public void setId(String id) {
        this.baseUserId = id;
    }

    public Wallet getWallet(){
        return this.wallet;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean isCurrentUser() {
        return isCurrentUser;
    }
    public void setIsCurrentUser(Boolean isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }

    public String getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }
    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * Serializes the BaseUser instance to a CSV formatted line.
     *
     * @return a CSV formatted string representing this BaseUser.
     */
    public String toCsv() {
        return String.join(",",
                baseUserId,
                emailAddress,
                password,
                userType,
                createdAt,
                String.valueOf(isCurrentUser),
                (deletedAt == null ? "" : deletedAt),
                String.valueOf(isDeleted)
        );
    }

    /**
     * Parses a CSV formatted line and returns a BaseUser instance.
     *
     * @param csvLine CSV formatted string.
     * @return a new BaseUser instance.
     */
    public static BaseUser fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 8) {
            throw new IllegalArgumentException("Invalid CSV line for BaseUser: " + csvLine);
        }
        return new BaseUser(
                parts[0],                       // baseUserId
                parts[1],                       // emailAddress
                parts[2],                       // password
                parts[3],                       // userType
                parts[4],                       // createdAt
                Boolean.parseBoolean(parts[5]), // isCurrentUser
                parts[6].isEmpty() ? null : parts[6], // deletedAt (null if empty)
                Boolean.parseBoolean(parts[7])  // isDeleted
        );
    }

    @Override
    public String toString() {
        return "BaseUser{" +
                "baseUserId='" + baseUserId + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", password='" + password + '\'' +
                ", userType='" + userType + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", isCurrentUser=" + isCurrentUser +
                ", deletedAt='" + deletedAt + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }

    /**
     * Authenticates this user using the provided email and password.
     *
     * @param email    the email to check
     * @param password the password to check
     * @return true if both match this BaseUser's credentials; false otherwise.
     */
    public boolean authenticate(String email, String password) {
        return this.emailAddress.equals(email) && this.password.equals(password);
    }
}
