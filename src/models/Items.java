package models;

public class Items {

    private String itemID, itemName, category, unit, expiryDate, supplier, status, imagePath;
    private int stock;
    private String mongoId;

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
}
