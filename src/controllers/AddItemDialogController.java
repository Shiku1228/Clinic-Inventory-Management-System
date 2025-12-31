/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import dao.ItemsDAO;
import java.io.File;
import javafx.fxml.FXML;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;
import models.Items;

/**
 * FXML Controller class
 *
 * @author RENZ S. LATANGGA
 */
public class AddItemDialogController implements Initializable {

    //Text Fields
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField stockField;
    @FXML
    private TextField supplierField;

    //Combo Boxes
    @FXML
    private ComboBox<String> categoryBox;
    @FXML
    private ComboBox<String> unitBox;
    @FXML
    private ComboBox<String> statusBox;

    //Date Picker
    @FXML
    private DatePicker expiryDatePicker;
    //Buttons
    @FXML
    private Button chooseImageButton;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;

    private File selectedImage;
    @FXML
    private VBox expiryContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //hide expiry container by default (it depends on the category)
        expiryContainer.setVisible(false);
        expiryContainer.setManaged(false);

        //add listener to categoryBox
        categoryBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null) {
                return;
            }

            if (newVal.equalsIgnoreCase("Equipment")) {
                expiryContainer.setVisible(false);
                expiryContainer.setManaged(false);
                expiryDatePicker.setValue(null);
            } else {
                expiryContainer.setVisible(true);
                expiryContainer.setManaged(true);
            }
        });

        // Populate Combo boxes
        categoryBox.getItems().addAll(
                "Medicine",
                "Supplies",
                "Equipment"
        );

        unitBox.getItems().addAll(
                "Pieces",
                "Box",
                "Bottle",
                "Pack"
        );

        statusBox.getItems().addAll(
                "Available",
                "Low Stock",
                "Out of Stock",
                "Expired"
        );
    }

    //Button Hnadlers
    @FXML
    private void handleChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select an item image!");

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Image Files", "*.png", "*.jpg", "*.jpeg"
                )
        );

        selectedImage = chooser.showOpenDialog(getStage());

        if (selectedImage != null) {
            chooseImageButton.setText("Image Selected");
        }
    }

    @FXML
    private void handleSubmit() {
        try {
            ItemsDAO dao = new ItemsDAO();
            Items newItem = new Items(
                    null, //id na ginawa ni mongoDb
                    null, //item ID can be genrated automatically of needed
                    itemNameField.getText(),
                    categoryBox.getValue(),
                    Integer.parseInt(stockField.getText()),
                    unitBox.getValue(),
                    expiryDatePicker.getValue() != null ? expiryDatePicker.getValue().toString() : null,
                    supplierField.getText(),
                    statusBox.getValue(),
                    null //will set after handling the images
            );

            //handles the image upload
            if (selectedImage != null) {
                // Target folder: existing resource/medImages
                File targetDir = new File("src/resource/medImages"); // <-- updated to your existing folder
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }

                // Destination file
                File destFile = new File(targetDir, selectedImage.getName());

                // Copy the file
                java.nio.file.Files.copy(
                        selectedImage.toPath(),
                        destFile.toPath(),
                        java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );

                // Save relative path to newItem
                newItem.setImagePath(destFile.getPath()); // <-- store full path
            }

            dao.addItem(newItem);

            closeDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = getStage();
        if (stage != null) {
            stage.close();
        }
    }

    private Stage getStage() {
        return (Stage) itemNameField.getScene().getWindow();
    }
}
