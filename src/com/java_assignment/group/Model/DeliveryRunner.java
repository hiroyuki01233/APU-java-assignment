package com.java_assignment.group.Model;

/**
 * DeliveryRunner model class.
 * Data file: delivery_runner.txt
 * Fields:
 *   - delivery_runner_id
 *   - base_user_id
 *   - first_name
 *   - last_name
 *   - created_at
 */
public class DeliveryRunner implements BaseModel {
    private String baseUserId;
    private String firstName;
    private String lastName;
    private String createdAt;

    public DeliveryRunner() {}

    public DeliveryRunner(String baseUserId, String firstName, String lastName, String createdAt) {
        this.baseUserId = baseUserId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return baseUserId;
    }

    @Override
    public void setId(String id) {
        this.baseUserId = id;
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
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
     * Serializes the DeliveryRunner instance to a CSV formatted string.
     *
     * @return CSV formatted string.
     */
    public String toCsv() {
        return String.join(",",
                baseUserId,
                firstName,
                lastName,
                createdAt
        );
    }

    /**
     * Creates a DeliveryRunner instance from a CSV formatted string.
     *
     * @param csvLine CSV formatted string.
     * @return a new DeliveryRunner instance.
     */
    public static DeliveryRunner fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line for DeliveryRunner: " + csvLine);
        }
        return new DeliveryRunner(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return "DeliveryRunner{" +
                ", baseUserId='" + baseUserId + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
