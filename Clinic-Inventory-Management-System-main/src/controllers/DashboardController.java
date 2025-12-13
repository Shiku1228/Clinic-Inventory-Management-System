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

public class DashboardController implements Initializable {

    @FXML private VBox sidePanel;
    @FXML private ImageView imageLogo;

    @FXML private Button btnDashboard;
    @FXML private Button btnRequestMedicine;
    @FXML private Button btnManageItems;
    @FXML private Button btnManageUsers;
    @FXML private Button btnLogout;

    @FXML private AnchorPane mainContent;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Load logo
        Image logoImage = new Image(getClass().getResourceAsStream("/resource/rmmcLogo.png"));
        imageLogo.setImage(logoImage);

        // Load default dashboard content
        handleDashboard();
    }

    @FXML
    private void handleDashboard() {
        loadView("/views/DashboardContent.fxml");
    }

    @FXML
    private void handleRequestMedicine() {
        loadView("/views/RequestMedicine.fxml");
    }

    @FXML
    private void handleManageItems() {
        loadView("/views/ManageItems.fxml");
    }

    @FXML
    private void handleManageUsers() {
        loadView("/views/ManageUsers.fxml");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
        // Implement logout logic here, e.g., return to login screen
        System.exit(0);
    }

    /**
     * Utility method to load FXML into mainContent.
     * Ensures the loaded AnchorPane fills the entire mainContent.
     */
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            AnchorPane pane = loader.load();

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
}
