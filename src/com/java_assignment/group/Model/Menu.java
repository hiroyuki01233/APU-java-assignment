package com.java_assignment.group.Model;

public class Menu implements BaseModel {
    private String menuId;
    private String venderId;
    private String name;
    private String description;
    private Double price;

    public Menu() {}

    public Menu(String menuId, String venderId, String name, String description, Double price) {
        this.menuId = menuId;
        this.venderId = venderId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    @Override
    public String getId() {
        return menuId;
    }

    @Override
    public void setId(String id) {
        this.menuId = id;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    /**
     * CSV形式にシリアライズ
     */
    public String toCsv() {
        return String.join(",",
                menuId,
                venderId,
                name,
                description,
                price.toString()
        );
    }

    /**
     * CSV文字列からMenuインスタンスを生成
     */
    public static Menu fromCsv(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if(parts.length < 5) {
            throw new IllegalArgumentException("Invalid CSV line for Menu: " + csvLine);
        }
        return new Menu(parts[0], parts[1], parts[2], parts[3], Double.parseDouble(parts[4]));
    }

    @Override
    public String toString() {
        return "Menu{" +
                "menuId='" + menuId + '\'' +
                ", venderId='" + venderId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
