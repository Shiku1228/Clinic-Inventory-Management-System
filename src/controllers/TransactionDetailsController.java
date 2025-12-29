/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Transactions;

/**
 * FXML Controller class
 *
 * @author RENZ S. LATANGGA
 */
public class TransactionDetailsController implements Initializable {

    @FXML
    private TextField txtDate;
    @FXML
    private TextField txtRequester;
    @FXML
    private TextField txtRequesterId;
    @FXML
    private TextField txtItem;
    @FXML
    private TextField txtType;
    @FXML
    private TextField txtQuantity;
    @FXML
    private TextField txtPerformedBy;
    @FXML
    private TextField txtRemarks;
    @FXML
    private TextField txtTransactionNo;

    //Data Holder
    private Transactions transaction;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public void setTransaction(Transactions transaction) {
        this.transaction = transaction;

        // Populate fields
        txtDate.setText(transaction.dateProperty().get());
        txtItem.setText(transaction.itemNameProperty().get());
        txtType.setText(transaction.typeProperty().get());
        txtQuantity.setText(String.valueOf(transaction.quantityProperty().get()));
        txtPerformedBy.setText(transaction.performedByProperty().get());
        txtRemarks.setText(transaction.remarksProperty().get());

        // TEMP / PLACEHOLDER (until DB is wired)
        txtRequester.setText("Renz S. Latangga");
        txtRequesterId.setText("2411600373");
        txtTransactionNo.setText("Clnc089347380ABC");
    }

    /* =========================
       BUTTON ACTIONS
       ========================= */
    @FXML
    private void handleClose() {
        Stage stage = (Stage) txtDate.getScene().getWindow();
        stage.close();
    }

}
