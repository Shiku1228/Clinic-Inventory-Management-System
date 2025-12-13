package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import models.Items;

public class ManageItemsController implements Initializable {

    //Head Summary
    @FXML
    private Label totalMedicinesLabel;
    @FXML
    private Label totalSuppliesLabel;
    @FXML
    private Label totalEquipmentsLabel;
    @FXML
    private Label lowStocksLabel;

    //Search and Toggle
    @FXML
    private TextField searchField;
    @FXML
    private Button searchBtn;
    @FXML
    private ToggleGroup viewToggleGroup;
    @FXML
    private RadioButton tableViewToggle;
    @FXML
    private RadioButton galleryViewToggle;

    //Main Content
    @FXML
    private StackPane mainContent;
    @FXML
    private TableView<Items> itemsTable;
    @FXML
    private TilePane galleryPane;
    @FXML
    private ScrollPane galleryScroll;

    //Table Columns
    @FXML
    private TableColumn<Items, String> colItemId;
    @FXML
    private TableColumn<Items, String> colItemName;
    @FXML
    private TableColumn<Items, String> colCategory;
    @FXML
    private TableColumn<Items, Integer> colStock;
    @FXML
    private TableColumn<Items, String> colUnit;
    @FXML
    private TableColumn<Items, String> colExpirationDate;
    @FXML
    private TableColumn<Items, String> colSupplier;
    @FXML
    private TableColumn<Items, String> colStatus;

    //Action Buttons
    @FXML
    private Button addItemBtn;
    @FXML
    private Button updateStockBtn;
    @FXML
    private Button exportDataBtn;
    @FXML
    private Button removeExpiredBtn;

    //Notifications
    @FXML
    private ListView<String> notificationsList;

    //Sample Data
    private ObservableList<Items> itemsData = FXCollections.observableArrayList();

    //Sumamry Cards
    @FXML
    private VBox totalMedicinesCard;
    @FXML
    private VBox totalSuppliesCard;
    @FXML
    private VBox totalEquipmentsCard;
    @FXML
    private VBox lowStocksCard;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set Table Columns
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemID"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colExpirationDate.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Limit TableView to 5 rows dynamically
        int maxVisibleRows = 5;

        // Get default row height
        double rowHeight = 24; // default approximate row height
        if (!itemsTable.getItems().isEmpty()) {
            rowHeight = itemsTable.getFixedCellSize() > 0 ? itemsTable.getFixedCellSize() : 24;
        }

        // Calculate total preferred height: rows + header
        double headerHeight = 25; // approximate, JavaFX TableView header height
        itemsTable.setPrefHeight(headerHeight + rowHeight * maxVisibleRows);

        // Optional: you can set fixed cell size for more precise control
        itemsTable.setFixedCellSize(rowHeight);

        // Sample Items
        itemsData
                .addAll(
                        new Items("001", "Paracetamol", "Medicine", 50, "pcs", "2026-01-01", "Supplier A", "Available"),
                        new Items("002", "Syringe", "Supplies", 10, "pcs", "2025-12-01", "Supplier B", "Low Stock"),
                        new Items("003", "Stethoscope", "Equipment", 5, "pcs", "2027-06-01", "Supplier C", "Low Stock")
                );

        //Set Table Values
        itemsTable.setItems(itemsData);

        //Initialize gallery
        refreshGallery();

        // Initialize ToggleGroup in code
        viewToggleGroup = new ToggleGroup();
        tableViewToggle.setToggleGroup(viewToggleGroup);
        galleryViewToggle.setToggleGroup(viewToggleGroup);

        //toggle between table or gallery
        viewToggleGroup.selectedToggleProperty().addListener((obs, oldTogle, newToggle) -> {
            if (tableViewToggle.isSelected()) {
                itemsTable.setVisible(true);
                galleryScroll.setVisible(false);
            } else {
                itemsTable.setVisible(false);
                galleryScroll.setVisible(true);
            }
        });

        //Search functionality
        searchBtn.setOnAction(e
                -> filterItems());

        //Action Button Placeholders
        addItemBtn.setOnAction(e
                -> System.out.println("Add New Item clicked"));
        updateStockBtn.setOnAction(e
                -> System.out.println("Update Stock clicked"));
        exportDataBtn.setOnAction(e
                -> System.out.println("Export Data clicked"));
        removeExpiredBtn.setOnAction(e
                -> System.out.println("Remove Expired Items clicked"));

