
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 *
 * @author RENZ S. LATANGGA
 */
public class LoginController implements Initializable {

    @FXML private ImageView logoImage;
    @FXML private ImageView footerLogo;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        errorLabel.setText("");
    }    
    
    @FXML
    private void handleLogin(){
        System.out.print("Login Clicked!");
    }
    
}
