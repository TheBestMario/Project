package org.example.projectcalendar.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.example.projectcalendar.Controller;

import com.calendarfx.view.CalendarView;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class Content extends Controller implements Initializable {

    @FXML
    public AnchorPane anchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showCalendarView();
    }

    public void setView(String fxmlPath, Controller controller) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setController(controller);
            Node view = loader.load();
            anchorPane.getChildren().setAll(view);
            AnchorPane.setTopAnchor(view, 0.0);
            AnchorPane.setBottomAnchor(view, 0.0);
            AnchorPane.setRightAnchor(view, 0.0);
            AnchorPane.setLeftAnchor(view, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showCalendarView() {
        CalendarViewController calendarController = new CalendarViewController();
        CalendarView calendarView = calendarController.createCalendarView();
        anchorPane.getChildren().setAll(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
    }

    public <T extends Node> void setView(T view, Controller controller) {
        anchorPane.getChildren().setAll(view);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
    }
}
