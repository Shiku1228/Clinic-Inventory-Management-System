package main;

import main.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/**
 *
 * @author Shiku
 */
public class ClinicInventoryManagementSystem extends Application {
    
    private static Stage stage;
    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        stage = primaryStage;
        Parent root = loadFXML("Dashboard");
        scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Clinic Inventory ManagementSystem");
        stage.setMaximized(true);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        Parent root = loadFXML(fxml);
        scene.setRoot(root);
        stage.sizeToScene(); // auto-adjust size
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(ClinicInventoryManagementSystem.class.getResource("/views/" + fxml + ".fxml"));
        return loader.load();
    }

    public static Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch();
    }
}
