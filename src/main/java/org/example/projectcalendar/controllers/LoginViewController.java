package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.MenuHandler;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void onLoginButtonClicked(){
        try {
            System.out.println("switches to calendar view");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onBackButtonClicked(){
        try {
            //animates and switches scenes after finishing animation
            getMenuHandler().addNodeToRoot("start-view.fxml");
            Node startViewScene = getMenuHandler().getNodeFromRoot("start-view.fxml");
            this.rootPane = getMenuHandler().getNodeFromRoot("login-view.fxml");
            startViewScene.setLayoutX(-rootPane.getLayoutX());

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), startViewScene);
            currentTransition.setFromX(-rootPane.getBoundsInParent().getWidth());
            currentTransition.setToX(0);

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), rootPane);
            nextTransition.setFromX(0);
            nextTransition.setToX(rootPane.getBoundsInParent().getWidth() + startViewScene.getTranslateX());

            nextTransition.setOnFinished(event -> {
                getMenuHandler().getRoot().getChildren().remove(rootPane);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition,nextTransition);
            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}


