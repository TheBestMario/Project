package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.CredentialStorage;
import org.example.projectcalendar.service.HashUtils;
import org.example.projectcalendar.service.User.Profile;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController extends Controller implements Initializable {
    public CheckBox rememberUserCheckBox;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //checks if user has saved credentials and fills in the fields
        String savedUsername = CredentialStorage.getSavedUsername();
        String savedPassword = CredentialStorage.getSavedPassword();

        if (savedUsername != null && savedPassword != null) {
            usernameField.setText(savedUsername);
            passwordField.setText(savedPassword);
            rememberUserCheckBox.setSelected(true);
        }
    }

    @FXML
    protected void onLoginButtonClicked() {
        try {
            String usernameInput = usernameField.getText();
            String passwordInput = passwordField.getText();

            String salt;
            String storedPassword;
            if (getConnectionService().checkConnection()) {
                salt = getConnectionService().getSalt(usernameInput);
                storedPassword = getConnectionService().getHashedPassword(usernameInput);
            }
            else{
                salt = getMenuHandler().getLocalDB().getSalt(usernameInput);
                storedPassword = getMenuHandler().getLocalDB().getPassword(usernameInput);
            }

            String hashedPassword = HashUtils.hashPassword(passwordInput, salt);

            if (hashedPassword.equals(storedPassword)) {
                if (rememberUserCheckBox.isSelected()) {
                    CredentialStorage.saveCredentials(usernameInput, passwordInput);
                } else {
                    CredentialStorage.clearCredentials();
                }
                getConnectionService().sendLoginRequest(usernameInput, storedPassword);
                System.out.println(Profile.getInstance().getUserName());
                System.out.println("Login successful.");
                getMenuHandler().switchToCalendarMenu();
            } else {
                System.out.println("Invalid username/email or password.");
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
            Node nextScene = getMenuHandler().getNodeFromRoot("register-view");
            this.rootPane = getMenuHandler().getNodeFromRoot("login-view");
            nextScene.setLayoutX(getMenuHandler().getPrimaryStage().getWidth());

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), rootPane);
            currentTransition.setFromX(0);
            currentTransition.setToX(-getMenuHandler().getPrimaryStage().getWidth());

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), nextScene);
            nextTransition.setFromX(getMenuHandler().getPrimaryStage().getWidth());
            nextTransition.setToX(0);


            nextTransition.setOnFinished(event -> {
                ((StackPane) getMenuHandler().getRoot()).getChildren().remove(rootPane);
                nextScene.setTranslateX(0);
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


