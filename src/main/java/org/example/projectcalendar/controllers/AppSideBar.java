package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import org.example.projectcalendar.Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class AppSideBar extends Controller implements Initializable {
    @FXML
    private Button calendarButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        calendarButton.getStyleClass().add("selected");
    }

    public void onCalenderClicked(MouseEvent mouseEvent) {
    }
}
