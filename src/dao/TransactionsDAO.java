package dao;

import com.mongodb.client.MongoCollection;
import dao.TransactionsDAO;
import database.MongoDBConnection;
import com.mongodb.client.MongoDatabase;
import models.Transactions;
import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.DayOfWeek;

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
                    doc.getString("transactionId"),
                    doc.getString("date"),
                    doc.getString("itemName"),
                    doc.getString("type"),
                    doc.getInteger("quantity", 0),
                    doc.getString("performedBy"),
                    doc.getString("remarks")
            );

            t.setId(doc.getObjectId("_id")); // set MongoDB _id for Transaction ID column
            list.add(t);
        }
        return list;
    }

    public void addTransaction(Transactions transaction) {

        String newTransactionId = generateNextTransactionId();

        Document doc = new Document("transactionId", newTransactionId)
                .append("date", transaction.getDate())
                .append("itemName", transaction.getItemName())
                .append("type", transaction.getType())
                .append("quantity", transaction.getQuantity())
                .append("performedBy", transaction.getPerformedBy())
                .append("remarks", transaction.getRemarks());

        collection.insertOne(doc);
    }

    public List<Transactions> getTransactionsByFilter(String type) {
        List<Transactions> list = new ArrayList<>();

        for (Document doc : collection.find(new Document("type", type)).sort(descending("transactionDate"))) {
            Transactions t = new Transactions(
                    doc.getString("transactionId"),
                    doc.getString("date"),
                    doc.getString("itemName"),
                    doc.getString("type"),
                    doc.getInteger("quantity", 0),
                    doc.getString("performedBy"),
                    doc.getString("remarks")
            );

            t.setId(doc.getObjectId("_id")); // set MongoDB _id for Transaction ID column
            list.add(t);
        }

        return list;
    }

    public String generateNextTransactionId() {
        long count = collection.countDocuments();
        return String.format("TRX-%03d", count + 1);
    }

    public long countTransactionsToday() {
        String today = java.time.LocalDate.now().toString();
        return collection.countDocuments(
                new Document("date", new Document("$regex", "^" + today))
        );
    }

    public long countItemsReceived() {
        return collection.countDocuments(
                new Document("type", "RECEIVED")
        );
    }

    public long countItemsIssued() {
        return collection.countDocuments(
                new Document("type", "REQUEST")
        );
    }

    public long countExpiredItems() {
        return collection.countDocuments(
                new Document("type", "EXPIRED")
        );
    }

    //for today
    public List<Transactions> getTransactionsToday() {
        LocalDate today = LocalDate.now();
        return getTransactionsBetweenDates(today, today);
    }

    //for this wweek
    public List<Transactions> getTransactionsThisWeek() {
        LocalDate today = LocalDate.now();
        LocalDate monday = today.with(DayOfWeek.MONDAY);

        return getTransactionsBetweenDates(monday, today);
    }

    //for the last week
    public List<Transactions> getTransactionsLastWeek() {
        LocalDate today = LocalDate.now();
        LocalDate lastWeekEnd = today.with(DayOfWeek.MONDAY).minusDays(1);
        LocalDate lastWeekStart = lastWeekEnd.minusDays(6);

        return getTransactionsBetweenDates(lastWeekStart, lastWeekEnd);
    }

    //for last month
    public List<Transactions> getTransactionsLastMonth() {
        LocalDate today = LocalDate.now();
        LocalDate monthAgo = today.minusDays(30);

        return getTransactionsBetweenDates(monthAgo, today);
    }

    private List<Transactions> getTransactionsBetweenDates(LocalDate start, LocalDate end) {
        List<Transactions> list = new ArrayList<>();

        Document query = new Document("date",
                new Document("$gte", start.toString())
                        .append("$lte", end.plusDays(1).toString())
        );

        for (Document doc : collection.find(query).sort(descending("date"))) {
            Transactions t = new Transactions(
                    doc.getString("transactionId"),
                    doc.getString("date"),
                    doc.getString("itemName"),
                    doc.getString("type"),
                    doc.getInteger("quantity", 0),
                    doc.getString("performedBy"),
                    doc.getString("remarks")
            );

            t.setId(doc.getObjectId("_id"));
            list.add(t);
        }
        
        return list;
    }
}
