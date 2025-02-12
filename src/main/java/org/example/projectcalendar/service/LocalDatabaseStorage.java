package org.example.projectcalendar.service;

import org.example.projectcalendar.service.User.Profile;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

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
        String query = "INSERT INTO Users (username,email,password,salt) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, salt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
}
