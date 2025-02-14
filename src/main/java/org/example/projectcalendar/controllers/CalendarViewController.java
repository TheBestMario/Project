package org.example.projectcalendar.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Calendar, Integer> calendarIdMap = new HashMap<>();

    @Override
    public void onDependenciesSet() {
        initializeCalendarView();
    }

    private void initializeCalendarView() {
        userProfile = Profile.getInstance();
        calendarView = new CalendarView();
        calendarView.getCalendarSources().clear();
        // Load user's calendars and their IDs
        var calendarData = getLocalStorage().getCalendarsForUser(userProfile.getUserId());
        List<Calendar> userCalendars = calendarData.getKey();
        calendarIdMap = calendarData.getValue();
        
        if (userCalendars.isEmpty()) {
            // Create default calendar
            String defaultName = userProfile.getUserName() + "'s Calendar";
            int calendarId = getLocalStorage().saveCalendar(defaultName, userProfile.getUserId());
            
            Calendar defaultCalendar = new Calendar(defaultName);
            defaultCalendar.setStyle(Calendar.Style.STYLE3);
            userCalendars.add(defaultCalendar);
            calendarIdMap.put(defaultCalendar, calendarId);
        }
        
        calendarView.setShowAddCalendarButton(false);
        
        CalendarSource source = new CalendarSource("My Calendars");
        source.getCalendars().addAll(userCalendars);
        
        // Load events for each calendar
        for (Calendar calendar : userCalendars) {
            // Get events for this calendar using its ID
            List<CalendarEvent> events = getLocalStorage().getEventsForCalendar(calendarIdMap.get(calendar));
            
            // Add events to the calendar
            for (CalendarEvent event : events) {
                Entry<CalendarEvent> entry = new Entry<>(event.getTitle());
                entry.setInterval(event.getStartTime(), event.getEndTime());
                entry.setLocation(event.getLocation());
                entry.setUserObject(event);
                calendar.addEntry(entry);
            }
            
            setupEventHandlers(calendar);
        }
        calendarView.getCalendarSources().add(source);
    }

    public void setupEventHandlers(Calendar calendar) {
        /*
        Add event handlers to the calendar passed
         */
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

    private void saveEventToDB(Entry<?> entry, Calendar calendar) {
        try {
            Entry<CalendarEvent> typedEntry = (Entry<CalendarEvent>) entry;
            System.out.println("Saving event: " + entry.getTitle());  // Debug log
            
            CalendarEvent event = new CalendarEvent(
                -1,
                calendarIdMap.get(calendar),
                entry.getTitle(),
                "",
                entry.getStartAsLocalDateTime(),
                entry.getEndAsLocalDateTime(),
                entry.getLocation()
            );
            
            int eventId = getLocalStorage().saveEvent(event);
            System.out.println("Event saved with ID: " + eventId);  // Debug log
            event.setEventId(eventId);
            typedEntry.setUserObject(event);
        } catch (Exception e) {
            e.printStackTrace();  // Print any exceptions
        }
    }

    public CalendarView getView() {
        return calendarView;
    }

    private void updateEventInDB(Entry<?> entry) {
        CalendarEvent event = (CalendarEvent) entry.getUserObject();
        if (event != null) {
            event.setTitle(entry.getTitle());
            event.setDescription(entry.getLocation());
            event.setStartTime(entry.getStartAsLocalDateTime());
            event.setEndTime(entry.getEndAsLocalDateTime());
            event.setLocation(entry.getLocation());
            
            getLocalStorage().updateEvent(event);
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

    public void addCalendarToMapping(Calendar calendar, int calendarId) {
        calendarIdMap.put(calendar, calendarId);
    }

    public void setupCalendarHandlers(Calendar calendar) {
        // ... existing setupEventHandlers code ...
    }
}
