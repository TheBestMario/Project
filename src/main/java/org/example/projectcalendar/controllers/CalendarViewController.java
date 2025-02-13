package org.example.projectcalendar.controllers;

import java.time.LocalDateTime;
import java.util.List;

import javafx.event.Event;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.model.CalendarEvent;
import org.example.projectcalendar.model.Profile;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

public class CalendarViewController extends Controller {
    private CalendarView calendarView;
    private MenuHandler menuHandler;
    private LocalDatabaseStorage localDB;
    private Profile userProfile;

    public CalendarView createCalendarView() {
        try {
            userProfile = Profile.getInstance();
            localDB = new LocalDatabaseStorage();
            
            Calendar calendar = new Calendar("My Calendar");
            calendar.setStyle(Calendar.Style.STYLE3);
            CalendarSource calendarSource = new CalendarSource("My Calendars");
            calendarSource.getCalendars().add(calendar);
            calendarView = new CalendarView();
            calendarView.getCalendarSources().add(calendarSource);
            calendarView.showMonthPage();
            // Load existing events
            loadEvents(calendar);

            calendar.addEventHandler( e -> {
                com.calendarfx.model.CalendarEvent event = (com.calendarfx.model.CalendarEvent) e;
                if (event.isEntryAdded()) {
                    saveEventToDB(event.getEntry(), calendar);
                } else if (event.isEntryRemoved()) {
                    deleteEventFromDB(event.getEntry());
                }

            });


            return calendarView;

        } catch (Exception e) {
            System.out.println("Error creating calendar view: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void saveEventToDB(Entry<?> entry, Calendar calendar) {
        Entry<CalendarEvent> typedEntry = (Entry<CalendarEvent>) entry;
        CalendarEvent event = new CalendarEvent(
            1, // calendar_id
            entry.getTitle(),
            entry.getLocation(),
            entry.getStartAsLocalDateTime(),
            entry.getEndAsLocalDateTime(),
            entry.getLocation()
        );
        
        localDB.saveEvent(
            event.getTitle(),
            event.getDescription(),
            event.getStartTime(),
            event.getEndTime(),
            event.getLocation(),
            event.getCalendarId()
        );
        
        typedEntry.setUserObject(event);
    }

    private void updateEventInDB(Entry<?> entry) {
        CalendarEvent event = (CalendarEvent) entry.getUserObject();
        if (event != null) {
            event.setTitle(entry.getTitle());
            event.setDescription(entry.getLocation()); // CalendarFX uses location for description
            event.setStartTime(entry.getStartAsLocalDateTime());
            event.setEndTime(entry.getEndAsLocalDateTime());
            event.setLocation(entry.getLocation());
            
            localDB.updateEvent(event); // You'll need to add this method to LocalDatabaseStorage
        }
    }

    private void deleteEventFromDB(Entry<?> entry) {
        CalendarEvent event = (CalendarEvent) entry.getUserObject();
        if (event != null) {
            localDB.deleteEvent(event.getEventId());
        }
    }

    private void loadEvents(Calendar calendar) {
        List<CalendarEvent> events = localDB.getEventsForCalendar(1); // calendar_id = 1
        for (CalendarEvent event : events) {
            Entry<CalendarEvent> entry = new Entry<>(event.getTitle());
            entry.setInterval(event.getStartTime(), event.getEndTime());
            entry.setLocation(event.getLocation());
            entry.setUserObject(event); // Store the event object for future updates/deletions
            calendar.addEntry(entry);
        }
    }

    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }
}
