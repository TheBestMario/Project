package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
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
    @FXML
    private Circle statusCircle;
    @FXML
    private Button manageServerButton;
    @FXML
    private Label informationLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
        Runs initialisation
        runs checks for saved credentials
        runs checks for connection status to inform user.
         */
        String savedUsername = CredentialStorage.getSavedUsername();
        String savedPassword = CredentialStorage.getSavedPassword();

        if (savedUsername != null && savedPassword != null) {
            usernameField.setText(savedUsername);
            passwordField.setText(savedPassword);
            rememberUserCheckBox.setSelected(true);
        }
    }
    @Override
    protected void onDependenciesSet() {
        updateStatus();
    }

    public void updateStatus(){
        System.out.println(getConnectionService().checkConnection());
        if (getConnectionService() == null){
            statusCircle.setStyle("-fx-fill: red");
        }else if (getConnectionService().checkConnection()){
            statusCircle.setStyle("-fx-fill: green");
        }else {
            statusCircle.setStyle("-fx-fill: red");
        }
    }

    @FXML
    protected void onLoginButtonClicked() {
        /*
        * Checks if the username is empty
        * checks if both fields are empty
        * If there is a connection to the server, then it checks credential validity against it
        * If there is no connection, then it checks credential validity against the local database
        * Depending on the same thing, once passwords match for the account then it will fetch
        * profile data from server or local storage
         */
        try {
            String usernameInput = usernameField.getText();
            String passwordInput = passwordField.getText();

            String salt;
            String storedPassword;
            if (getLocalStorage().getUsername(usernameInput) == null) {
                informationLabel.setText("Invalid username/email");
                informationLabel.setStyle("-fx-text-fill: red");
                return;
            } else if (usernameInput.isEmpty() || passwordInput.isEmpty()) {
                informationLabel.setText("Please fill in all the fields to login.");
                informationLabel.setStyle("-fx-text-fill: red");
                return;
            }

            if (getConnectionService().checkConnection()) {
                salt = getConnectionService().getSalt(usernameInput);
                storedPassword = getConnectionService().getHashedPassword(usernameInput);
            }
            else{
                salt = getLocalStorage().getSalt(usernameInput);
                storedPassword = getLocalStorage().getPassword(usernameInput);
            }

            String hashedPassword = HashUtils.hashPassword(passwordInput, salt);

            if (hashedPassword.equals(storedPassword)) {
                if (rememberUserCheckBox.isSelected()) {
                    CredentialStorage.saveCredentials(usernameInput, passwordInput);
                } else {
                    CredentialStorage.clearCredentials();
                }
                if (Profile.getInstance().getUserName() == null) {

                    if (getConnectionService().checkConnection()){
                        getConnectionService().sendLoginRequest(usernameInput, hashedPassword);
                        System.out.println("Fetching profile from server");
                    }else{
                        getLocalStorage().handleLoginOffline(usernameInput, hashedPassword);
                    }
                }

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
    @FXML
    public void onManageServerPressed(MouseEvent mouseEvent) {
        try{
            getMenuHandler().addNodeToRoot("Initial/manage-server-view.fxml");
            Node nextScene = getMenuHandler().getNodeFromRoot("manage-server-view");
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
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


