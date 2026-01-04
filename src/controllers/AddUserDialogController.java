/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import dao.UsersDAO;
import database.MongoDBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.UsersDAO;
import com.mongodb.client.MongoDatabase;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.image.ImageView;
import models.Users;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AddUserDialogController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField passwordField;
    @FXML
    private ComboBox<String> roleBox;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private Button submitButton;
    @FXML
    private Button cancelButton;
    @FXML
    private ImageView avatarPreview;
    @FXML
    private Button chooseAvatarBtn;

    private String avatarPath;

    private Users createdUser;

    private dao.UsersDAO usersDAO;

    private boolean isSubmitting = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleBox.getItems().addAll("Director", "Doctor", "Nurse", "Admin");
        statusBox.getItems().addAll("Active", "Inactive");

        //for setting default of selection
        roleBox.getSelectionModel().selectFirst();
        statusBox.getSelectionModel().selectFirst();

        usersDAO = new UsersDAO(MongoDBConnection.getDatabase());

        //prevent Enter key from firing submit button automatically
        submitButton.setDefaultButton(false);
        cancelButton.setCancelButton(false);
    }
    
    @FXML
    private void handleSubmit() {
        // Prevent double submission
        if (isSubmitting) {
            return;
        }
        isSubmitting = true;
        submitButton.setDisable(true);

        try {
            String username = nameField.getText().trim();
            String password = passwordField.getText().trim();
            String role = roleBox.getValue();
            String contact = contactField.getText().trim();
            String email = emailField.getText().trim();
            String status = statusBox.getValue();

            // Validation
            if (username.isEmpty() || password.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Username and password cannot be empty.");
                return;
            }

            if (avatarPath == null) {
                showAlert(Alert.AlertType.WARNING, "Validation Error", "Please choose an avatar.");
                return;
            }

            // Copy avatar to resource folder
            String savedAvatarPath;
            try {
                File sourceFile = new File(new java.net.URI(avatarPath));
                String fileName = sourceFile.getName();
                File destDir = new File("src/resource/avatars");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                File destFile = new File(destDir, fileName);
                Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                savedAvatarPath = "/resource/avatars/" + fileName;
            } catch (IOException | java.net.URISyntaxException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "File Error", "Failed to save avatar image.");
                return;
            }

            // Create the user object
            String newUserId = usersDAO.generateNextUserId();
            createdUser = new Users(
                    null,
                    newUserId,
                    username,
                    role,
                    contact,
                    email,
                    status,
                    savedAvatarPath
            );

            // Insert into database
            if (usersDAO.insertUser(createdUser)) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "User added successfully!");
                closeDialog(); // close dialog only after successful insertion
            } else {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add user to database.");
            }

        } finally {
            // ALWAYS reset flag and enable button
            isSubmitting = false;
            submitButton.setDisable(false);
        }
    }
    

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    //helper to close the dialog
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    //helper to show the Notifications
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Users getCreatedUser() {
        return createdUser;
    }

    @FXML
    private void handleChooseAvatar() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choose Avatar");
        fileChooser.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        java.io.File selectedFile = fileChooser.showOpenDialog(chooseAvatarBtn.getScene().getWindow());
        if (selectedFile != null) {
            avatarPath = selectedFile.toURI().toString(); // store path for saving user
            avatarPreview.setImage(new javafx.scene.image.Image(avatarPath)); // show preview
        }

    }
}
