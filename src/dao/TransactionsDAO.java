package dao;

import com.mongodb.client.MongoCollection;
import dao.TransactionsDAO;
import database.MongoDBConnection;
import com.mongodb.client.MongoDatabase;
import models.Transactions;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.mongodb.client.model.Sorts.descending;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;

public class TransactionsDAO {

    private MongoCollection<Document> collection;

    public TransactionsDAO(MongoDatabase db) {
        collection = db.getCollection("Transactions Collection");
    }
    
    public List<Transactions> getAllTransactions() {
        List<Transactions> list = new ArrayList<>();

        for (Document doc : collection.find().sort(descending("transactionDate"))) {
            Transactions t = new Transactions(
                    doc.getString("transactionDate"),
                    doc.getString("itemName"),
                    doc.getString("type"),
                    doc.getInteger("quantity"),
                    doc.getString("performedBy"),
                    doc.getString("remarks")
            );

            t.setId(doc.getObjectId("_id")); // set MongoDB _id for Transaction ID column
            list.add(t);
        }
        return list;
    }

    public boolean addTransaction(Transactions t) {
        try {
            Document doc = new Document("transactionDate", t.dateProperty().get())
                    .append("itemName", t.itemNameProperty().get())
                    .append("type", t.typeProperty().get())
                    .append("quantity", t.quantityProperty().get())
                    .append("performedBy", t.performedByProperty().get())
                    .append("remarks", t.remarksProperty().get());

            collection.insertOne(doc);

            t.setId(doc.getObjectId("_id")); // set ID after insert
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Transactions> getTransactionsByFilter(String type) {
        List<Transactions> list = new ArrayList<>();

        for (Document doc : collection.find(new Document("type", type)).sort(descending("transactionDate"))) {
            Transactions t = new Transactions(
                    doc.getString("transactionDate"),
                    doc.getString("itemName"),
                    doc.getString("type"),
                    doc.getInteger("quantity"),
                    doc.getString("performedBy"),
                    doc.getString("remarks")
            );
            t.setId(doc.getObjectId("_id"));
            list.add(t);
        }

        return list;
    }
}
