package org.example.projectcalendar.controllers;

import com.calendarfx.view.CalendarView;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.example.projectcalendar.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Content extends Controller implements Initializable {


    public AnchorPane anchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        CalendarView calendarView = new CalendarView();
        calendarView.showMonthPage();
        calendarView.setEnableTimeZoneSupport(true);
        anchorPane.getChildren().add(calendarView);
        AnchorPane.setTopAnchor(calendarView, 0.0);
        AnchorPane.setBottomAnchor(calendarView, 0.0);
        AnchorPane.setRightAnchor(calendarView, 0.0);
        AnchorPane.setLeftAnchor(calendarView, 0.0);


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
    public <T extends Node> void setView(T view, Controller controller) {
        anchorPane.getChildren().setAll(view);
        AnchorPane.setTopAnchor(view, 0.0);
        AnchorPane.setBottomAnchor(view, 0.0);
        AnchorPane.setRightAnchor(view, 0.0);
        AnchorPane.setLeftAnchor(view, 0.0);
    }

    @Override
    protected void onDependenciesSet() {

    }
}
