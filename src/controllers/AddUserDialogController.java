/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import models.Users;

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
    
    private Users createdUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        roleBox.getItems().addAll("Director", "Doctor", "Nurse", "Admin");
        statusBox.getItems().addAll("Active", "Inactive");
        
        //for setting default of selection
        roleBox.getSelectionModel().selectFirst();
        statusBox.getSelectionModel().selectFirst();
    }    
    
    @FXML
    private void handleSubmit(){
        String username = nameField.getText().trim();
        String password = passwordField.getText().trim();
        String role = roleBox.getValue();
        String contact = contactField.getText().trim();
        String email = emailField.getText().trim();
        String status = statusBox.getValue();
        
        //validation
        if(username.isEmpty() || password.isEmpty()){
            showAlert(Alert.AlertType.WARNING, "Validation Error", "Username and password cannot be empty.");
            return;
        }
        
        //Create the user object 
        createdUser = new Users(
                String.valueOf(System.currentTimeMillis()),
                username,
                role,
                contact,
                email,
                status,
                "resource/avatars/user.png"//default image for a user
        );
        
        //debugging message
        System.out.println("User Created: " + username);
        
        closeDialog();
    }
    
    @FXML
    private void handleCancel(){
        closeDialog();
    }
    
    //helper to close the dialog
    private void closeDialog(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    //helper to show the Notifications
    private void showAlert(Alert.AlertType type, String title, String message){
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public Users getCreatedUser(){
        return createdUser;
    }
}
