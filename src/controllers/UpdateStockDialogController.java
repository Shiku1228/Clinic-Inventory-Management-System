package controllers;

import dao.ItemsDAO;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Items;

public class UpdateStockDialogController implements Initializable {

    private Items selectedItem;

    @FXML
    private TextField itemNameField;

    @FXML
    private ComboBox<String> categoryBox;

    @FXML
    private ComboBox<String> unitBox;

    @FXML
    private TextField stockField;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        categoryBox.setItems(FXCollections.observableArrayList(
                "Medicine",
                "Equipment",
                "Supply"
        ));

        unitBox.setItems(FXCollections.observableArrayList(
                "Box",
                "Piece",
                "Pack",
                "Bottle"
        ));

        // allow selection
        categoryBox.setDisable(false);
        unitBox.setDisable(false);
    }

    //called from manage item controller
    public void setItem(Items item) {
        this.selectedItem = item;

        //prefilling the fields with the selcted item
        itemNameField.setText(item.getItemName());
        categoryBox.setValue(item.getCategory());
        unitBox.setValue(item.getUnit());
        stockField.setText(String.valueOf(item.getStock()));

    }

    @FXML
    private void handleUpdate() {

        String itemName = itemNameField.getText();
        String category = categoryBox.getValue();
        String unit = unitBox.getValue();
        String stockText = stockField.getText();

        if (itemName == null || itemName.isBlank()) {
            System.out.println("Please complete required fields.");
            return;
        }

        int stock;

        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            System.out.println("Stock must be a number!");
            return;
        }

        selectedItem.setItemName(itemName);
        selectedItem.setCategory(category);
        selectedItem.setUnit(unit);
        selectedItem.setStock(stock);

        ItemsDAO dao = new ItemsDAO();
        dao.updateItem(selectedItem);

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
