package com.java_assignment.group.Model;

import com.java_assignment.group.Controller.OrderController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Order implements BaseModel {
    private String orderId;
    private String userId;
    private String venderId;
    private String deliveryRunnerId;
    private String orderType;
    private String address;
    private String currentStatus;
    private LocalDateTime createdAt;
    private List<OrderItem> orderItems;
    private Vender vender;

    // 金額関連の追加
    private double totalPrice;
    private double commission;
    private double tax;
    private double venderPayout;
    private double deliveryFee;

    public Order() {}


    // currenStatus: Ordered, Preparing,Preparing-runnerWaiting  Decline, ReadyToPickup, OnDelivery, Completed, ForceCancelled
    public Order(String orderId, String userId, String venderId, String deliveryRunnerId,
                 String orderType, String address, String currentStatus, LocalDateTime createdAt,
                 Double totalPrice, Double commission, Double tax, Double venderPayout, Double deliveryFee) {
        this.orderId = orderId;
        this.userId = userId;
        this.venderId = venderId;
        this.deliveryRunnerId = deliveryRunnerId;
        this.orderType = orderType;
        this.address = address;
        this.currentStatus = currentStatus;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.commission = commission;
        this.tax = tax;
        this.venderPayout = venderPayout;
        this.deliveryFee = deliveryFee;

        try {
            OrderController orderController = new OrderController();
            this.orderItems = orderController.getOrderItemsByOrder(this.orderId);
            TxtModelRepository<Vender> venderRepository = new TxtModelRepository<>("Data/vender.txt", Vender::fromCsv, Vender::toCsv);
            for (Vender venderItem: venderRepository.readAll()){
                if (venderItem.getId().equals(this.venderId)){
                    this.vender = venderItem;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getId() {
        return orderId;
    }

    @Override
    public void setId(String id) {
        this.orderId = id;
    }

    public List<OrderItem> getItems() {
        return this.orderItems;
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

    public String getDeliveryRunnerId() {
        return deliveryRunnerId;
    }

    public void setDeliveryRunnerId(String deliveryRunnerId) {
        this.deliveryRunnerId = deliveryRunnerId;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public double getCommission() {
        return commission;
    }

    public double getTax() {
        return tax;
    }

    public double getVenderPayout() {
        return venderPayout;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public double getTotalPriceAllIncludes(){
        return totalPrice+commission+tax+venderPayout+deliveryFee;
    }

    public Vender getVender(){
        return this.vender;
    }

    /**
     * CSV形式での出力
     */
    public String toCsv() {
        return String.join(",",
                orderId,
                userId,
                venderId,
                deliveryRunnerId,
                orderType,
                address,
                currentStatus,
                createdAt.toString(),
                String.valueOf(totalPrice),
                String.valueOf(commission),
                String.valueOf(tax),
                String.valueOf(venderPayout),
                String.valueOf(deliveryFee)
        );
    }

    /**
     * CSVから `Order` を復元
     */
    public static Order fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 13) {
            throw new IllegalArgumentException("Invalid CSV line for Order: " + csvLine);
        }
        return new Order(parts[0], parts[1], parts[2], parts[3],
                parts[4], parts[5], parts[6], LocalDateTime.parse(parts[7], DateTimeFormatter.ISO_LOCAL_DATE_TIME), Double.parseDouble(parts[8]),
                Double.parseDouble(parts[9]), Double.parseDouble(parts[10]), Double.parseDouble(parts[11]), Double.parseDouble(parts[12]));
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", venderId='" + venderId + '\'' +
                ", deliveryRunnerId='" + deliveryRunnerId + '\'' +
                ", orderType='" + orderType + '\'' +
                ", address='" + address + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", totalPrice=" + totalPrice +
                ", commission=" + commission +
                ", tax=" + tax +
                ", venderPayout=" + venderPayout +
                ", deliveryFee=" + deliveryFee +
                '}';
    }
}
