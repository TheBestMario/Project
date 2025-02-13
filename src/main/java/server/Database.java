package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database implements AutoCloseable {
    private static final int DEFAULT_PORT = 8766;
    private static final int MAX_POOL_SIZE = 200;
    
    private static volatile Database instance;
    
    private final String dbUsername;
    private final String dbPassword;
    private final String connectionUrl;
    private final String containerName;
    private Connection connection;

    private Database() {
        this.dbUsername = requireEnvVariable("DB_USERNAME");
        this.dbPassword = requireEnvVariable("DB_PASSWORD");
        this.containerName = "calendarDB";
        this.connectionUrl = buildConnectionUrl();
    }

    // Keep existing methods but make them more robust
    private String buildConnectionUrl() {
        return String.format("jdbc:sqlserver://localhost:1433;databaseName=calendarDB;user=%s;password=%s;encrypt=false",
            dbUsername, dbPassword);
    }

    public static Database getInstance() {
        if (instance == null) {
            synchronized (Database.class) {
                if (instance == null) {
                    instance = new Database();
                }
            }
        }
        return instance;
    }

    private String requireEnvVariable(String name) {
        String value = System.getenv(name);
        if (value == null || value.trim().isEmpty()) {
            throw new ConfigurationException("Missing required environment variable: " + name);
        }
        return value;
    }

    public void connect() {
        int maxAttempts = 30;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                connection = DriverManager.getConnection(connectionUrl);
                System.out.println("Successfully connected to database");
                initializeDatabase();
                return;
            } catch (SQLException e) {
                attempts++;
                if (attempts == maxAttempts) {
                    throw new DatabaseConnectionException("Failed to connect to database after " + maxAttempts + " attempts", e);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new DatabaseConnectionException("Connection wait interrupted", ie);
                }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    private void initializeDatabase() {
        try (Statement stmt = connection.createStatement()) {
            // Create Users table if it doesn't exist
            stmt.execute("""
                IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Users')
                CREATE TABLE Users (
                    id INT IDENTITY(1,1) PRIMARY KEY,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    salt VARCHAR(255) NOT NULL,
                    created_at DATETIME DEFAULT GETDATE(),
                    updated_at DATETIME DEFAULT GETDATE()
                )
            """);
            
            System.out.println("Database initialization complete");
        } catch (SQLException e) {
            throw new DatabaseInitializationException("Failed to initialize database", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed");
            }
        } catch (SQLException e) {
            System.out.println("Error closing database connection");
        }
    }

    // Custom exceptions
    public static class ServerInitializationException extends RuntimeException {
        public ServerInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ConfigurationException extends RuntimeException {
        public ConfigurationException(String message) {
            super(message);
        }
    }

    public static class DatabaseConnectionException extends RuntimeException {
        public DatabaseConnectionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DatabaseInitializationException extends RuntimeException {
        public DatabaseInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // User CRUD Operations
    public void addUsertoTable(String username, String email, String salt, String hashedPassword) {
        String query = "INSERT INTO Users (username, email, salt, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, username);
            st.setString(2, email);
            st.setString(3, salt);
            st.setString(4, hashedPassword);
            st.execute();
        } catch (SQLException e) {
            System.out.println("Failed to add user to table: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public Profile getUserFromTable(String username, String password) {
        String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            
            if (rs.next()) {
                Profile profile = new Profile();
                profile.setUsername(rs.getString("username"));
                profile.setEmail(rs.getString("email"));
                return profile;
            }
        } catch (SQLException e) {
            System.out.println("Failed to get user from table: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean verifyCredentials(String username, String password) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ? AND password = ?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, username);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Failed to verify credentials: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return false;
    }

    public String getSalt(String username) {
        String query = "SELECT salt FROM Users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("salt");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get salt: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public String getHashedPassword(String username) {
        String query = "SELECT password FROM Users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            System.out.println("Failed to get hashed password: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean checkUserNameExists(String username) {
        String query = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(query)) {
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Failed to check username existence: " + e.getMessage());
            throw new RuntimeException(e);
        }
        return false;
    }
}