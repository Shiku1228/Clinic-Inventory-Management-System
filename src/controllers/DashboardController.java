package controllers;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.net.URL;
import java.util.ResourceBundle;
import controllers.DashboardContentController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Session;

public class DashboardController implements Initializable {

    @FXML
    private VBox sidePanel;
    @FXML
    private ImageView imageLogo;

    @FXML
    private Button btnDashboard;
    @FXML
    private Button btnRequestMedicine;
    @FXML
    private Button btnManageItems;
    @FXML
    private Button btnManageUsers;
    @FXML
    private Button btnTransactions;
    @FXML
    private Button btnLogout;

    @FXML
    private AnchorPane mainContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load logo
        Image logoImage = new Image(getClass().getResourceAsStream("/resource/images_icons/rmmcLogo.png"));
        imageLogo.setImage(logoImage);

        //rolebased access
        applyRoleBasedAccess();
        
        // Load default dashboard content
        handleDashboard();
    }

    @FXML
    private void handleDashboard() {
        setActiveButton(btnDashboard);
        loadView("/views/DashboardContent.fxml");
    }

    @FXML
    public void handleRequestMedicine() {
        setActiveButton(btnRequestMedicine);
        loadView("/views/RequestMedicine.fxml");
    }

    @FXML
    public void handleManageItems() {
        setActiveButton(btnManageItems);
        loadView("/views/ManageItems.fxml");
    }

    @FXML
    public void handleManageUsers() {
        setActiveButton(btnManageUsers);
        loadView("/views/ManageUsers.fxml");
    }

    @FXML
    public void handleTransactions() {
        setActiveButton(btnTransactions);
        loadView("/views/Transactions.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            // Get the current stage
            Stage stage = (Stage) btnLogout.getScene().getWindow();

            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Login.fxml"));
            Parent root = loader.load();

            // Create new scene with the login view
            Scene scene = new Scene(root);

            // Apply the current window size to the new scene
            double currentWidth = stage.getWidth();
            double currentHeight = stage.getHeight();

            // Set the new scene
            stage.setScene(scene);

            // Restore window size
            stage.setWidth(currentWidth);
            stage.setHeight(currentHeight);

            // Center the window on the screen
            stage.centerOnScreen();

        } catch (IOException e) {
            System.err.println("Failed to load login screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility method to load FXML into mainContent. Ensures the loaded
     * AnchorPane fills the entire mainContent.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();

            Object controller = loader.getController();

            //Inject DashboardController if DasboardContent is loaded
            if (controller instanceof DashboardContentController) {
                DashboardContentController dc = (DashboardContentController) controller;
                dc.setDashboardController(this);
                dc.setupNavigation();
            }

            // Clear previous content and set new content
            mainContent.getChildren().setAll(pane);

            // Anchor to all sides to fill the mainContent
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public void navigateFromDashboard(String target) {
        switch (target) {
            case "MANAGE_ITEMS":
                handleManageItems();
                break;
            case "MANAGE_USERS":
                handleManageUsers();
                break;
            case "TRANSACTIONS":
                handleTransactions();
                break;
            default:
                System.out.println("Unknown navigation target: " + target);
        }
    }

    private void setActiveButton(Button activeButton) {
        //remove active style from the rest of inactive buttons
        btnDashboard.getStyleClass().remove("nav-button-active");
        btnRequestMedicine.getStyleClass().remove("nav-button-active");
        btnManageItems.getStyleClass().remove("nav-button-active");
        btnManageUsers.getStyleClass().remove("nav-button-active");
        btnTransactions.getStyleClass().remove("nav-button-active");

        //add active style clicked button
        if (!activeButton.getStyleClass().contains("nav-button-active")) {
            activeButton.getStyleClass().add("nav-button-active");
        }
    }

    private void applyRoleBasedAccess() {
        String role = Session.getRole();
        if (role == null) {
            return; //this is the breakpoint
        }
        role = role.toLowerCase();

        //hide all buttons first (selective show only)
        btnManageUsers.setVisible(false);
        btnManageItems.setVisible(false);
        btnRequestMedicine.setVisible(false);
        btnTransactions.setVisible(false);

        switch (role) {
            case "director":
                //director sees everything
                btnManageUsers.setVisible(true);
                btnManageItems.setVisible(true);
                btnRequestMedicine.setVisible(true);
                btnTransactions.setVisible(true);
                break;

            case "admin":
                //users and items only
                btnManageUsers.setVisible(true);
                btnManageItems.setVisible(true);
                break;

            case "doctor":
                // Doctor sees request medicine and transactions
                btnRequestMedicine.setVisible(true);
                btnTransactions.setVisible(true);
                break;

            case "nurse":
                // Nurse sees request medicine only
                btnRequestMedicine.setVisible(true);
                break;
                
            default:
                //unknown role will be invalid.
                break;
        }
    }
}
