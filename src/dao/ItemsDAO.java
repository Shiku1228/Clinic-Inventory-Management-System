/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import database.MongoDBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import models.Items;
import org.bson.Document;

public class ItemsDAO {

    private final MongoCollection<Document> itemsCollection;

    public ItemsDAO() {
        MongoDatabase db = MongoDBConnection.getDatabase();
        itemsCollection = db.getCollection("Items Collection");
    }

    public ObservableList<Items> getAllItems() {
        ObservableList<Items> itemList = FXCollections.observableArrayList();

        for (Document doc : itemsCollection.find()) {

            Items item = new Items(
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

            itemList.add(item);
        }
        return itemList;
    }

    public void addItem(Items item) {
        Document doc = new Document("itemId", item.getItemID())
                .append("name", item.getItemName())
                .append("category", new Document("categoryName", item.getCategory()))
                .append("quantityOnHand", item.getStock())
                .append("unit", item.getUnit())
                .append("expiryDate", item.getExpiryDate())
                .append("supplier", item.getSupplier())
                .append("status", item.getStatus())
                .append("imagePath", item.getImagePath());

        itemsCollection.insertOne(doc);
    }
}
