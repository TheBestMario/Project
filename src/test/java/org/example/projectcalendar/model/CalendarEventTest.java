package org.example.projectcalendar.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class CalendarEventTest {

    private CalendarEvent event;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final LocalDateTime endTime = startTime.plusHours(1);

    @BeforeEach
    void setUp() {
        event = new CalendarEvent(
            1, // eventId
            1, // calendarId
            "Test Event",
            "Test Description",
            startTime,
            endTime,
            "Test Location"
        );
    }

    @Test
    void constructor_ShouldSetAllFieldsCorrectly() {
        assertEquals(1, event.getEventId());
        assertEquals(1, event.getCalendarId());
        assertEquals("Test Event", event.getTitle());
        assertEquals("Test Description", event.getDescription());
        assertEquals(startTime, event.getStartTime());
        assertEquals(endTime, event.getEndTime());
        assertEquals("Test Location", event.getLocation());
    }

    @Test
    void setAndGetTitle_ShouldWorkCorrectly() {
        String newTitle = "Updated Event";
        event.setTitle(newTitle);
        assertEquals(newTitle, event.getTitle());
    }

    @Test
    void setAndGetDescription_ShouldWorkCorrectly() {
        String newDescription = "Updated Description";
        event.setDescription(newDescription);
        assertEquals(newDescription, event.getDescription());
    }

    @Test
    void setAndGetTimes_ShouldWorkCorrectly() {
        LocalDateTime newStart = startTime.plusDays(1);
        LocalDateTime newEnd = endTime.plusDays(1);
        
        event.setStartTime(newStart);
        event.setEndTime(newEnd);
        
        assertEquals(newStart, event.getStartTime());
        assertEquals(newEnd, event.getEndTime());
    }

    @Test
    void setAndGetLocation_ShouldWorkCorrectly() {
        String newLocation = "Updated Location";
        event.setLocation(newLocation);
        assertEquals(newLocation, event.getLocation());
    }

    @Test
    void setAndGetUserId_ShouldWorkCorrectly() {
        int userId = 123;
        event.setUserId(userId);
        assertEquals(userId, event.getUserId());
    }
} 