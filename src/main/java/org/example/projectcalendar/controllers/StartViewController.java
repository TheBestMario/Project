package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.animations.Animations;
import org.example.projectcalendar.service.MenuHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class StartViewController extends Controller implements Initializable{
    private MenuHandler menuHandler;
    @FXML
    public VBox rootPane;

    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
    @FXML
    protected void onLoginButtonClick() {
        Animations.applyLeftTransition(rootPane,0,-rootPane.getWidth(),() -> {
            try {
                menuHandler.setSceneOnStage("login-view.fxml", "Login");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    @FXML
    protected void onRegisterButtonClick() {}

}