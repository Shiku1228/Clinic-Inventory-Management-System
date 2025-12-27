package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.shape.Circle;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import models.Users;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.application.Platform;

public class ManageUsersController implements Initializable {

    //Summary Cards
    @FXML
    private VBox directorCard;
    @FXML
    private VBox doctorCard;
    @FXML
    private VBox nurseCard;
    @FXML
    private VBox adminCard;

    //Search Field
    @FXML
    private TextField searchField;

    //Selected User Card
    @FXML
    private StackPane avatarWrapper;
    @FXML
    private Circle avatarBorder;
    @FXML
    private ImageView userAvatar;
    @FXML
    private Label userName;
    @FXML
    private Label userClinic;
    @FXML
    private Label userEmail;
    @FXML
    private Label userContact;
    @FXML
    private Button editButton;
    @FXML
    private Button deactivateButton;

    //User Table    
    @FXML
    TableView<Users> usersTable;
    @FXML
    Button addUserButton;

    //Data
    private ObservableList<Users> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUsersTable();
        setupSearch();
        setupSelectedUserCard();
        setupSummaryCards();
    }

    private void setupSummaryCards() {
        ObservableList<Users> visibleUsers = usersTable.getItems();
        //Update numnbers dynamically based on the User List
        long directors = visibleUsers.stream().filter(u -> u.getRole().equalsIgnoreCase("Director")).count();
        long doctors = visibleUsers.stream().filter(u -> u.getRole().equalsIgnoreCase("Doctor")).count();
        long nurses = visibleUsers.stream().filter(u -> u.getRole().equalsIgnoreCase("Nurse")).count();
        long admins = visibleUsers.stream().filter(u -> u.getRole().equalsIgnoreCase("Admin")).count();

        ((Label) directorCard.getChildren().get(0)).setText(String.valueOf(directors));
        ((Label) doctorCard.getChildren().get(0)).setText(String.valueOf(doctors));
        ((Label) nurseCard.getChildren().get(0)).setText(String.valueOf(nurses));
        ((Label) adminCard.getChildren().get(0)).setText(String.valueOf(admins));
    }

    private void setupUsersTable() {
        //Columns of the Table
        TableColumn<Users, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().idProperty());

        TableColumn<Users, String> nameCol = new TableColumn<>("User Name");
        nameCol.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        TableColumn<Users, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> cellData.getValue().roleProperty());

        TableColumn<Users, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(cellData -> cellData.getValue().contactProperty());

        TableColumn<Users, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

        TableColumn<Users, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        TableColumn<Users, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button toggleButton = new Button("Deactivate");

            {
                editButton.getStyleClass().add("table-edit-btn");

                editButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    openEditUserDialog(user);
                });

                toggleButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());

                    if (user.getStatus().equalsIgnoreCase("Active")) {
                        user.setStatus("Inactive");
                    } else {
                        user.setStatus("Active");
                    }

                    updateToggleButton(user);
                    usersTable.refresh();
                    usersTable.getSelectionModel().select(user);
                    setupSummaryCards();
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Users user = getTableView().getItems().get(getIndex());

                    updateToggleButton(user);
                    HBox box = new HBox(6, editButton, toggleButton);
                    setGraphic(box);;
                }
            }
            
            //for styling the toggleButton
            private void updateToggleButton(Users user) {
                toggleButton.getStyleClass().removeAll(
                        "table-toggle-active",
                        "table-toggle-inactive"
                );

                if (user.getStatus().equalsIgnoreCase("Active")) {
                    toggleButton.setText("Deactivate");
                    toggleButton.getStyleClass().add("table-toggle-active");
                } else {
                    toggleButton.setText("Activate");
                    toggleButton.getStyleClass().add("table-toggle-inactive");
                }
            }

        });

        usersTable.getColumns().setAll(idCol, nameCol, roleCol, contactCol, emailCol, statusCol, actionsCol);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //Sample data items
        userList.addAll(
                new Users(
                        "1",
                        "Renz Latangga",
                        "Director",
                        "09999999999",
                        "renz@email.com",
                        "Active",
                        "/resource/avatars/renz_pfp.png"
                ),
                new Users(
                        "2",
                        "John Daniel Marañan",
                        "Doctor",
                        "09998887777",
                        "janjan@email.com",
                        "Active",
                        "/resource/avatars/janjan_pfp.png"
                ),
                new Users(
                        "3",
                        "John Christian Abelgas",
                        "Nurse",
                        "09997776666",
                        "upaw@email.com",
                        "Active",
                        "/resource/avatars/upaw_pfp.png"
                ),
                new Users(
                        "4",
                        "Merdin Harid",
                        "Admin",
                        "09996665555",
                        "merdin@email.com",
                        "Active",
                        "/resource/avatars/merdin_pfp.png"
                ),
                new Users(
                        "5",
                        "Krish Talino",
                        "Admin",
                        "09995554444",
                        "krish@email.com",
                        "Active",
                        "/resource/avatars/krish_pfp.png"
                )
        );

        usersTable.setItems(userList);

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showSelectedUser(newSelection);
                    }
                });
    }

    private void setupSelectedUserCard() {
        //Edit button (in the card itself)
        editButton.setOnAction(e -> {
            Users selected = usersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                openEditUserDialog(selected);
            }
        });

        deactivateButton.setOnAction(e -> {
            Users selected = usersTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                selected.setStatus("Inactive");
                usersTable.refresh();
                setupSummaryCards();
            }
        });

        //add user button
        addUserButton.setOnAction(e -> {
            openAddUserDialog();
        });
    }

    private void showSelectedUser(Users user) {
        // Update user info
        userName.setText(user.getName());
        userClinic.setText("Clinic Inventory Management System");
        userEmail.setText(user.getEmail());
        userContact.setText(user.getContact());

        // Load the user's own avatar image from their userList
        Image avatar;
        try {
            avatar = new Image(getClass().getResourceAsStream(user.getAvatarUrl()));
        } catch (Exception e) {
            // Fallback if image not found
            avatar = new Image(getClass().getResourceAsStream("/resource/avatars/user.png"));
        }

        // Determine border color based on role
        Color borderColor;
        switch (user.getRole().toLowerCase()) {
            case "director":
                borderColor = Color.web("#FFD700"); // Gold
                break;
            case "doctor":
                borderColor = Color.web("#4CAF50"); // Green
                break;
            case "nurse":
                borderColor = Color.web("#2196F3"); // Blue
                break;
            case "admin":
                borderColor = Color.web("#9C27B0"); // Purple
                break;
            default:
                borderColor = Color.GRAY;
        }

        // Apply circular avatar with role-based border
        applyCircularAvatar(avatar, borderColor);
    }

    //for making the avatar circular
    private void applyCircularAvatar(Image image, Color borderColor) {
        double size = 120;
        double radius = size / 2;

        // Center crop for square
        double min = Math.min(image.getWidth(), image.getHeight());
        Rectangle2D viewport = new Rectangle2D(
                (image.getWidth() - min) / 2,
                (image.getHeight() - min) / 2,
                min,
                min
        );

        userAvatar.setImage(image);
        userAvatar.setViewport(viewport);
        userAvatar.setFitWidth(size);
        userAvatar.setFitHeight(size);
        userAvatar.setPreserveRatio(false);

        // Circular clip
        Circle clip = new Circle(radius, radius, radius);
        userAvatar.setClip(clip);

        // Role-based border
        Circle border = new Circle(radius, radius, radius);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(borderColor);
        border.setStrokeWidth(4);

        // Wrap avatar and border in the StackPane
        StackPane wrapper = (StackPane) userAvatar.getParent();
        wrapper.getChildren().setAll(border, userAvatar);
    }

    private void setupSearch() {
        FilteredList<Users> filteredData = new FilteredList<>(userList, b -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {

                //if search field is empty
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                //match name, role, status
                return user.getName().toLowerCase().contains(searchKeyword)
                        || user.getRole().toLowerCase().contains(searchKeyword)
                        || user.getStatus().toLowerCase().contains(searchKeyword);
            });
            
            setupSummaryCards();
        });

        SortedList<Users> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(usersTable.comparatorProperty());

        usersTable.setItems(sortedData);

        if (!sortedData.isEmpty()) {
            usersTable.getSelectionModel().selectFirst();
        }

        Platform.runLater(() -> {
            if (!usersTable.getItems().isEmpty()) {
                usersTable.getSelectionModel().selectFirst();
            }
        });

    }

    private void openAddUserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/AddUserDialog.fxml")
            );

            Scene scene = new Scene(loader.load());

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Add User");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.setScene(scene);

            //get the controller instance
            AddUserDialogController controller = loader.getController();

            dialogStage.showAndWait();

            Users newUser = controller.getCreatedUser();
            if (newUser != null) {
                userList.add(newUser);
                setupSummaryCards();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void openEditUserDialog(Users user){
        try{
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/views/EditUserDialog.fxml")
            );
            
            Scene scene = new Scene(loader.load());
            
            Stage stage = new Stage();
            stage.setTitle("Edit User");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.setScene(scene);
            
            EditUserDialogController controller = loader.getController();
            controller.setUser(user);
            
            stage.showAndWait();
            
            
            usersTable.refresh();
            setupSummaryCards();
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
