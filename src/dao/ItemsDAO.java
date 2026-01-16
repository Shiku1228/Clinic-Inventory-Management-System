/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import database.MongoDBConnection;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import models.Items;
import org.bson.Document;
import org.bson.types.ObjectId;
import utils.NotificationManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ItemsDAO {

    private final MongoCollection<Document> itemsCollection;

    public ItemsDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        itemsCollection = db.getCollection("Items Collection");
    }

    public ObservableList<Items> getAllItems() {
        ObservableList<Items> allItems = FXCollections.observableArrayList();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        for (Document doc : itemsCollection.find()) {

            String expiryDateStr = doc.getString("expiryDate");
            String status = doc.getString("status");

            // 🔒 Safe expiry check
            if (expiryDateStr != null && !expiryDateStr.isBlank()) {
                LocalDate expiry = LocalDate.parse(expiryDateStr.trim(), formatter);

                if (!expiry.isAfter(today) && !"Expired".equalsIgnoreCase(status)) {
                    itemsCollection.updateOne(
                            eq("_id", doc.getObjectId("_id")),
                            set("status", "Expired")
                    );
                    status = "Expired"; // sync local object
                }
            }

            Items item = new Items(
                    doc.getObjectId("_id").toHexString(),
                    doc.getString("itemId"),
                    doc.getString("name"),
                    doc.get("category", Document.class).getString("categoryName"),
                    doc.getInteger("quantityOnHand"),
                    doc.getString("unit"),
                    expiryDateStr,
                    doc.getString("supplier"),
                    status,
                    doc.getString("imagePath")
            );

            allItems.add(item);
        }

        return allItems;
    }

    public void addItem(Items item) {
        Document doc = new Document("itemId", generateItemId())
                .append("name", item.getItemName())
                .append("category", new Document("categoryName", item.getCategory()))
                .append("quantityOnHand", item.getStock())
                .append("unit", item.getUnit())
                .append("expiryDate", item.getExpiryDate())
                .append("supplier", item.getSupplier())
                .append("status", item.getStatus())
                .append("imagePath", item.getImagePath());

        itemsCollection.insertOne(doc);

        //notifs for the added items
        NotificationManager.push(
                "New item added: " + item.getItemName(),
                "Just now",
                "INFO"
        );
    }

    public void updateItem(Items item) {

        itemsCollection.updateOne(
                eq("_id", new ObjectId(item.getMongoId())),
                combine(
                        set("itemId", item.getItemID()),
                        set("name", item.getItemName()),
                        set("category.categoryName", item.getCategory()),
                        set("quantityOnHand", item.getStock()),
                        set("unit", item.getUnit()),
                        set("status", item.getStatus())
                )
        );
    }

    private String generateItemId() {
        long count = itemsCollection.countDocuments() + 1;
        return String.format("ITEM-%04d", count);
    }

    public void deleteItem(Items item) {
        if (item == null || item.getMongoId() == null) {
            throw new IllegalArgumentException("Item or mongoId is null");
        }

        itemsCollection.deleteOne(
                eq("_id", new ObjectId(item.getMongoId()))
        );
    }

    public void disableItem(Items item) {
        itemsCollection.updateOne(
                eq("_id", new ObjectId(item.getMongoId())),
                set("status", "Disabled")
        );

        NotificationManager.push(
                "Item disabled: " + item.getItemName(),
                "Just now",
                "ERROR"
        );
    }

    public void enableItem(Items item) {
        itemsCollection.updateOne(
                eq("_id", new ObjectId(item.getMongoId())),
                set("status", "Available")
        );

        NotificationManager.push(
                "Item enabled: " + item.getItemName(),
                "Just now",
                "SUCCESS"
        );
    }

    public ObservableList<Items> getActiveItems() {
        ObservableList<Items> list = FXCollections.observableArrayList();

        for (Document doc : itemsCollection.find(eq("status", "Active"))) {

            Items item = new Items(
                    doc.getObjectId("_id").toHexString(),
                    doc.getString("itemId"),
                    doc.getString("name"),
                    doc.get("category", Document.class).getString("categoryName"),
                    doc.getInteger("quantityOnHand"),
                    doc.getString("unit"),
                    doc.getString("expiryDate"),
                    doc.getString("supplier"),
                    doc.getString("status"),
                    doc.getString("imagePath")
            );

            list.add(item);

            //notif for the expired items
            LocalDate expiry = LocalDate.parse(item.getExpiryDate());
            if (!expiry.isAfter(LocalDate.now())) {
                NotificationManager.push(
                        "Expired " + item.getCategory().toLowerCase() + " detected: " + item.getItemName(),
                        "Today",
                        "EXPIRED"
                );
            }
        }
        return list;
    }

    public Image getItemImage(Items item) {
        try {
            // Get only the filename, in case DB has a full path
            String filename = new File(item.getImagePath()).getName();

            // Look for the image in your src/resource/medImages folder
            File imgFile = new File("src/resource/medImages/" + filename);

            if (imgFile.exists()) {
                return new Image(imgFile.toURI().toString());
            } else {
                System.out.println("⚠ Image not found for item " + item.getItemName() + ", using placeholder.");
                return new Image(new File("src/resource/medImages/placeholder.png").toURI().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Image("https://via.placeholder.com/50"); // fail-safe
        }
    }

    // Decrease stock method
    public void decreaseStock(String itemId, int quantity) {
        ObjectId objectId = new ObjectId(itemId);

        Document itemDoc = itemsCollection.find(eq("_id", objectId)).first();
        if (itemDoc == null) {
            return;
        }

        int currentStock = itemDoc.getInteger("quantityOnHand");
        int newStock = currentStock - quantity;

        itemsCollection.updateOne(
                eq("_id", objectId),
                new Document("$inc", new Document("quantityOnHand", -quantity))
        );

        //Low Stock Notification
        if (newStock <= 10) {
            NotificationManager.push(
                    "Low stock alert: " + itemDoc.getString("name")
                    + " (" + newStock + " remaining)",
                    "Just now",
                    "WARNING"
            );
        }
    }

    //Total Medicines
    public long countAllItems() {
        return itemsCollection.countDocuments();
    }

    //Low Stock (threshhold = 10)
    public long countLowStockItems(int threshold) {
        return itemsCollection.countDocuments(
                Filters.lt("quantityOnHand", threshold)
        );
    }

    //Exoired Itenms
    public long countExpiredItems() {
        String today = LocalDate.now().toString();//yy mm dd

        return itemsCollection.countDocuments(
                Filters.and(
                        Filters.exists("expiryDate", true),
                        Filters.ne("expiryDate", ""),
                        Filters.lte("expiryDate", today)
                )
        );
    }

    public long deleteExpiredItems() {
        String today = LocalDate.now().toString();

        return itemsCollection.deleteMany(
                Filters.and(
                        Filters.exists("expiryDate", true),
                        Filters.ne("expiryDate", null),
                        Filters.ne("expiryDate", ""),
                        Filters.lte("expiryDate", today)
                )
        ).getDeletedCount();
    }

    public void generateItemNotifications() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String nowTime = LocalTime.now().withNano(0).toString();

        for (Document doc : itemsCollection.find()) {
            String name = doc.getString("name");
            String category = doc.get("category", Document.class).getString("categoryName");
            int stock = doc.getInteger("quantityOnHand");
            String expiryStr = doc.getString("expiryDate");

            // Expired
            if (expiryStr != null && !expiryStr.isBlank()) {
                LocalDate expiry = LocalDate.parse(expiryStr.trim(), formatter);
                if (!expiry.isAfter(today)) {
                    // avoid duplicate
                    boolean exists = NotificationManager.getFeed().stream()
                            .anyMatch(n -> n.getMessage().contains(name) && n.getType().equals("EXPIRED"));
                    if (!exists) {
                        NotificationManager.push(
                                "Expired " + category.toLowerCase() + ": " + name,
                                "Today",
                                "EXPIRED"
                        );
                    }
                }
            }

            // Out of stock
            if (stock == 0) {
                boolean exists = NotificationManager.getFeed().stream()
                        .anyMatch(n -> n.getMessage().contains(name) && n.getType().equals("OUT_OF_STOCK"));
                if (!exists) {
                    NotificationManager.push(
                            "Out of stock: " + name,
                            nowTime,
                            "OUT_OF_STOCK"
                    );
                }
            }

            // Low stock
            if (stock > 0 && stock <= 10) { // threshold = 10
                boolean exists = NotificationManager.getFeed().stream()
                        .anyMatch(n -> n.getMessage().contains(name) && n.getType().equals("LOW_STOCK"));
                if (!exists) {
                    NotificationManager.push(
                            "Low stock alert: " + name + " (" + stock + " left)",
                            nowTime,
                            "LOW_STOCK"
                    );
                }
            }
        }
    }
}
