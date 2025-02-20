package com.java_assignment.group.Model;

/**
 * Admin model class.
 * Data file: admin.txt
 * Fields:
 *   - admin_id
 *   - first_name
 *   - last_name
 *   - created_at
 */
public class Admin implements BaseModel {
    private String adminId;
    private String firstName;
    private String lastName;
    private String createdAt;

    public Admin() {}

    public Admin(String adminId, String firstName, String lastName, String createdAt) {
        this.adminId = adminId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return adminId;
    }

    @Override
    public void setId(String id) {
        this.adminId = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Serializes the Admin instance to a CSV formatted string.
     *
     * @return CSV formatted string.
     */
    public String toCsv() {
        return String.join(",", adminId, firstName, lastName, createdAt);
    }

    /**
     * Creates an Admin instance from a CSV formatted string.
     *
     * @param csvLine CSV formatted string.
     * @return a new Admin instance.
     */
    public static Admin fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line for Admin: " + csvLine);
        }
        return new Admin(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + adminId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
