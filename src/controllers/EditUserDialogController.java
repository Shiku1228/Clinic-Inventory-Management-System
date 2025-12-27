/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Users;

/**
 * FXML Controller class
 *
 * @author RENZ S. LATANGGA
 */
public class EditUserDialogController implements Initializable {

    @FXML
    private TextField nameField;
    @FXML
    private TextField contactField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> roleBox;
    @FXML
    private ComboBox<String> statusBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    
    private Users user;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populate the rolebox
        roleBox.getItems().addAll(
                "Director",
                "Doctor",
                "Nurse",
                "Admin"
        );
        
        statusBox.getItems().addAll(
                "Active",
                "Inactive"
        );
    }    
    
    public void setUser(Users user){
        this.user = user;
        
        nameField.setText(user.getName());
        contactField.setText(user.getContact());
        emailField.setText(user.getEmail());
        roleBox.setValue(user.getRole());
        statusBox.setValue(user.getStatus());
    }
    
    @FXML
    private void handleSave(){
        if (user == null) return;
        
        user.setName(nameField.getText());
        user.setContact(contactField.getText());
        user.setEmail(emailField.getText());
        user.setRole(roleBox.getValue());
        user.setStatus(statusBox.getValue());
        
        closeWindow();
    }
    @FXML
    private void handleCancel(){
        closeWindow();
    }
    
    @FXML
    private void closeWindow(){
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
    
}
