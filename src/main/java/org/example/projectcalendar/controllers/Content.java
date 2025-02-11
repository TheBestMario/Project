package org.example.projectcalendar.controllers;

import com.calendarfx.view.CalendarView;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import org.example.projectcalendar.Controller;
import java.net.URL;
import java.util.Calendar;
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
}
