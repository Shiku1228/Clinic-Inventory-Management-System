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
                new Users("1", "Renz Latangga", "Director", "Active"),
                new Users("2", "John Daniel Marañan", "Doctor", "Active"),
                new Users("3", "Krish Talino", "Nurse", "Active"),
                new Users("4", "Merdin Harid", "Admin", "Active")
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
        userName.setText(user.getName());
        userClinic.setText("Clinic Inventory Management System"); //static for now
        userEmail.setText(user.getName().toLowerCase().replace(" ", "") + "@email.com"); // dummy email
        userPhone.setText("09999999999999");

        /**
         * Avatar is static: you can replace based ont he role that you want.
         * /Example: userAvatar.setImage.(new Image("/path/to/image.png)*
         */
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
