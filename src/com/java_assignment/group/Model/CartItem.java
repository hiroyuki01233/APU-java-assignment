package com.java_assignment.group.Model;

import com.java_assignment.group.Controller.MenuController;

import java.io.IOException;

public class CartItem implements BaseModel {
    private String cartItemId;
    private String userId;
    private String cartId;
    private String menuId;
    private Integer amount;
    private String createdAt;

    private MenuController menuController;
    private Menu menu;

    public CartItem() {}

    public CartItem(String cartItemId, String userId, String cartId, String menuId, Integer amount, String createdAt) {
        this.cartItemId = cartItemId;
        this.userId = userId;
        this.cartId = cartId;
        this.menuId = menuId;
        this.amount = amount;
        this.createdAt = createdAt;

        try{
            this.menuController = new MenuController();
            this.menu = menuController.getMenuById(this.menuId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return cartItemId;
    }

    @Override
    public void setId(String id) {
        this.cartItemId = id;
    }

    public Menu getMenu(){
        return this.menu;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Double getEachPrice(){
        return this.menu.getPrice();
    }

    public String toCsv() {
        return String.join(",",
                cartItemId,
                userId,
                cartId,
                menuId,
                amount.toString(),
                createdAt
        );
    }

    public static CartItem fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 5) {
            throw new IllegalArgumentException("Invalid CSV line for CartItem: " + csvLine);
        }
        return new CartItem(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5]);
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId='" + cartItemId + '\'' +
                ", userId='" + userId + '\'' +
                ", cartId='" + cartId + '\'' +
                ", menuId='" + menuId + '\'' +
                ", amount='" + amount + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
