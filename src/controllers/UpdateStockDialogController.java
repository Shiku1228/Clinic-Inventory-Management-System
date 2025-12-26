package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class UpdateStockDialogController implements Initializable {

    @FXML
    private ComboBox<String> itemBox;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private ComboBox<String> unitBox;

    @FXML
    private TextField stockField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TEMP SAMPLE DATA (replace with DB later)
        itemBox.setItems(FXCollections.observableArrayList(
                "Alcohol",
                "Bandage",
                "Syringe",
                "Gloves"
        ));

        categoryBox.setItems(FXCollections.observableArrayList(
                "Medicine",
                "Equipment",
                "Supply"
        ));

        unitBox.setItems(FXCollections.observableArrayList(
                "Box",
                "Piece",
                "Pack"
        ));

        // Disable category & unit (auto-filled based on item)
        categoryBox.setDisable(true);
        unitBox.setDisable(true);

        // When item changes, auto-fill category & unit
        itemBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) return;

            switch (newVal) {
                case "Alcohol" -> {
                    categoryBox.setValue("Medicine");
                    unitBox.setValue("Bottle");
                }
                case "Bandage" -> {
                    categoryBox.setValue("Supply");
                    unitBox.setValue("Pack");
                }
                case "Syringe" -> {
                    categoryBox.setValue("Equipment");
                    unitBox.setValue("Piece");
                }
                case "Gloves" -> {
                    categoryBox.setValue("Supply");
                    unitBox.setValue("Box");
                }
            }
        });
    }

    @FXML
    private void handleUpdate() {

        String item = itemBox.getValue();
        String category = categoryBox.getValue();
        String unit = unitBox.getValue();
        String stock = stockField.getText();

        if (item == null || stock.isEmpty()) {
            System.out.println("Please complete required fields.");
            return;
        }

        System.out.println("Updating Stock:");
        System.out.println("Item: " + item);
        System.out.println("Category: " + category);
        System.out.println("Unit: " + unit);
        System.out.println("Stock: " + stock);

        closeDialog();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) stockField.getScene().getWindow();
        stage.close();
    }
}
