package controllers;

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
    private TextArea remarksArea;
    @FXML
    private Button btnSubmitRequest;
    @FXML
    private Button btnClear;

    //for popup feature of select medecine
    private Popup medicinePopup = new Popup();

    // SAmple data for the medicines
    private List<Medicine> medicines = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        quantityField.setText("1"); //default quantity for the medicine

        loadSampleMedicines();
        buildMedicinePopup();

        //Show popup when clicked the textfield
        medicineNameField.setOnMouseClicked(e -> showMedicinePopup());

        //HIde the popup
        medicineNameField.focusedProperty().addListener((obs, oldV, newV) -> {
            if (!newV) {
                medicinePopup.hide();
            }
        });
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

    //Popup Gallery for Medicines
    // ===============================
// POPUP MEDICINE SELECTOR
// ===============================
    //Popup Gallery for Medicines 
    private void loadSampleMedicines() {
        medicines.add(new Medicine("Biogesic", "For headache and fever", "/resource/medImages/biogesic.jpg"));
        medicines.add(new Medicine("Neozep", "For colds and flu", "/resource/medImages/neozep.jpg"));
        medicines.add(new Medicine("Alaxan Fr", "Muscle Pain reliever", "/resource/medImages/alaxan.jpg"));
        medicines.add(new Medicine("Decolgen", "Sinus, allergies, & flu relief", "/resource/medImages/decolgen.jpg"));
    }

    private void buildMedicinePopup() {
        VBox container = new VBox(10);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: white; -fx-background-radius: 12;");

        for (Medicine med : medicines) {
            HBox card = createMedicineCard(med);
            container.getChildren().add(card);
        }

        ScrollPane scroll = new ScrollPane(container);
        scroll.setPrefSize(280, 260);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: white; -fx-background-radius: 12;");

        medicinePopup.getContent().clear();
        medicinePopup.getContent().add(scroll);

        // --- Auto-hide when clicking outside ---
        scroll.setOnMouseClicked(e -> e.consume());
    }

    private HBox createMedicineCard(Medicine med) {
        HBox card = new HBox(10);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 8; -fx-cursor: hand;");

        // Load image correctly from classpath
        Image img = new Image(getClass().getResourceAsStream(med.imagePath));

        ImageView imgView = new ImageView(img);
        imgView.setFitWidth(50);
        imgView.setFitHeight(50);

        Label nameLabel = new Label(med.name);
        nameLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold;");

        Label infoLabel = new Label(med.info);
        infoLabel.setStyle("-fx-font-size: 11px;");
        infoLabel.setWrapText(true);

        VBox labels = new VBox(3, nameLabel, infoLabel);

        card.getChildren().addAll(imgView, labels);

        // Clicking a card inserts text and hides popup
        card.setOnMouseClicked(e -> {
            medicineNameField.setText(med.name);
            medicinePopup.hide();
        });

        return card;
    }

    private void showMedicinePopup() {
        if (!medicinePopup.isShowing()) {
            medicinePopup.show(medicineNameField.getScene().getWindow());

            double x = medicineNameField.localToScreen(0, 0).getX();
            double y = medicineNameField.localToScreen(0, 0).getY() + medicineNameField.getHeight();

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
    private void handleSubmitRequest(ActionEvent event
    ) {
        String medicineName = medicineNameField.getText().trim();
        String quantity = quantityField.getText().trim();
        String requestedBy = requestedByField.getText().trim();
        String requestedFrom = requestedFromField.getText().trim();
        String remarks = remarksArea.getText().trim();

        if (medicineName.isEmpty() || quantity.isEmpty() || requestedBy.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Incomplete Information", "Please fill out all required fields.");
            return;
        }

        // For now, just simulate saving to a database
        System.out.println("Medicine Requested:");
        System.out.println("Name: " + medicineName);
        System.out.println("Quantity: " + quantity);
        System.out.println("Requested By: " + requestedBy);
        System.out.println("Requested From: " + requestedFrom);
        System.out.println("Remarks: " + remarks);

        showAlert(Alert.AlertType.INFORMATION, "Request Sent", "Medicine request submitted successfully!");
        clearForm();
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

    //Internal class for medicine data
    class Medicine {

        String name;
        String info;
        String imagePath;

        Medicine(String name, String info, String imagePath) {
            this.name = name;
            this.info = info;
            this.imagePath = imagePath;
        }
    }
}