        //Sample Notifications
        notificationsList.getItems()
                .addAll(
                        "Paracetamol stock is low",
                        "Syringe expiring soon",
                        "Stethoscope stock is low"
                );
        // Remove blank or empty notifications
        notificationsList.getItems().removeIf(item -> item == null || item.trim().isEmpty());

        //Update Header Summary
        updateSummary();

        // Make summary cards clickable
        totalMedicinesCard.setOnMouseClicked(e -> handleSummaryClick("Medicine"));
        totalSuppliesCard.setOnMouseClicked(e -> handleSummaryClick("Supplies"));
        totalEquipmentsCard.setOnMouseClicked(e -> handleSummaryClick("Equipment"));
        lowStocksCard.setOnMouseClicked(e -> handleSummaryClick("LowStock"));

    }

    private void filterItems() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            itemsTable.setItems(itemsData);
        } else {
            ObservableList<Items> filtered = FXCollections.observableArrayList();
            for (Items item : itemsData) {
                if (item.getItemName().toLowerCase().contains(keyword)
                        || item.getCategory().toLowerCase().contains(keyword)) {
                    filtered.add(item);
                }
            }
            itemsTable.setItems(filtered);
        }
    }

    private void refreshGallery() {
        galleryPane.getChildren().clear();

        for (Items item : itemsData) {

            // CARD CONTAINER
            VBox card = new VBox(10);
            card.getStyleClass().add("gallery-card");

            // IMAGE
            String imagePath = "src/main/resources/resource/images_icons/medicine.png"; // default
            ImageView img = new ImageView(new Image("file:" + imagePath));
            img.getStyleClass().add("item-image");

            // NAME
            Label name = new Label(item.getItemName());
            name.getStyleClass().add("item-name");

            // CATEGORY
            Label category = new Label(item.getCategory());
            category.getStyleClass().add("item-category");

            // BUTTONS (HBox)
            HBox btnRow = new HBox(8);
            btnRow.getStyleClass().add("card-btn-row");

            Button editBtn = new Button("Edit");
            editBtn.getStyleClass().add("edit-btn");

            Button deleteBtn = new Button("Delete");
            deleteBtn.getStyleClass().add("delete-btn");

            Button disableBtn = new Button("Disable");
            disableBtn.getStyleClass().add("disable-btn");

            // ADD BUTTON ACTIONS (you can edit these)
            editBtn.setOnAction(e -> handleEdit(item));
            deleteBtn.setOnAction(e -> handleDelete(item));
            disableBtn.setOnAction(e -> handleDisable(item));

            btnRow.getChildren().addAll(editBtn, deleteBtn, disableBtn);

            // FINAL CARD ASSEMBLY
            card.getChildren().addAll(img, name, category, btnRow);
            galleryPane.getChildren().add(card);
        }
    }

    private void updateSummary() {
        int totalMedicines = (int) itemsData.stream().filter(i -> i.getCategory().equalsIgnoreCase("Medicine")).count();
        int totalSupplies = (int) itemsData.stream().filter(i -> i.getCategory().equalsIgnoreCase("Supplies")).count();
        int totalEquipments = (int) itemsData.stream().filter(i -> i.getCategory().equalsIgnoreCase("Equipment")).count();
        int lowStocks = (int) itemsData.stream().filter(i -> i.getStock() < 10).count();

        totalMedicinesLabel.setText(String.valueOf(totalMedicines));
        totalSuppliesLabel.setText(String.valueOf(totalSupplies));
        totalEquipmentsLabel.setText(String.valueOf(totalEquipments));
        lowStocksLabel.setText(String.valueOf(lowStocks));
    }

    private void handleEdit(Items item) {
        System.out.println("Edit clicked for: " + item.getItemName());
        // TODO: open edit dialog
    }

    private void handleDelete(Items item) {
        System.out.println("Delete clicked for: " + item.getItemName());
        // TODO: delete confirmation
    }

    private void handleDisable(Items item) {
        System.out.println("Disable clicked for: " + item.getItemName());
        // TODO: change status to disabled
    }

    private void handleSummaryClick(String category) {
        switch (category) {
            case "Medicine":
                System.out.println("Total Medicines clicked!");
                break;
            case "Supplies":
                System.out.println("Total Supplies clicked!");
                break;
            case "Equipment":
                System.out.println("Total Equipments clicked!");
                break;
            case "LowStock":
                System.out.println("Low Stocks clicked!");
                break;
        }
    }

}
