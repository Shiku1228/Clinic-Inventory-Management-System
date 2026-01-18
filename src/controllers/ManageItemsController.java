package controllers;

import controllers.UpdateStockDialogController;
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
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import models.Items;
import dao.ItemsDAO;
import java.io.File;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Optional;
import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import utils.NotificationManager;
import javafx.stage.FileChooser;
import models.Notifications;
import utils.ExcelExporter;

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
    private ListView<Notifications> notificationsList;

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
        colStatus.setCellValueFactory(cellData -> {
            Items item = cellData.getValue();

            // sequence or hierarchal  Expired > Out Of Stock > Low Stock / Normal
            if (item.isExpired()) {
                return new javafx.beans.property.SimpleStringProperty("Expired");
            } else if (item.isOutOfStock()) {
                return new javafx.beans.property.SimpleStringProperty("Out Of Stock");
            } else if ((item.getCategory().equalsIgnoreCase("Equipment") && item.getStock() <= 3)
                    || ((item.getCategory().equalsIgnoreCase("Medicine") || item.getCategory().equalsIgnoreCase("Supplies"))
                    && item.getStock() <= 10)) {
                return new javafx.beans.property.SimpleStringProperty("Low Stock");
            } else if (item.getStatus().equalsIgnoreCase("Active")) {
                return new javafx.beans.property.SimpleStringProperty("Available");
            } else {
                return new javafx.beans.property.SimpleStringProperty(item.getStatus());
            }
        }
        );

        itemsTable.setFixedCellSize(
                28);
        itemsTable.prefHeightProperty()
                .bind(
                        itemsTable.fixedCellSizeProperty()
                                .multiply(Bindings.size(itemsTable.getItems()).add(1.2))
                );
        // Get default row height
        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        itemsTable.setPlaceholder(new Label("No items available"));

        // Sample Items
        ItemsDAO itemsDAO = new ItemsDAO();
        itemsData = itemsDAO.getAllItems();
        itemsTable.setItems(itemsData);

        autoDisableExpiredItems();

        //Set Table Values
        itemsTable.setItems(itemsData);
        itemsTable.setRowFactory(tv -> new TableRow<>() {

            @Override
            protected void updateItem(Items item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else if (item.isExpired()) {
                    // style in row for expired items 
                    setStyle(
                            "-fx-background-color: #8B0000;"
                            + // deep red
                            "-fx-text-fill: white;"
                    );
                } else if ((item.getCategory().equalsIgnoreCase("Equipment") && item.getStock() <= 3)
                        || ((item.getCategory().equalsIgnoreCase("Medicine") || item.getCategory().equalsIgnoreCase("Supplies"))
                        && item.getStock() <= 10)) {
                    // style in row for low stock items 
                    setStyle(
                            "-fx-background-color: #4B0082;"
                            + // deep purple (indigo)
                            "-fx-text-fill: white;"
                    );
                } else if (item.isOutOfStock()) {
                    // style in row for out of stock items 
                    setStyle(
                            "-fx-background-color: #0B0C31;"
                            + // deep purple (indigo)
                            "-fx-text-fill: white;"
                    );
                } else if (item.getStatus().equalsIgnoreCase("Disabled")) {
                    //faded lang pag disabled
                    setStyle("-fx-opacity: 0.6;");
                } else {
                    setStyle("");
                }
            }

        });

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
                -> handleAddItem());

        updateStockBtn.setOnAction(e -> {
            Items selected = itemsTable.getSelectionModel().getSelectedItem();

            if (selected == null) {
                showWarning("No Selection", "Please select an item first.");
                return;
            }

            if (selected.isExpired()) {
                showWarning(
                        "Expired Item",
                        "This item is already expired and cannot be updated."
                );
                return;
            }

            handleEdit(selected);
        });

        exportDataBtn.setOnAction(e
                -> handleExportExcel());

        removeExpiredBtn.setOnAction(e
                -> handleRemoveExpiredItems());

        //restriction for the remove expired items 
        removeExpiredBtn.disableProperty().bind(
                Bindings.createBooleanBinding(
                        () -> itemsData.stream().noneMatch(Items::isExpired),
                        itemsData
                )
        );

        //For the notifications
        //notificationsList.setItems(NotificationManager.getFeed());

        /*notificationsList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Notifications item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                setText(
                        "[" + item.getType() + "] "
                        + item.getMessage()
                        + " • "
                        + item.getTimeText()
                );

                // optional styling
                getStyleClass().removeAll("notif-success", "notif-warning", "notif-error");

                switch (item.getType()) {
                    case "SUCCESS" ->
                        getStyleClass().add("notif-success");
                    case "WARNING" ->
                        getStyleClass().add("notif-warning");
                    case "ERROR" ->
                        getStyleClass().add("notif-error");
                }
            }
        });
         */
        //Update Header Summary
        updateSummary();

        // Make summary cards clickable
        totalMedicinesCard.setOnMouseClicked(e -> handleSummaryClick("Medicine"));
        totalSuppliesCard.setOnMouseClicked(e -> handleSummaryClick("Supplies"));
        totalEquipmentsCard.setOnMouseClicked(e -> handleSummaryClick("Equipment"));
        lowStocksCard.setOnMouseClicked(e -> handleSummaryClick("LowStock"));

        ItemsDAO dao = new ItemsDAO();
        dao.generateItemNotifications(); // generate stock/expiry notifications

        NotificationManager.getFeed().addListener((javafx.collections.ListChangeListener.Change<? extends Notifications> c) -> {
            // do something if needed, e.g., update badge count
            lowStocksLabel.setText(String.valueOf(
                    NotificationManager.getFeed().stream().filter(n -> n.getType().equals("LOW_STOCK")).count()
            ));
        });
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
            ImageView img = new ImageView();
            img.setFitWidth(120);
            img.setFitHeight(120);
            img.setPreserveRatio(true);

            // Load image from filesystem if available, else use placeholder
            if (item.getImagePath() != null) {
                File imgFile = new File(item.getImagePath());
                if (imgFile.exists()) {
                    img.setImage(new Image(imgFile.toURI().toString()));
                } else {
                    // fallback placeholder
                    File placeholder = new File("src/resource/medImages/placeholder.png");
                    if (placeholder.exists()) {
                        img.setImage(new Image(placeholder.toURI().toString()));
                    }
                }
            } else {
                File placeholder = new File("src/resource/medImages/placeholder.png");
                if (placeholder.exists()) {
                    img.setImage(new Image(placeholder.toURI().toString()));
                }
            }

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

            Button disableBtn = new Button(
                    item.getStatus().equalsIgnoreCase("Disabled") ? "Enable" : "Disable"
            );

            disableBtn.getStyleClass().add("disable-btn");

            disableBtn.setOnAction(e -> handleDisableToggle(item));

            // Reset first (important when refreshing)
            card.getStyleClass().removeAll(
                    "expired-card",
                    "lowstock-card",
                    "out-of-stock-card",
                    "disabled-card"
            );
            card.setOpacity(1);

            // sequence toh siya for styling ng cards: Expired > Low Stock > Disabled
            if (item.getStatus().equalsIgnoreCase("Disabled")) {
                card.getStyleClass().add("disabled-card");
            }

            if (item.isExpired()) {
                card.getStyleClass().add("expired-card");
            } else if ((item.getCategory().equalsIgnoreCase("Equipment") && item.getStock() <= 3)
                    || ((item.getCategory().equalsIgnoreCase("Medicine") || item.getCategory().equalsIgnoreCase("Supplies"))
                    && item.getStock() <= 10)) {
                card.getStyleClass().add("lowstock-card");
            } else if (item.isOutOfStock()) {
                card.getStyleClass().add("out-of-stock-card");
            }

            if (item.isExpired()) {
                editBtn.setDisable(true);
                deleteBtn.setDisable(false);
                disableBtn.setDisable(true);
            }

            // ADD BUTTON ACTIONS (you can edit these)
            editBtn.setDisable(item.getStatus().equalsIgnoreCase("Disabled"));
            editBtn.setOnAction(e -> handleEdit(item));
            deleteBtn.setOnAction(e -> handleDelete(item, card));
            disableBtn.setOnAction(e -> handleDisableToggle(item));

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
        int lowStocks = (int) itemsData.stream()
                .filter(i
                        -> (i.getCategory().equalsIgnoreCase("Equipment") && i.getStock() <= 3)
                || ((i.getCategory().equalsIgnoreCase("Medicine") || i.getCategory().equalsIgnoreCase("Supplies"))
                && i.getStock() <= 10)
                )
                .filter(i -> !i.isExpired())
                .count();

        totalMedicinesLabel.setText(String.valueOf(totalMedicines));
        totalSuppliesLabel.setText(String.valueOf(totalSupplies));
        totalEquipmentsLabel.setText(String.valueOf(totalEquipments));
        lowStocksLabel.setText(String.valueOf(lowStocks));
    }

    private void handleEdit(Items item) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/UpdateStockDialog.fxml")
            );

            AnchorPane root = loader.load();

            //pass selected items sa dialog
            UpdateStockDialogController controller = loader.getController();
            controller.setItem(item);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit/Update Stock");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(addItemBtn.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            //refresh after update
            ItemsDAO dao = new ItemsDAO();
            itemsData = dao.getAllItems();
            itemsTable.setItems(itemsData);
            refreshGallery();

            NotificationManager.push(
                    "Stock updated: " + item.getItemName(),
                    "Admin updated the stock of this item",
                    "SUCCESS"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //de;ete items
    private void handleDelete(Items item, Node itemCard) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText(
                item.isExpired()
                ? "Delete expired item?"
                : "Are you sure you want to delete this item?"
        );
        alert.setContentText("This item will be deleted.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {

            try {
                ItemsDAO dao = new ItemsDAO();
                dao.deleteItem(item);

                //remove from the gallery 
                galleryPane.getChildren().remove(itemCard);

                NotificationManager.push(
                        "Item removed: " + item.getItemName(),
                        "Admin deleted the item",
                        "WARNING"
                );
                System.out.println("Removed Successfully");
            } catch (Exception ex) {
                ex.printStackTrace();

                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error");
                error.setHeaderText("Delete Failed");
                error.setContentText(ex.getMessage());
                error.showAndWait();
            }
        }
    }

    //diable ng mga item
    private void handleDisableToggle(Items item) {

        boolean isDisabled = item.getStatus().equalsIgnoreCase("Disabled");

        if (item.isExpired()) {
            showWarning(
                    "Expired Item",
                    "Expired items cannot be enabled or modified. You may only delete them."
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(isDisabled ? "Enable Items" : "Diable Items");
        alert.setHeaderText(isDisabled ? "Enable this item?" : "Are you sure you want to disable this item?");
        alert.setContentText(isDisabled ? "This item will be available again." : "This item will be disabled and connot be used until enabled again.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                ItemsDAO dao = new ItemsDAO();

                if (isDisabled) {
                    dao.enableItem(item);
                    item.setStatus("Active");
                } else {
                    dao.disableItem(item);
                    item.setStatus("Disabled");
                }

                itemsTable.refresh();
                refreshGallery();

                NotificationManager.push(
                        "Item disabled: " + item.getItemName(),
                        "Admin disabled this item",
                        "WARNING"
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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

    @FXML
    private void handleAddItem() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/AddItemDialog.fxml")
            );

            AnchorPane root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add New Item");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initOwner(addItemBtn.getScene().getWindow());

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.setResizable(false);

            dialogStage.showAndWait();

            NotificationManager.push(
                    "New item added successfully",
                    "Admin added a new item",
                    "SUCCESS"
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateStock() {
        Items selected = itemsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            handleEdit(selected); // reuse the same edit logic
        } else {
            System.out.println("Please select an item first!");
        }
    }

    private Image loadItemImage(String imagePath) {
        try {
            if (imagePath == null || imagePath.isBlank()) {
                return null;
            }
            var stream = getClass().getResource(imagePath);
            if (stream == null) {
                System.out.println("Image not found: " + imagePath);
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //for warnings/alerts
    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    //auto disable mga expired
    private void autoDisableExpiredItems() {
        ItemsDAO dao = new ItemsDAO();

        for (Items item : itemsData) {
            if (item.isExpired() && item.getStatus().equalsIgnoreCase("Active")) {
                try {
                    dao.disableItem(item);
                    item.setStatus("Disabled");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void handleRemoveExpiredItems() {

        //count expired Items first 
        long expiredCount = itemsData.stream()
                .filter(Items::isExpired)
                .count();

        if (expiredCount == 0) {
            showWarning(
                    "No expired items.",
                    "There are no expired items to remove"
            );
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Remove Expired Items");
        alert.setHeaderText("Remove all the expired items.");
        alert.setContentText(
                "This will permanently delete " + expiredCount + " expired item(s). \nThis action cannot be undone.");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            ItemsDAO dao = new ItemsDAO();
            long deleted = dao.deleteExpiredItems();

            if (deleted > 0) {
                NotificationManager.push(
                        "Expired items removed on " + LocalDate.now(),
                        "Admin removed " + deleted + " expired item(s) were permanently deleted",
                        "WARNING"
                );
            }

            //refreshdata
            itemsData = dao.getAllItems();
            itemsTable.setItems(itemsData);
            itemsTable.refresh();

            refreshGallery();
            updateSummary();

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Success");
            success.setHeaderText("Expired Items Removed");
            success.setContentText(deleted + " expired item(s) were removed.");
            success.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showWarning(
                    "Error",
                    "Failed to remove expired items."
            );
        }

    }

    private void handleExportExcel() {

        if (itemsData == null || itemsData.isEmpty()) {
            showWarning(
                    "No Data",
                    "There are no items to export"
            );
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Items Report");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel File (*.xlsk)", "*.xlsx")
        );
        fileChooser.setInitialFileName("Items_Report");

        File file = fileChooser.showSaveDialog(
                exportDataBtn.getScene().getWindow()
        );

        if (file == null) {
            return; //  user cancelled
        }

        try {
            //String logoPath = "C:/Users/RENZ S. LATANGGA/Documents/NetBeansProjects/Clinic-Inventory-Management-System-main/src/resource/images_icons/rmmcLogo.png"; // adjust to your logo path
            ExcelExporter.exportItems(itemsData, file.getAbsolutePath());

            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Export Successful");
            success.setHeaderText(null);
            success.setContentText(
                    "Items were successfully export to Excel. \n\n"
                    + "File: " + file.getName()
            );
            success.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();

            showWarning("Export Failed", "An error occured when doing thee exporting process");
        }

    }

}
