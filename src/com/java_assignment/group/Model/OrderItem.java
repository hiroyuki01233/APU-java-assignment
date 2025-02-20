package com.java_assignment.group.Model;

import com.java_assignment.group.Controller.MenuController;

import java.io.IOException;

public class OrderItem implements BaseModel {
    private String orderItemId;
    private String orderId;
    private String menuId;
    private Integer amount;
    private String createdAt;
    private Double eachPrice; // 追加
    private MenuController menuController;

    private Menu menu;

    public OrderItem() {}

    public OrderItem(String orderItemId, String orderId, String menuId, Integer amount, String createdAt, Double eachPrice) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.menuId = menuId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.eachPrice = eachPrice;

        try {
            menuController = new MenuController();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return orderItemId;
    }

    @Override
    public void setId(String id) {
        this.orderItemId = id;
    }

    public Menu getMenu() {
        return menuController.getMenuById(this.menuId);
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public Double getEachPrice() {
        return eachPrice;
    }

    public void setEachPrice(Double eachPrice) {
        this.eachPrice = eachPrice;
    }

    public String toCsv() {
        return String.join(",",
                orderItemId,
                orderId,
                menuId,
                amount.toString(),
                createdAt,
                eachPrice.toString() // 追加
        );
    }

    public static OrderItem fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 6) { // 変更: 期待するフィールド数を6に
            throw new IllegalArgumentException("Invalid CSV line for OrderItem: " + csvLine);
        }
        return new OrderItem(parts[0], parts[1], parts[2], Integer.parseInt(parts[3]), parts[4], Double.parseDouble(parts[5])); // 変更: eachPrice を追加
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId='" + orderItemId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", menuId='" + menuId + '\'' +
                ", amount=" + amount +
                ", createdAt='" + createdAt + '\'' +
                ", eachPrice=" + eachPrice + // 追加
                '}';
    }
}
