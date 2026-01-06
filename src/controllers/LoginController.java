package controllers;

import com.mongodb.client.MongoDatabase;
import dao.UsersDAO;
import database.MongoDBConnection;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.bson.Document;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;

/**
 * FXML Controller class
 *
 * @author RENZ S. LATANGGA
 */
public class LoginController implements Initializable {

    @FXML
    private ImageView logoImage;
    @FXML
    private ImageView footerLogo;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private UsersDAO userDAO;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");

        // initialize DAO
        MongoDatabase db = MongoDBConnection.getDatabase(); // your DB connection
        userDAO = new UsersDAO(db);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();

        if (username.isEmpty()) {
            errorLabel.setText("Please enter your username.");
            return;
        }

        Document user = userDAO.authenticate(username);

        if (user == null) {
            errorLabel.setText("User not found.");
            return;
        }

        Session.setCurrentUser(user);

        try {
            // Load Dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/Dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            // Get the current stage (login stage)
            Stage currentStage = (Stage) usernameField.getScene().getWindow();

            // Remove minimum size constraints as they might interfere
            currentStage.setMinWidth(0);
            currentStage.setMinHeight(0);

            // Create a new scene with the dashboard content
            Scene dashboardScene = new Scene(dashboardRoot);

            // Set the new scene on the existing stage
            currentStage.setScene(dashboardScene);

            // Set stage properties
            currentStage.setTitle("Clinic Inventory Dashboard");

            // Get the screen dimensions
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            // Set the stage to full screen
            currentStage.setX(bounds.getMinX());
            currentStage.setY(bounds.getMinY());
            currentStage.setWidth(bounds.getWidth());
            currentStage.setHeight(bounds.getHeight());

            // Show the window
            currentStage.show();

            // Force layout and CSS application
            Platform.runLater(() -> {
                dashboardRoot.applyCss();
                dashboardRoot.layout();
                currentStage.setMaximized(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load dashboard");
        }
    }

    public class Session {

        private static Document currentUser;

        public static void setCurrentUser(Document user) {
            currentUser = user;
        }

        public static Document getCurrentUser() {
            return currentUser;
        }
    }
}
