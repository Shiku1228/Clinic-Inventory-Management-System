package models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Items {

    private String itemID, itemName, category, unit, expiryDate, supplier, status, imagePath;
    private int stock;
    private String mongoId;
    private static final int LOW_STOCK_THRESHOLD = 10;

    public Items(String mongoId, String itemID, String itemName, String category, int stock, String unit, String expiryDate, String supplier, String status, String imagePath) {
        this.mongoId = mongoId;
        this.itemID = itemID;
        this.itemName = itemName;
        this.category = category;
        this.stock = stock;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.supplier = supplier;
        this.status = status;
        this.imagePath = imagePath;
    }

    public String getMongoId() {
        return mongoId;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public String getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public String getUnit() {
        return unit;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getStatus() {
        return status;
    }

    public String getImagePath() {
        return imagePath;
    }

    //setters
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    //check if item is low stock na
    public boolean isExpired() {
        if (expiryDate == null || expiryDate.isBlank()) {
            return false;
        }

        try {
            LocalDate exp = LocalDate.parse(expiryDate, DateTimeFormatter.ISO_LOCAL_DATE);
            return !exp.isAfter(LocalDate.now());
        } catch (Exception e) {
            return false; // safe fallback
        }
    }

    // Check if out of stock
    public boolean isOutOfStock() {
        return stock == 0;
    }

    //for low stock!
    public boolean isLowStock() {
        return stock > 0 && stock <= LOW_STOCK_THRESHOLD;
    }

// Inventory condition label
    public String getInventoryCondition() {
        if (isExpired()) {
            return "Expired";
        }
        if (isLowStockByCategory()) {
            return "Low Stock";
        }

        if (isOutOfStock()) {
            return "Out of Stock";
        }
        return "Normal";
    }

    //low stock notification. filtered na by status
    public boolean isLowStockByCategory() {
        if (category == null) {
            return false;
        }

        if (category.equalsIgnoreCase("Equipment")) {
            return stock > 0 && stock <= 3;  
        }

        if (category.equalsIgnoreCase("Medicine") || category.equalsIgnoreCase("Supplies")) {
            return stock > 0 && stock <= LOW_STOCK_THRESHOLD; // default 10
        }
        
        return false;
    }
}
