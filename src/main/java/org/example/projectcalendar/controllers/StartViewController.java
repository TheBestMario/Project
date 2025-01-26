package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.MenuHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class StartViewController extends Controller implements Initializable {
    private MenuHandler menuHandler;

    @FXML
    public Node scenePane;

    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void onLoginButtonClick() {
        try {
            //animates and switches scenes after finishing animation
            System.out.println("before");
            menuHandler.addSceneToRoot("login-view.fxml");
            Node loginScene = menuHandler.getSceneFromRoot("login-view.fxml");
            this.scenePane = menuHandler.getSceneFromRoot("start-view.fxml");
            loginScene.setLayoutX(500);

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), scenePane);
            currentTransition.setFromX(0);
            currentTransition.setToX(-scenePane.getScene().getWidth());

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), loginScene);
            nextTransition.setFromX(loginScene.getLayoutX() + loginScene.getTranslateX());
            nextTransition.setToX(0);

            nextTransition.setOnFinished(event -> {
                System.out.println("hello");
                loginScene.setTranslateX(0);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition,nextTransition);
            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick() {
    }
}