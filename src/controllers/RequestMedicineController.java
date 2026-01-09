package controllers;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Popup;
import javafx.event.ActionEvent;
import models.Items;
import org.bson.Document;
import dao.ItemsDAO;
import dao.TransactionsDAO;
import database.MongoDBConnection;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;
import models.Transactions;
import utils.NotificationManager;
import utils.Session;

public class RequestMedicineController implements Initializable {

    @FXML
    private TextField medicineNameField;
    @FXML
    private TextField quantityField;
    @FXML
    private TextField requestedByField;
    @FXML
    private TextField requestedFromField;
    @FXML
    private TextField requesterIdField;
    @FXML
    private TextArea remarksArea;
    @FXML
    private Button btnSubmitRequest;
    @FXML
    private Button btnClear;

    private final ItemsDAO itemsDAO = new ItemsDAO();

    //for popup feature of select medecine
    private Popup medicinePopup = new Popup();

    //medicines from teh database
    private List<Items> medicines = new ArrayList<>();

    //submission sa database
    private TransactionsDAO transactionsDAO;
    private MongoDatabase database;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quantityField.setText("1"); //default quantity for the medicine

        //load the medicines from the database
        loadAvailableMedicines();
        buildMedicinePopup();

        //Show popup when clicked the textfield
        medicineNameField.setOnMouseClicked(e -> {
            if (medicines.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION,
                        "No Medicines Available",
                        "Please add items first in Manage Items.");
                return;
            }
            showMedicinePopup();
        });

        //HIde the popup
        medicineNameField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                medicinePopup.hide();
            }
        });

        transactionsDAO = new TransactionsDAO(MongoDBConnection.getDatabase());
    }

    // Functioning of the quantity buttons
    @FXML
    private void increaseQuantity(ActionEvent event) {
        try {
            int qty = Integer.parseInt(quantityField.getText());
            quantityField.setText(String.valueOf(qty + 1));
        } catch (NumberFormatException e) {
            quantityField.setText("1");
        }
    }

    @FXML
    private void decreaseQuantity(ActionEvent event) {
        try {
            int qty = Integer.parseInt(quantityField.getText());
            if (qty > 1) {
                quantityField.setText(String.valueOf(qty - 1));
            }
        } catch (NumberFormatException e) {
            quantityField.setText("1");
        }
    }

    private void buildMedicinePopup() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        for (Items med : medicines) {
            HBox card = createMedicineCard(med);
            container.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(container);
        scroll.setPrefSize(320, 220);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-background-radius: 12;");

        medicinePopup.getContent().clear();
        medicinePopup.getContent().add(scroll);
        medicinePopup.setAutoFix(true);
        medicinePopup.setAutoHide(true);
        medicinePopup.setHideOnEscape(true);

        // --- Auto-hide when clicking outside ---
        scroll.setOnMouseClicked(e -> e.consume());
    }

    private HBox createMedicineCard(Items item) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 8; -fx-cursor: hand;");

        // ✅ Load image via DAO
        Image image = itemsDAO.getItemImage(item);

        ImageView imgView = new ImageView(image);
        imgView.setFitWidth(50);
        imgView.setFitHeight(50);
        imgView.setPreserveRatio(true);
        imgView.setSmooth(true);
        imgView.setCache(true);

        Label nameLabel = new Label(item.getItemName());
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label infoLabel = new Label("Stock: " + item.getStock() + " • " + item.getUnit());
        infoLabel.setStyle("-fx-font-size: 11px;");

        VBox labels = new VBox(3, nameLabel, infoLabel);

        card.getChildren().addAll(imgView, labels);

        card.setOnMouseClicked(e -> {
            medicineNameField.setText(item.getItemName());
            medicineNameField.setUserData(item);
            medicinePopup.hide();
        });

        return card;
    }

    private void showMedicinePopup() {
        if (!medicinePopup.isShowing()) {
            medicinePopup.show(medicineNameField.getScene().getWindow());

            double x = medicineNameField.localToScreen(0, 0).getX();
            double y = medicineNameField.localToScreen(0, 0).getY() + medicineNameField.getHeight() + 6;

            medicinePopup.setX(x);
            medicinePopup.setY(y);

            // Auto-hide when clicking anywhere else
            Platform.runLater(() -> {
                medicineNameField.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
                    if (medicinePopup.isShowing()
                            && !medicineNameField.isHover()
                            && !medicinePopup.getContent().get(0).isHover()) {
                        medicinePopup.hide();
                    }
                });
            });
        }
    }

    //Form Submission and Validation
    @FXML
    private void handleSubmitRequest(ActionEvent event) {
        String medicineName = medicineNameField.getText().trim();
        String quantityStr = quantityField.getText().trim();
        String requestedBy = requestedByField.getText().trim();
        String requestedFrom = requestedFromField.getText().trim();
        String requesterName = requestedByField.getText().trim();
        String requesterId = requesterIdField.getText().trim();
        String remarks = remarksArea.getText().trim();

        if (medicineName.isEmpty() || quantityStr.isEmpty()
                || requestedBy.isEmpty() || requesterIdField.getText().isEmpty()) {

            showAlert(Alert.AlertType.WARNING,
                    "Incomplete Information",
                    "Please fill out all required fields including Requester ID.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Quantity", "Please eneter a valid number for quantity!");
            return;
        }

        //get selected items object if available
        Items selectedItem = (Items) medicineNameField.getUserData();

        if (selectedItem == null) {
            showAlert(Alert.AlertType.WARNING, "Invalid Selection", "Please select a valid medicine from the list.");
            return;
        }

        try {
            // create transaction object
            String transactionId = transactionsDAO.generateNextTransactionId();

            Transactions transaction = new Transactions(
                    transactionId,
                    LocalDateTime.now().toString(),
                    medicineName,
                    "REQUEST",
                    quantity,
                    requestedFrom, // performedBy
                    requesterName, // requesterName
                    requesterId, // requesterId
                    remarks
            );

            // Save to MongoDB
            transactionsDAO.addTransaction(transaction);

            // Optional: reduce stock in Items collection
            itemsDAO.decreaseStock(selectedItem.getMongoId(), quantity);
            
            NotificationManager.push("New Medicine Request: " + selectedItem.getItemName() + " x " + quantity + "Requested By: " + requestedBy, "Just Now", "INFO");

            showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Medicine request submitted successfully!");

            clearForm();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to submit the medicine request.");
        }
    }

    @FXML
    private void handleClearForm(ActionEvent event
    ) {
        clearForm();
    }

    private void clearForm() {
        medicineNameField.clear();
        quantityField.clear();
        requestedByField.clear();
        requestedFromField.clear();
        remarksArea.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadAvailableMedicines() {
        medicines.clear();
        medicines.addAll(itemsDAO.getActiveItems());

        MongoDatabase db = MongoDBConnection.getDatabase();
        MongoCollection<Document> collection = db.getCollection("Items Collection");

        for (var doc : collection.find()) {

            String status = doc.getString("status");

            //filter here the status 
            if (!"Available".equalsIgnoreCase(status)) {
                continue;
            }

            Items item = new Items(
                    doc.getObjectId("_id").toHexString(),
                    doc.getString("itemId"),
                    doc.getString("name"),
                    doc.get("category", Document.class)
                            .getString("categoryName"),
                    doc.getInteger("quantityOnHand", 0),
                    doc.getString("unit"),
                    doc.getString("expiryDate"),
                    doc.getString("supplier"),
                    status,
                    doc.getString("imagePath")
            );
            medicines.add(item);
        }
    }
}
