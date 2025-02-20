package com.java_assignment.group.Model;

/**
 * User model class.
 * Data file: customer.txt
 * Fields:
 *   - user_id
 *   - base_user_id
 *   - icon_image
 *   - first_name
 *   - last_name
 *   - address
 *   - created_at
 */
public class Customer implements BaseModel {
    private String customerId;
    private String baseUserId;
    private String iconImage;
    private String firstName;
    private String lastName;
    private String address;
    private String createdAt;

    public Customer() {}

    public Customer(String customerId, String baseUserId, String iconImage, String firstName, String lastName, String address, String createdAt) {
        this.customerId = customerId;
        this.baseUserId = baseUserId;
        this.iconImage = iconImage;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return customerId;
    }

    @Override
    public void setId(String id) {
        this.customerId = id;
    }

    public String getBaseUserid() {
        return baseUserId;
    }

    public void setBaseUserId(String basecustomerId) {
        this.baseUserId = basecustomerId;
    }

    public String getIconImage() {
        return iconImage;
    }

    public void setIconImage(String iconImage) {
        this.iconImage = iconImage;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Serializes the User instance to a CSV formatted string.
     *
     * @return a CSV formatted string representing the User.
     */
    public String toCsv() {
        return String.join(",",
                customerId,
                baseUserId,
                iconImage,
                firstName,
                lastName,
                address,
                createdAt
        );
    }

    /**
     * Creates a User instance from a CSV formatted string.
     *
     * @param csvLine a CSV formatted string.
     * @return a new User instance.
     */
    public static Customer fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 7) {
            throw new IllegalArgumentException("Invalid CSV line for Customer: " + csvLine);
        }
        return new Customer(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", basecustomerId='" + baseUserId + '\'' +
                ", iconImage='" + iconImage + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", address='" + address + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
