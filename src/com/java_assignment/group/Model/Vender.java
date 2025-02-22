package com.java_assignment.group.Model;

import com.java_assignment.group.Controller.MenuController;
import com.java_assignment.group.Controller.OrderController;
import com.java_assignment.group.Controller.ReviewController;

import java.io.IOException;
import java.util.List;

/**
 * Vender model class.
 * Data file: vender.txt
 * Fields:
 *   - vender_id
 *   - base_user_id
 *   - store_name
 *   - store_backgroud_image
 *   - store_icon_image
 *   - store_description
 *   - created_at
 */
public class Vender implements BaseModel {
    private String baseUserId;
    private String storeName;
    private String storeBackgroundImage;
    private String storeIconImage;
    private String storeDescription;
    private String createdAt;
    private MenuController menuController;
    private ReviewController reviewController;

    private List<Menu> menuItems;
    private List<Review> reviews;
    private double averageRating;

    public Vender() {}

    public Vender(String baseUserId, String storeName, String storeBackgroundImage,
                  String storeIconImage, String storeDescription, String createdAt) {
        this.baseUserId = baseUserId;
        this.storeName = storeName;
        this.storeBackgroundImage = storeBackgroundImage;
        this.storeIconImage = storeIconImage;
        this.storeDescription = storeDescription;
        this.createdAt = createdAt;
        try{
            this.menuController = new MenuController();
            this.reviewController = new ReviewController();

            this.reviews = reviewController.getAllReviewsToUser(this.baseUserId);
            this.averageRating = reviewController.getAverageRatingByBaseUserId(this.baseUserId);
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

    public List<Review> getReviews(){
        return this.reviews;
    }

    public double getAverageRating(){
        return this.averageRating;
    }

    public List<Menu> getItems() {
        try{
            return menuController.getMenusByVender(this.baseUserId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getBaseUserId() {
        return baseUserId;
    }

    public void setBaseUserId(String baseUserId) {
        this.baseUserId = baseUserId;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreBackgroundImage() {
        return storeBackgroundImage;
    }

    public void setStoreBackgroundImage(String storeBackgroundImage) {
        this.storeBackgroundImage = storeBackgroundImage;
    }

    public String getStoreIconImage() {
        return storeIconImage;
    }

    public void setStoreIconImage(String storeIconImage) {
        this.storeIconImage = storeIconImage;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Serializes the Vender instance to a CSV formatted string.
     *
     * @return CSV formatted string.
     */
    public String toCsv() {
        return String.join(",",
                baseUserId,
                storeName,
                storeBackgroundImage,
                storeIconImage,
                storeDescription,
                createdAt
        );
    }

    /**
     * Creates a Vender instance from a CSV formatted string.
     *
     * @param csvLine CSV formatted string.
     * @return a new Vender instance.
     */
    public static Vender fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6) {
            throw new IllegalArgumentException("Invalid CSV line for Vender: " + csvLine);
        }
        return new Vender(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
    }

    @Override
    public String toString() {
        return "Vender{" +
                ", baseUserId='" + baseUserId + '\'' +
                ", storeName='" + storeName + '\'' +
                ", storeBackgroundImage='" + storeBackgroundImage + '\'' +
                ", storeIconImage='" + storeIconImage + '\'' +
                ", storeDescription='" + storeDescription + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
