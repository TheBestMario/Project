package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.MenuHandler;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class StartViewController extends Controller implements Initializable {

    @FXML
    public Node scenePane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    protected void onLoginButtonClick() {
        try {
            //animates and switches scenes after finishing animation
            getMenuHandler().addNodeToRoot("login-view.fxml");
            Node loginScene = getMenuHandler().getNodeFromRoot("login-view.fxml");
            this.scenePane = getMenuHandler().getNodeFromRoot("start-view.fxml");
            loginScene.setLayoutX(500);

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), scenePane);
            currentTransition.setFromX(0);
            currentTransition.setToX(-scenePane.getBoundsInParent().getWidth());

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), loginScene);
            nextTransition.setFromX(scenePane.getBoundsInParent().getWidth() + loginScene.getTranslateX());
            nextTransition.setToX(0);

            nextTransition.setOnFinished(event -> {
                getMenuHandler().getRoot().getChildren().remove(scenePane);
                loginScene.setTranslateX(0);
                System.out.println(getMenuHandler());
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
        try {
            //animates and switches scenes after finishing animation
            getMenuHandler().addNodeToRoot("register-view.fxml");
            Node registerScene = getMenuHandler().getNodeFromRoot("register-view.fxml");
            this.scenePane = getMenuHandler().getNodeFromRoot("start-view.fxml");
            registerScene.setLayoutX(500);

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), scenePane);
            currentTransition.setFromX(0);
            currentTransition.setToX(-scenePane.getBoundsInParent().getWidth());

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), registerScene);
            nextTransition.setFromX(scenePane.getBoundsInParent().getWidth() + registerScene.getTranslateX());
            nextTransition.setToX(0);


            nextTransition.setOnFinished(event -> {
                getMenuHandler().getRoot().getChildren().remove(scenePane);
                registerScene.setTranslateX(0);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition,nextTransition);
            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}