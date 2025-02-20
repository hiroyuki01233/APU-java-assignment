package com.java_assignment.group.Model;

import com.java_assignment.group.Controller.CartController;
import com.java_assignment.group.Controller.OrderController;

import java.io.IOException;
import java.util.List;

public class Cart implements BaseModel {
    private String cartId;
    private String userId;
    private String venderId;
    private String createdAt;

    private CartController cartController;
    private List<CartItem> cartItem;

    public Cart() {}

    public Cart(String cartId, String userId, String venderId, String createdAt) {
        this.cartId = cartId;
        this.userId = userId;
        this.venderId = venderId;
        this.createdAt = createdAt;

        try{
            this.cartController = new CartController();
            this.cartItem = this.cartController.getCartItemsByCartId(this.cartId);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return cartId;
    }

    @Override
    public void setId(String id) {
        this.cartId = id;
    }

    public List<CartItem> getCartItems(){
        return cartItem;
    }

    public Integer getAllAmountOfItems(){
        Integer amount = 0;
        for (CartItem item : cartItem){
            amount += item.getAmount();
        }

        return amount;
    }

    public CartItem getCartItemByMenuId(String menuId){
        for (CartItem item: cartItem){
            if (item.getMenuId().equals(menuId)){
                return item;
            }
        }

        return null;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String toCsv() {
        return String.join(",",
                cartId,
                userId,
                venderId,
                createdAt
        );
    }

    public static Cart fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV line for Cart: " + csvLine);
        }
        return new Cart(parts[0], parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId='" + cartId + '\'' +
                ", userId='" + userId + '\'' +
                ", venderId='" + venderId + '\'' +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
