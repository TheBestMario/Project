package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.Database;
import org.example.projectcalendar.service.HashUtils;

import javax.crypto.SecretKey;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController extends Controller implements Initializable {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button backButton;
    @FXML
    private Node rootPane;

    Database database = Database.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //initialises database (reconnects each time)
        database = Database.getInstance();

    }

    @FXML
    protected void onLoginButtonClicked(){
        try {
            String usernameInput = usernameField.getText();
            String passwordInput = passwordField.getText();

            String salt = database.getSalt(usernameInput);
            String storedPassword = database.getHashedPassword(usernameInput);

            String hashedPassword = HashUtils.hashPassword(passwordInput, salt);

            if (hashedPassword.equals(storedPassword)) {
                System.out.println("Login successful");
                getMenuHandler().switchToCalendarMenu();
                // Proceed with login
            } else {
                System.out.println("Invalid username/email or password.");
                //informationLabel.setText("Invalid username/email or password.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onSignUpLinkClicked(){
        //animates and switches scenes after finishing animation
        try {
            getMenuHandler().addNodeToRoot("Initial/register-view.fxml");
            Node registerScene = getMenuHandler().getNodeFromRoot("register-view");
            this.rootPane = getMenuHandler().getNodeFromRoot("login-view");
            registerScene.setLayoutX(500);

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), rootPane);
            currentTransition.setFromX(0);
            currentTransition.setToX(-rootPane.getBoundsInParent().getWidth());

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), registerScene);
            nextTransition.setFromX(rootPane.getBoundsInParent().getWidth() + registerScene.getTranslateX());
            nextTransition.setToX(0);


            nextTransition.setOnFinished(event -> {
                ((StackPane) getMenuHandler().getRoot()).getChildren().remove(rootPane);
                registerScene.setTranslateX(0);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition, nextTransition);
            parallelTransition.play();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @FXML
    protected void onBackButtonClicked(){
        try {
            //animates and switches scenes after finishing animation
            getMenuHandler().addNodeToRoot("Initial/start-view.fxml");
            Node startViewScene = getMenuHandler().getNodeFromRoot("start-view");
            this.rootPane = getMenuHandler().getNodeFromRoot("login-view");
            startViewScene.setLayoutX(-rootPane.getLayoutX());

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), startViewScene);
            currentTransition.setFromX(-rootPane.getBoundsInParent().getWidth());
            currentTransition.setToX(0);

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), rootPane);
            nextTransition.setFromX(0);
            nextTransition.setToX(rootPane.getBoundsInParent().getWidth() + startViewScene.getTranslateX());
            StackPane root = (StackPane) getMenuHandler().getRoot();
            nextTransition.setOnFinished(event -> {
                root.getChildren().remove(rootPane);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition,nextTransition);
            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


