package org.example.projectcalendar.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.model.CalendarEvent;
import org.example.projectcalendar.model.Profile;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;

public class CalendarViewController extends Controller {
    private CalendarView calendarView;
    private Profile userProfile;

    @Override
    public void onDependenciesSet() {
        initializeCalendarView();
    }

    private void initializeCalendarView() {
        userProfile = Profile.getInstance();
        calendarView = new CalendarView();
        
        Calendar calendar = createMainCalendar();
        setupCalendarView(calendar);
        loadExistingEvents(calendar);
        setupEventHandlers(calendar);
    }

    private Calendar createMainCalendar() {
        Calendar calendar = new Calendar("My Calendar");
        calendar.setStyle(Calendar.Style.STYLE3);
        return calendar;
    }

    private void setupCalendarView(Calendar calendar) {
        CalendarSource source = new CalendarSource("My Calendars");
        source.getCalendars().add(calendar);
        
        calendarView.setShowAddCalendarButton(false);
        calendarView.getCalendarSources().clear();
        calendarView.getCalendarSources().add(source);
        calendarView.showMonthPage();
    }

    public CalendarView getView() {
        return calendarView;
    }

    private void setupEventHandlers(Calendar calendar) {
        calendar.addEventHandler( e -> {
            System.out.println(e.getEventType().toString());
            com.calendarfx.model.CalendarEvent event = (com.calendarfx.model.CalendarEvent) e;
            if (event.isEntryAdded()) {
                System.out.println("Saving event to DB");
                saveEventToDB(event.getEntry(), calendar);
            } else if (event.isEntryRemoved()) {
                System.out.println("Deleting event from DB");
                deleteEventFromDB(event.getEntry());
            } else if (event.getEventType().getName().equals("ENTRY_TITLE_CHANGED")) {
                System.out.println("Updating event in DB");
                String newTitle = event.getEntry().getTitle();
                updateEventInDB(event.getEntry());
            } else if (event.getEventType().getName().equals("ENTRY_LOCATION_CHANGED")) {
                System.out.println("Updating event in DB");
                String newLocation = event.getEntry().getLocation();
                updateEventInDB(event.getEntry());
            } else if (event.getEventType().getName().equals("ENTRY_INTERVAL_CHANGED")) {
                System.out.println("Updating event in DB");
                LocalDateTime newStart = event.getEntry().getStartAsLocalDateTime();
                LocalDateTime newEnd = event.getEntry().getEndAsLocalDateTime();
                updateEventInDB(event.getEntry());
            }
        });
    }

    private void loadExistingEvents(Calendar calendar) {
        List<CalendarEvent> events = getLocalStorage().getEventsForCalendar(1); // calendar_id = 1
        for (CalendarEvent event : events) {
            Entry<CalendarEvent> entry = new Entry<>(event.getTitle());
            entry.setInterval(event.getStartTime(), event.getEndTime());
            entry.setLocation(event.getLocation());
            entry.setUserObject(event);
            calendar.addEntry(entry);
        }
    }

    private void saveEventToDB(Entry<?> entry, Calendar calendar) {
        Entry<CalendarEvent> typedEntry = (Entry<CalendarEvent>) entry;
        int calendarID = 1;
        // Set the calendar for the entry explicitly
        entry.setCalendar(calendar);
        
        CalendarEvent event = new CalendarEvent(
                calendarID,
            entry.getTitle(),
            entry.getLocation(),
            entry.getStartAsLocalDateTime(),
            entry.getEndAsLocalDateTime(),
            entry.getLocation()
        );

        // Save to database and get the generated event ID
        int eventId = getLocalStorage().saveEvent(
            event.getTitle(),
            event.getDescription(),
            event.getStartTime(),
            event.getEndTime(),
            event.getLocation(),
            event.getCalendarId()
        );
        
        // Set the event ID after database save
        event.setEventId(eventId);
        // Set the user object with the complete event
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
            
            getLocalStorage().updateEvent(event); // You'll need to add this method to LocalDatabaseStorage
        }
    }

    private void deleteEventFromDB(Entry<?> entry) {
        CalendarEvent event = (CalendarEvent) entry.getUserObject();
        if (event != null) {
            getLocalStorage().deleteEvent(event.getEventId());
        }
    }

    public void addCalendar(String name, int calendarId) {
        Calendar calendar = new Calendar(name);
        calendar.setStyle(Calendar.Style.STYLE3);
        
        CalendarSource source = calendarView.getCalendarSources().get(0);
        source.getCalendars().add(calendar);
        
        // Load events for this calendar
        List<CalendarEvent> events = getLocalStorage().getEventsForCalendar(calendarId);
        for (CalendarEvent event : events) {
            Entry<CalendarEvent> entry = new Entry<>(event.getTitle());
            entry.setInterval(event.getStartTime(), event.getEndTime());
            entry.setLocation(event.getLocation());
            entry.setUserObject(event);
            calendar.addEntry(entry);
        }
    }
}
