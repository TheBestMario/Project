package org.example.projectcalendar.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.example.projectcalendar.Controller;

import com.calendarfx.view.CalendarView;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class Content extends Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Wait for dependencies
    }

    @Override
    public void onDependenciesSet() {
        showCalendarView();
    }

    public void showCalendarView() {
        CalendarViewController controller = new CalendarViewController();
        menuHandler.initializeController(controller);
        CalendarView view = controller.getView();
        setView(view);
    }

    private void setView(Node view) {
        anchorPane.getChildren().setAll(view);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
    }

    public CalendarView getCalendarView() {
        return ((CalendarView) anchorPane.getChildren().get(0));
    }
}
