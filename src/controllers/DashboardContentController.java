package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.layout.VBox;

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
        welcomeLabel.setText("Welcome Back, Nurse!");
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
}
