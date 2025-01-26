package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.projectcalendar.CalendarApplication;
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
            Parent root = menuHandler.getCurrentScene().getRoot();
            FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource("login-view.fxml"));

            Parent loginViewRoot = loader.load();
            loginViewRoot.setTranslateX(root.getLayoutX() + root.getBoundsInParent().getWidth());

            ((Pane) root).getChildren().add(loginViewRoot);

            Animations.applyLeftTransition(root, loginViewRoot, 0, -root.getBoundsInParent().getWidth(), () -> {
                menuHandler.createScene("login-view.fxml", "Login");
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