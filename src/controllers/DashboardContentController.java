package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import utils.Session;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.geometry.Insets;
import javafx.scene.layout.HBox;
import models.Notifications;
import utils.NotificationManager;

public class DashboardContentController {

    @FXML
    private Label dateTimeLabel;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Label totalMedicinesLabel;
    @FXML
    private Label lowSupplyLabel;
    @FXML
    private Label totalUsersLabel;
    @FXML
    private Label requestsTodayLabel;
    @FXML
    private Label expiredItemsLabel;

    @FXML
    private VBox totalMedicinesCard;
    @FXML
    private VBox lowSupplyCard;
    @FXML
    private VBox registeredUserCard;
    @FXML
    private VBox requestTodayCard;
    @FXML
    private VBox expiredItemsCard;

    @FXML
    private Button btnAddNewMedicine;
    @FXML
    private Button btnViewInventory;
    @FXML
    private Button btnNewRequest;

    @FXML
    private ListView<Notifications> activityList;

    private DashboardController dashboardController;

    public void initialize() {
        // Initialize date/time
        updateDateTime();

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.minutes(1), e -> updateDateTime())
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        // Initialize dashboard stats (replace with DB queries later)
        totalMedicinesLabel.setText("142");
        lowSupplyLabel.setText("5");
        totalUsersLabel.setText("7");
        requestsTodayLabel.setText("12");
        expiredItemsLabel.setText("3");

        // Set welcome message
        welcomeLabel.setText("Welcome Back, " + Session.getUsername() + "!");

        applyRoleBasedAccess();

        activityList.setItems(NotificationManager.getFeed());

        activityList.setCellFactory(list -> new ListCell<Notifications>() {
            @Override
            protected void updateItem(Notifications item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    // VBox for the notification
                    VBox box = new VBox(5);
                    box.setPadding(new Insets(15)); // more padding for larger items
                    box.setStyle("-fx-background-radius: 10; -fx-background-color: #E0E0E0;");

                    // Main message
                    Label message = new Label(item.getMessage());
                    message.setWrapText(true);
                    message.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #141652;");

                    // Time label
                    Label time = new Label(item.getTimeText());
                    time.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                    box.getChildren().addAll(message, time);

                    // Color code by type
                    String type = item.getType().toLowerCase();
                    switch (type) {
                        case "info" ->
                            box.setStyle("-fx-background-color: #D0E9FF; -fx-background-radius: 10;");
                        case "success" ->
                            box.setStyle("-fx-background-color: #D4FFD8; -fx-background-radius: 10;");
                        case "warning" ->
                            box.setStyle("-fx-background-color: #FFF4C2; -fx-background-radius: 10;");
                        case "expired" ->
                            box.setStyle("-fx-background-color: #FFD6D6; -fx-background-radius: 10;");
                        default ->
                            box.setStyle("-fx-background-color: #E0E0E0; -fx-background-radius: 10;");
                    }

                    setGraphic(box);
                }
            }
        });
    }

    

    private void updateDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy | hh:mm a");
        dateTimeLabel.setText(now.format(formatter));
    }

    // Optional: Quick action button methods if you want them clickable
    // You can leave them empty or print a message for now
    @FXML
    private void handleAddNewMedicine() {
        if (dashboardController != null) {
            dashboardController.handleManageItems();
        }
    }

    @FXML
    private void handleViewInventory() {
        if (dashboardController != null) {
            dashboardController.handleManageItems();
        }
    }

    @FXML
    private void handleNewRequest() {
        if (dashboardController != null) {
            dashboardController.handleRequestMedicine();
        }
    }

    private void handleSummaryClick(String usage) {
        if (dashboardController == null) {
            System.err.println("Dashboard Controller is not injected yet!");
            return;
        }
        switch (usage) {
            case "Medicines":
            case "Supplies":
            case "Expired Items":
                dashboardController.handleManageItems();
                break;
            case "Users":
                dashboardController.handleManageUsers();
                break;
            case "Requests":
                dashboardController.handleTransactions();
                break;
        }
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }

    public void setupNavigation() {
        totalMedicinesCard.setOnMouseClicked(e -> handleSummaryClick("Medicines"));
        lowSupplyCard.setOnMouseClicked(e -> handleSummaryClick("Supplies"));
        registeredUserCard.setOnMouseClicked(e -> handleSummaryClick("Users"));
        requestTodayCard.setOnMouseClicked(e -> handleSummaryClick("Requests"));
        expiredItemsCard.setOnMouseClicked(e -> handleSummaryClick("Expired Items"));
    }

    private void applyRoleBasedAccess() {
        String role = Session.getRole();
        if (role == null) {
            return;
        }

        role = role.toLowerCase();

        // Hide everything first
        totalMedicinesCard.setManaged(false);
        totalMedicinesCard.setVisible(false);
        lowSupplyCard.setManaged(false);
        lowSupplyCard.setVisible(false);
        registeredUserCard.setManaged(false);
        registeredUserCard.setVisible(false);
        requestTodayCard.setManaged(false);
        requestTodayCard.setVisible(false);
        expiredItemsCard.setManaged(false);
        expiredItemsCard.setVisible(false);

        btnAddNewMedicine.setManaged(false);
        btnAddNewMedicine.setVisible(false);
        btnViewInventory.setManaged(false);
        btnViewInventory.setVisible(false);
        btnNewRequest.setManaged(false);
        btnNewRequest.setVisible(false);

        switch (role) {
            case "director":
                totalMedicinesCard.setManaged(true);
                totalMedicinesCard.setVisible(true);
                lowSupplyCard.setManaged(true);
                lowSupplyCard.setVisible(true);
                registeredUserCard.setManaged(true);
                registeredUserCard.setVisible(true);
                requestTodayCard.setManaged(true);
                requestTodayCard.setVisible(true);
                expiredItemsCard.setManaged(true);
                expiredItemsCard.setVisible(true);

                btnAddNewMedicine.setManaged(true);
                btnAddNewMedicine.setVisible(true);
                btnViewInventory.setManaged(true);
                btnViewInventory.setVisible(true);
                btnNewRequest.setManaged(true);
                btnNewRequest.setVisible(true);
                break;

            case "admin":
                registeredUserCard.setManaged(true);
                registeredUserCard.setVisible(true);
                totalMedicinesCard.setManaged(true);
                totalMedicinesCard.setVisible(true);

                btnAddNewMedicine.setManaged(true);
                btnAddNewMedicine.setVisible(true);
                btnViewInventory.setManaged(true);
                btnViewInventory.setVisible(true);
                break;

            case "doctor":
                requestTodayCard.setManaged(true);
                requestTodayCard.setVisible(true);
                expiredItemsCard.setManaged(true);
                expiredItemsCard.setVisible(true);

                btnNewRequest.setManaged(true);
                btnNewRequest.setVisible(true);
                btnViewInventory.setManaged(true);
                btnViewInventory.setVisible(true);
                break;

            case "nurse":
                requestTodayCard.setManaged(true);
                requestTodayCard.setVisible(true);

                btnNewRequest.setManaged(true);
                btnNewRequest.setVisible(true);
                break;

            default:
                break;
        }
    }
}
