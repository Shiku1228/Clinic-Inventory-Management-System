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
    private Label userPhone;
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
        setupSummaryCards();
        setupUsersTable();
        setupSelectedUserCard();
        setupSearch();
    }

    private void setupSummaryCards() {
        //Update numnbers dynamically based on the User List
        long directors = userList.stream().filter(u -> u.getRole().equalsIgnoreCase("Director")).count();
        long doctors = userList.stream().filter(u -> u.getRole().equalsIgnoreCase("Doctor")).count();
        long nurses = userList.stream().filter(u -> u.getRole().equalsIgnoreCase("Nurse")).count();
        long admins = userList.stream().filter(u -> u.getRole().equalsIgnoreCase("Admin")).count();

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

        TableColumn<Users, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        TableColumn<Users, String> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button toggleButton = new Button("Deactivate");

            {
                editButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());
                    showSelectedUser(user);
                });
                toggleButton.setOnAction(event -> {
                    Users user = getTableView().getItems().get(getIndex());

                    if (user.getStatus().equalsIgnoreCase("Active")) {
                        user.setStatus("Inactive");
                    } else {
                        user.setStatus("Active");
                    }

                    usersTable.refresh();
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

                    toggleButton.setText(
                            user.getStatus().equalsIgnoreCase("Active") ? "Deactivate" : "Activate"
                    );

                    HBox box = new HBox(5, editButton, toggleButton);
                    setGraphic(box);;
                }
            }
        });

        usersTable.getColumns().setAll(idCol, nameCol, roleCol, statusCol, actionsCol);

        //Sample data items
        userList.addAll(
                new Users("1", "Renz Latangga", "Director", "Active", "/resource/avatars/renz_pfp.png"),
                new Users("2", "John Daniel Marañan", "Doctor", "Active", "/resource/avatars/janjan_pfp.png"),
                new Users("3", "John Christian Abelgas", "Nurse", "Active", "/resource/avatars/upaw_pfp.png"),
                new Users("4", "Merdin Harid", "Admin", "Active", "/resource/avatars/merdin_pfp.png"),
                new Users("5", "Krish Talino", "Admin", "Active", "/resource/avatars/krish_pfp.png")
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
                showSelectedUser(selected);
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
            System.out.println("The add user button was clicked!");
        });
    }

    private void showSelectedUser(Users user) {
        // Update user info
        userName.setText(user.getName());
        userClinic.setText("Clinic Inventory Management System");
        userEmail.setText(user.getName().toLowerCase().replace(" ", "") + "@email.com");
        userPhone.setText("09999999999");

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

                //if searc field is empty
                if (newValue == null || newValue.isBlank()) {
                    return true;
                }

                String searchKeyword = newValue.toLowerCase();

                //match name, role, status
                if (user.getName().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (user.getRole().toLowerCase().contains(searchKeyword)) {
                    return true;
                } else if (user.getStatus().toLowerCase().contains(searchKeyword)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<Users> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(usersTable.comparatorProperty());

        usersTable.setItems(sortedData);
    }
}
