package org.example.projectcalendar.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.example.projectcalendar.model.CalendarEvent;
import org.example.projectcalendar.model.Profile;

import com.calendarfx.model.Calendar;

public class LocalDatabaseStorage {
    private static final String DB_URL = "jdbc:sqlite:local_storage.db";
    private Connection conn;
    private String schemaPath = "local_schema.txt";

    public LocalDatabaseStorage() {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        /*
        Reads from file, line by line and splits lines by ;
        Then executes each query from the list

         */
        try {
            BufferedReader reader = new BufferedReader(new FileReader(schemaPath));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            String schema = sb.toString();
            String[] queries = schema.split(";");
            try (Statement stmt = conn.createStatement()) {
                for (String query : queries) {
                    query = query.trim();
                    if (!query.isEmpty()) {
                        stmt.execute(query);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveUsernamePassword(String username, String email, String password, String salt) {
        try {
            // Validate all inputs before database operation
            ValidationUtils.validateUsername(username);
            ValidationUtils.validateEmail(email);
            ValidationUtils.validatePassword(password);
            
            String query = "INSERT INTO Users (username,email,password,salt) VALUES (?,?,?,?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, email);
                stmt.setString(3, password);
                stmt.setString(4, salt);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while saving user: " + e.getMessage(), e);
        }
    }

    public boolean checkUsernamePassword(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public Profile handleLoginOffline(String username, String password) {
        Profile profile = Profile.getInstance();
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement st = conn.prepareStatement(query)) {
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                profile.setUsername(rs.getString("username"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return profile;
    }

    public String getSalt(String username) {
        System.out.println(username);
        String query = "SELECT salt FROM Users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.getString("salt") != null) {
                return rs.getString("salt");
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return null;
    }

    public String getPassword(String username){
        String query = "SELECT password FROM Users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.getString("password") != null){
                return rs.getString("password");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public String getUsername(String usernameInput) {
        String query = "SELECT username FROM Users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usernameInput);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()){
                return rs.getString("username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int saveEvent(String title, String description, LocalDateTime startTime, LocalDateTime endTime, String location, int calendarId) {
        try {
            ValidationUtils.validateEventTitle(title);
            ValidationUtils.validateEventDescription(description);
            
            String sql = "INSERT INTO events (title, description, start_time, end_time, location, calendar_id) " +
                         "VALUES (?, ?, ?, ?, ?, ?) RETURNING event_id";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, title);
                pstmt.setString(2, description);
                pstmt.setTimestamp(3, java.sql.Timestamp.valueOf(startTime));
                pstmt.setTimestamp(4, java.sql.Timestamp.valueOf(endTime));
                pstmt.setString(5, location);
                pstmt.setInt(6, calendarId);
                
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("event_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database error while saving event: " + e.getMessage(), e);
        }
        return -1;
    }
    public int saveCalendar(String name, int userId) {
        String sql = "INSERT INTO calendars (name, user_id) VALUES (?, ?) RETURNING calendar_id";
        int calendarId = -1;
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, userId);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                calendarId = rs.getInt("calendar_id");
            }
        
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error saving calendar to database: " + e.getMessage());
        }
    
        return calendarId;
    }

    public void deleteEvent(int eventId) {
        String query = "DELETE FROM Events WHERE event_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, eventId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<CalendarEvent> getEventsForCalendar(int calendarId) {
        List<CalendarEvent> events = new ArrayList<>();
        String query = "SELECT * FROM Events WHERE calendar_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, calendarId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                CalendarEvent event = new CalendarEvent(
                    rs.getInt("event_id"),
                    rs.getInt("calendar_id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    LocalDateTime.parse(rs.getString("start_time")),
                    LocalDateTime.parse(rs.getString("end_time")),
                    rs.getString("location")
                );
                events.add(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return events;
    }

    public void updateEvent(CalendarEvent event) {
        String query = "UPDATE Events SET title=?, description=?, start_time=?, end_time=?, location=? WHERE event_id=?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, event.getTitle());
            stmt.setString(2, event.getDescription());
            stmt.setString(3, event.getStartTime().toString());
            stmt.setString(4, event.getEndTime().toString());
            stmt.setString(5, event.getLocation());
            stmt.setInt(6, event.getEventId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCalendar(Calendar calendar) {
        String query = "INSERT INTO Calendars (name) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, calendar.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
