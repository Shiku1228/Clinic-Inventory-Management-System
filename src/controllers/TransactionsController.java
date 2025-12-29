package controllers;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import models.Transactions;

public class TransactionsController implements Initializable {

    /* =========================
       TABLE & COLUMNS
       ========================= */
    @FXML
    private TableView<Transactions> transactionTable;

    @FXML
    private TableColumn<Transactions, String> colDate;
    @FXML
    private TableColumn<Transactions, String> colItem;
    @FXML
    private TableColumn<Transactions, String> colType;
    @FXML
    private TableColumn<Transactions, Integer> colQuantity;
    @FXML
    private TableColumn<Transactions, String> colPerformedBy;
    @FXML
    private TableColumn<Transactions, String> colRemarks;
    @FXML
    private TableColumn<Transactions, Void> colAction;

    /* =========================
       CONTROLS
       ========================= */
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> filterCombo;

    /* =========================
       DATA
       ========================= */
    private final ObservableList<Transactions> transactionList
            = FXCollections.observableArrayList();

    /* =========================
       INITIALIZE
       ========================= */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // Bind table columns to model
        colDate.setCellValueFactory(data -> data.getValue().dateProperty());
        colItem.setCellValueFactory(data -> data.getValue().itemNameProperty());
        colType.setCellValueFactory(data -> data.getValue().typeProperty());
        colQuantity.setCellValueFactory(
                data -> data.getValue().quantityProperty().asObject()
        );
        colPerformedBy.setCellValueFactory(
                data -> data.getValue().performedByProperty()
        );
        colRemarks.setCellValueFactory(
                data -> data.getValue().remarksProperty()
        );

        // Filter options
        filterCombo.getItems().addAll(
                "Today",
                "Last Week",
                "Last Month"
        );
        filterCombo.getSelectionModel().selectFirst();

        // Load sample data (temporary)
        loadSampleTransactions();

        transactionTable.setItems(transactionList);
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        addViewButtonToTable();

        transactionTable.setRowFactory(tv -> {
            TableRow<Transactions> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    openTransactionDetails(row.getItem());
                }
            });
            return row;
        });
    }

    /* =========================
       SAMPLE DATA
       ========================= */
    private void loadSampleTransactions() {
        transactionList.addAll(
                new Transactions(
                        "2025-11-14 10:21 AM",
                        "Paracetamol",
                        "Issued",
                        -5,
                        "Nurse Anna",
                        "Request #102"
                ),
                new Transactions(
                        "2025-11-14 09:32 AM",
                        "Alcohol",
                        "Received",
                        50,
                        "Admin Juan",
                        "Delivered by RMMC"
                ),
                new Transactions(
                        "2025-11-13 07:50 AM",
                        "Betadine",
                        "Expired",
                        -2,
                        "System Notification",
                        "Auto Expiry"
                )
        );
    }

    /* =========================
       ACTION HANDLERS
       ========================= */
    @FXML
    private void handleAddTransaction() {
        System.out.println("Add Transaction clicked");
        // TODO: Open Add Transaction modal
    }

    @FXML
    private void handleRefresh() {
        System.out.println("Refresh clicked");
        // TODO: Reload from database
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText();
        System.out.println("Searching: " + keyword);
        // TODO: Implement search filtering
    }

    private void addViewButtonToTable() {
        colAction.setCellFactory(param -> new TableCell<>() {

            private final Button viewButton = new Button("View");

            {
                viewButton.setOnAction(event -> {
                    Transactions transaction
                            = getTableView().getItems().get(getIndex());
                    openTransactionDetails(transaction);
                });

                viewButton.setStyle("-fx-font-size: 11px;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(viewButton);
                }
            }
        });
    }

    private void openTransactionDetails(Transactions transaction) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/TransactionDetails.fxml")
            );

            Parent root = loader.load();

            // Get the controller of the dialog
            TransactionDetailsController controller = loader.getController();

            // Pass the selected transaction
            controller.setTransaction(transaction);

            // Create modal window
            Stage stage = new Stage();
            stage.setTitle("Transaction Details");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            stage.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
