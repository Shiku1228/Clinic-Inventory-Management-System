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

        txtDate.setText(transaction.getDate());
        txtRequester.setText(transaction.getRequesterName());
        txtRequesterId.setText(transaction.getRequesterId());
        txtItem.setText(transaction.getItemName());
        txtType.setText(transaction.getType());
        txtQuantity.setText(String.valueOf(transaction.getQuantity()));
        txtPerformedBy.setText(transaction.getPerformedBy());
        txtRemarks.setText(transaction.getRemarks());
        txtTransactionNo.setText(transaction.getTransactionId());
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
