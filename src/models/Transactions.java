package models;

import javafx.beans.property.*;
import org.bson.types.ObjectId;

public class Transactions {

    private ObjectId id;

    private final StringProperty transactionId;
    private final StringProperty date;
    private final StringProperty itemName;
    private final StringProperty type;
    private final IntegerProperty quantity;
    private final StringProperty performedBy;
    private final StringProperty remarks;
    private final StringProperty requesterName;
    private final StringProperty requesterId;

    public Transactions(String transactionId,
            String date,
            String itemName,
            String type,
            int quantity,
            String performedBy,
            String requesterName,
            String requesterId,
            String remarks
    ) {
        this.transactionId = new SimpleStringProperty(transactionId);
        this.date = new SimpleStringProperty(date);
        this.itemName = new SimpleStringProperty(itemName);
        this.type = new SimpleStringProperty(type);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.performedBy = new SimpleStringProperty(performedBy);
        this.requesterName = new SimpleStringProperty(requesterName);
        this.requesterId = new SimpleStringProperty(requesterId);
        this.remarks = new SimpleStringProperty(remarks);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public StringProperty transactionIdProperty() {
        return transactionId;
    }

    public StringProperty dateProperty() {
        return date;
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public StringProperty performedByProperty() {
        return performedBy;
    }

    public StringProperty requesterNameProperty() {
        return requesterName;
    }

    public StringProperty requesterIdProperty() {
        return requesterId;
    }

    public StringProperty remarksProperty() {
        return remarks;
    }

    //getting the raw values
    public String getTransactionId() {
        return transactionId.get();
    }

    public String getDate() {
        return date.get();
    }

    public String getItemName() {
        return itemName.get();
    }

    public String getType() {
        return type.get();
    }

    public int getQuantity() {
        return quantity.get();
    }

    public String getPerformedBy() {
        return performedBy.get();
    }

    public String getRequesterName() {
        return requesterName.get();
    }

    public String getRequesterId() {
        return requesterId.get();
    }

    public String getRemarks() {
        return remarks.get();
    }

}
