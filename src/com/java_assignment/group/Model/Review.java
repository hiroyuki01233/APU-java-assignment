package com.java_assignment.group.Model;

/**
 * Review model class.
 * Data file: review.txt
 * Fields:
 *   - review_id
 *   - user_id
 *   - target_user_id
 *   - rating
 *   - text
 *   - created_at
 */
public class Review implements BaseModel {
    private String reviewId;
    private String userId;
    private String targetUserId;
    private int rating;
    private String text;
    private String createdAt;

    public Review() {}

    public Review(String reviewId, String userId, String targetUserId, int rating, String text, String createdAt) {
        this.reviewId = reviewId;
        this.userId = userId;
        this.targetUserId = targetUserId;
        this.rating = rating;
        this.text = text;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return reviewId;
    }

    @Override
    public void setId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Serializes the Review instance to a CSV formatted string.
     *
     * @return a CSV formatted string representing the Review.
     */
    public String toCsv() {
        return String.join(",",
                reviewId,
                userId,
                targetUserId,
                String.valueOf(rating),
                text,
                createdAt
        );
    }

    /**
     * Creates a Review instance from a CSV formatted string.
     *
     * @param csvLine a CSV formatted string.
     * @return a new Review instance.
     */
    public static Review fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV line for Review: " + csvLine);
        }
        return new Review(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], parts[5]);
    }

    @Override
    public String toString() {
        return "Review{" +
                "reviewId='" + reviewId + '\'' +
                ", userId='" + userId + '\'' +
                ", targetUserId='" + targetUserId + '\'' +
                ", rating=" + rating +
                ", text='" + text + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
