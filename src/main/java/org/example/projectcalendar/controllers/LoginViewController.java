package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.MenuHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginViewController extends Controller implements Initializable {
    private MenuHandler menuHandler;

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("LoginViewController initialized");
    }
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;

    }

    @FXML
    protected void onLoginButtonClicked(){
        try {
            menuHandler.createScene("calendar-view.fxml", "Calendar");
            menuHandler.setStageScene();
        } catch (Exception e) {
            e.printStackTrace();
        }
}   }
