package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.animations.Animations;
import org.example.projectcalendar.service.MenuHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class StartViewController extends Controller implements Initializable {
    private MenuHandler menuHandler;
    @FXML
    public VBox backgroundPane;

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
            Parent root = menuHandler.getScene().getRoot();
            menuHandler.setScene("login-view.fxml", "Login");
            Parent loginViewRoot = this.getRoot();
            loginViewRoot.setTranslateX(root.getLayoutX() + root.getBoundsInParent().getWidth());
            Animations.applyLeftTransition(root, loginViewRoot, 0, -root.getBoundsInParent().getWidth(), () -> {
                menuHandler.setStageScene();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick() {

    }
}