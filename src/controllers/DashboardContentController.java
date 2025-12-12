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

    public void initialize() {
        // Initialize date/time
        updateDateTime();
        Timeline timeline = new Timeline(new KeyFrame(Duration.minutes(1), e -> updateDateTime()));
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

        //dashboard cards clickable
        totalMedicinesCard.setOnMouseClicked(e -> handleSummaryClick("Medicines"));
        lowSupplyCard.setOnMouseClicked(e -> handleSummaryClick("Supplies"));
        registeredUserCard.setOnMouseClicked(e -> handleSummaryClick("Users"));
        requestTodayCard.setOnMouseClicked(e -> handleSummaryClick("Requests"));
        expiredItemsCard.setOnMouseClicked(e -> handleSummaryClick("Expired Items"));
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
        System.out.println("Quick Action: Add New Medicine clicked");
    }

    @FXML
    private void handleViewInventory() {
        System.out.println("Quick Action: View Inventory clicked");
    }

    @FXML
    private void handleNewRequest() {
        System.out.println("Quick Action: New Request clicked");
    }

    private void handleSummaryClick(String usage) {
        switch (usage) {
            case "Medicines":
                System.out.println("Total Medicine Card Clicked!");
                break;
            case "Supplies":
                System.out.println("Low Supply Card Clicked");
                break;
            case "Users":
                System.out.println("Total Users Card Clicked");
                break;
            case "Requests":
                System.out.println("Requests Today Card Clicked");
                break;
            case "Expired Items":
                System.out.println("Expired Items Card");
                break;
        }
    }
}
