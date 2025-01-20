package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.MenuHandler;
import org.example.projectcalendar.service.MenuInterface;

import java.net.URL;
import java.util.ResourceBundle;

public class loginViewController extends Controller implements Initializable {
    private MenuHandler menuHandler;

    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
