package org.example.projectcalendar.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.model.Profile;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

public class TitleBarController extends Controller implements Initializable {
    public MenuItem newCalendarButton;
    public MenuItem newEventButton;
    @FXML
    private Label usernameLabel;
    private Profile profile;

    @FXML
    public MenuItem logoutButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        profile = Profile.getInstance();
        usernameLabel.setText(profile.getUserName());
    }

    @FXML
    public void onNewCalendarPressed(ActionEvent actionEvent) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("New Calendar");
        dialog.setHeaderText("Create a new calendar");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField calendarName = new TextField();
        calendarName.setPromptText("Calendar name");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(calendarName, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to a string when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                return calendarName.getText();
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            // Save calendar to database
            System.out.println(Profile.getInstance().getUserId());
            int calendarId = getLocalStorage().saveCalendar(name, Profile.getInstance().getUserId());
            
            // Update calendar view
            Content content = menuHandler.loadView("Initial/content.fxml");
            CalendarView calendarView = content.getCalendarView();
            CalendarSource source = calendarView.getCalendarSources().get(0);
            
            // Create and add new calendar
            Calendar newCalendar = new Calendar(name);
            newCalendar.setStyle(Calendar.Style.STYLE3);
            source.getCalendars().add(newCalendar);
        });
    }

    public void onNewEventPressed(ActionEvent actionEvent) {
        Dialog<Entry<String>> dialog = new Dialog<>();
        dialog.setTitle("New Event");
        dialog.setHeaderText("Create a new event");

        // Set the button types
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Create the form grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField();
        title.setPromptText("Event title");
        TextArea description = new TextArea();
        description.setPromptText("Description");
        TextField location = new TextField();
        location.setPromptText("Location");
        DatePicker startDate = new DatePicker(LocalDate.now());
        Spinner<Integer> startHour = new Spinner<>(0, 23, LocalTime.now().getHour());
        Spinner<Integer> startMinute = new Spinner<>(0, 59, LocalTime.now().getMinute());

        DatePicker endDate = new DatePicker(LocalDate.now());
        Spinner<Integer> endHour = new Spinner<>(0, 23, LocalTime.now().plusHours(1).getHour());
        Spinner<Integer> endMinute = new Spinner<>(0, 59, LocalTime.now().getMinute());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(location, 1, 2);
        grid.add(new Label("Start Date:"), 0, 3);
        grid.add(startDate, 1, 3);
        grid.add(startHour, 2, 3);
        grid.add(startMinute, 3, 3);
        grid.add(new Label("End Date:"), 0, 4);
        grid.add(endDate, 1, 4);
        grid.add(endHour, 2, 4);
        grid.add(endMinute, 3, 4);

        dialog.getDialogPane().setContent(grid);

        // Convert the result to an event when the save button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                Entry<String> entry = new Entry<>(title.getText());
                LocalDateTime startDateTime = LocalDateTime.of(startDate.getValue(), 
                    LocalTime.of(startHour.getValue(), startMinute.getValue()));
                LocalDateTime endDateTime = LocalDateTime.of(endDate.getValue(), 
                    LocalTime.of(endHour.getValue(), endMinute.getValue()));
                entry.setInterval(startDateTime, endDateTime);
                entry.setLocation(location.getText());
                return entry;
            }
            return null;
        });

        Optional<Entry<String>> result = dialog.showAndWait();
        result.ifPresent(entry -> {
            // Get the current calendar view and add the entry
            Content content = menuHandler.loadView("Initial/content.fxml");
            CalendarView calendarView = content.getCalendarView();
            Calendar calendar = calendarView.getCalendarSources().get(0).getCalendars().get(0);
            
            // Add to calendar - this will trigger the ENTRY_CREATED event handler
            // which will save to database, so we don't need to save here
            calendar.addEntry(entry);
        });
    }

    @FXML
    public void onLogoutPressed(ActionEvent actionEvent) {
        // Clear the profile
        Profile.getInstance().clearProfile();
        
        // Reset to initial login view using existing root
        StackPane root = new StackPane();
        root.setStyle("-fx-background-image: url('static/images/robert_walters_logo.jpeg')");
        root.setId("root");
        getMenuHandler().setRoot(root);
        getMenuHandler().addNodeToRoot("Initial/login-view.fxml");
        getMenuHandler().showView(root);
    }
}
