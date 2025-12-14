package models;

import javafx.beans.property.*;

public class Transactions {

    private final StringProperty date;
    private final StringProperty itemName;
    private final StringProperty type;
    private final IntegerProperty quantity ;
    private final StringProperty performedBy;
    private final StringProperty remarks;

    public Transactions(String date, String itemName, String type, int quantity, String performedBy, String remarks) {
        this.date = new SimpleStringProperty(date);
        this.itemName = new SimpleStringProperty(itemName);
        this.type = new SimpleStringProperty(type);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.performedBy = new SimpleStringProperty(performedBy);
        this.remarks = new SimpleStringProperty(remarks);
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

    public StringProperty remarksProperty() {
        return remarks;
    }
}

