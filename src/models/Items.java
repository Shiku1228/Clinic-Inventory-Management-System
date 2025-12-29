package models;

public class Items {
    private String itemID, itemName, category, unit, expiryDate, supplier, status, imagePath;
    private int stock;
    
    public Items(String itemID, String itemName, String category, int stock, String unit, String expiryDate, String supplier, String status, String imagePath) {
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
    
    public String getItemID() {return itemID;}
    public String getItemName() {return itemName;}
    public String getCategory() {return category;}
    public int getStock() {return stock;}
    public String getUnit() {return unit;}
    public String getExpiryDate() {return expiryDate;}
    public String getSupplier() {return supplier;}
    public String getStatus() {return status;}
    public String getImagePath() {return imagePath;}
    
    //setters
    public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
}
}

