package org.example.projectcalendar.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.model.CalendarEvent;
import org.example.projectcalendar.model.Profile;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class CalendarViewController extends Controller {
    private CalendarView calendarView;
    private MenuHandler menuHandler;
    private LocalDatabaseStorage localDB;
    private Profile userProfile;

    public CalendarView createCalendarView() {
        /*
        Uses calendarFX methods to create a calendar view
        Creates a calendar source and calendar
        loads events, and sets up listeners
        https://dlsc-software-consulting-gmbh.github.io/CalendarFX/#_entry
        https://dlsc.com/wp-content/html/calendarfx/apidocs/index.html
         */
        try {
            userProfile = Profile.getInstance();
            localDB = new LocalDatabaseStorage();

            Calendar calendar = new Calendar("My Calendar");
            calendar.setStyle(Calendar.Style.STYLE1); // Add some style
            CalendarSource calendarSource = new CalendarSource("My Calendars");
            calendarSource.getCalendars().add(calendar);
            calendarView = new CalendarView();
            calendarView.getCalendarSources().add(calendarSource);

            calendarView.showMonthPage();
            loadEvents(calendar);

            calendarView.setEntryDetailsCallback(param -> {
                showEventDialog((Entry<CalendarEvent>) param.getEntry(),calendar);
                return null;
            });

            return calendarView;

        } catch (Exception e) {
            System.out.println("Error creating calendar view: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void showEventDialog(Entry<CalendarEvent> entry, Calendar calendar) {
        /*
        converts entry to custom Event object
         */
        int calendarID = 1;

        CalendarEvent event = (CalendarEvent) entry.getUserObject();
        if (event == null) {
            event = new CalendarEvent(
                    calendarID,
                    entry.getTitle(),
                    "", // default description
                    entry.getStartAsLocalDateTime(),
                    entry.getEndAsLocalDateTime(),
                    entry.getLocation()
            );
            entry.setUserObject(event);
        }

        Dialog<CalendarEvent> dialog = new Dialog<>();
        dialog.setTitle(entry.getTitle().isEmpty() ? "Add New Event" : "Edit Event");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField title = new TextField(entry.getTitle());
        TextArea description = new TextArea(event.getDescription());
        TextField location = new TextField(entry.getLocation());

        grid.add(new Label("Title:"), 0, 0);
        grid.add(title, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(description, 1, 1);
        grid.add(new Label("Location:"), 0, 2);
        grid.add(location, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        CalendarEvent finalEvent = event;
        /*
        sets result after save button is clicked in dialogue
         */
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                entry.setTitle(title.getText());
                entry.setLocation(location.getText());

                finalEvent.setTitle(title.getText());
                finalEvent.setDescription(description.getText());
                finalEvent.setLocation(location.getText());
                finalEvent.setStartTime(entry.getStartAsLocalDateTime());
                finalEvent.setEndTime(entry.getEndAsLocalDateTime());

                localDB.saveEvent(
                        finalEvent.getTitle(),
                        finalEvent.getDescription(),
                        finalEvent.getStartTime(),
                        finalEvent.getEndTime(),
                        finalEvent.getLocation(),
                        finalEvent.getCalendarId()
                );

                if (!calendar.findEntries(entry.getTitle()).contains(entry)) {
                    calendar.addEntry(entry);
                }

                return finalEvent;
            }
            return null;
        });

        dialog.showAndWait();
    }


    private void loadEvents(Calendar calendar) {
        /*
        Only loads events for one calendar.
         */
        List<CalendarEvent> events = localDB.getEventsForCalendar(1);
        for (CalendarEvent event : events) {
            Entry<CalendarEvent> entry = new Entry<>(event.getTitle());
            entry.setInterval(event.getStartTime(), event.getEndTime());
            entry.setLocation(event.getLocation());
            calendar.addEntry(entry);
        }
    }

    private void deleteEvent(CalendarEvent event) {
        localDB.deleteEvent(event.getEventId());
    }

    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }
}
