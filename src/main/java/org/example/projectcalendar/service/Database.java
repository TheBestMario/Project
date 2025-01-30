package org.example.projectcalendar.service;

import org.example.projectcalendar.service.User.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Duration;
import java.util.Map;

public class Database {
    private String dbUsername;
    private String dbPassword;
    private String connectionUrl;
    private static Connection con;
    private static Database database;

    public Database() {
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");
        int port = getPortFromEnv();
        database = this;

        try {


            // Check if the database is accessible
            if (!databaseAccessibleCheck("localhost", port)) {
                // Start Docker container
                System.out.println("cannot connect to container, making one now.");
                String[] command = {"docker-compose", "up", "-d"};
                runCommand(command);
                System.out.println("finished executing docker");
                // Wait for the SQL Server to be ready
                boolean isReady = waitForPort("localhost", port, 30);
                if (!isReady) {
                    throw new RuntimeException("Failed to set up the local Docker container with SQL Server.");
                }

                // Add a delay to ensure SQL Server is fully up and running
                //Like don't even remove this except for adding a listener for the server.
                Thread.sleep(Duration.ofSeconds(12));

                // Execute the schema
                Path path = Paths.get(System.getProperty("user.dir"), "schema.txt");
                String[] commandCreateDB = {
                        "docker", "exec", "calendarDB",
                        "/opt/mssql-tools18/bin/sqlcmd",
                        "-S", "localhost",
                        "-U", dbUsername,
                        "-P", dbPassword,
                        "-C",
                        "-i", "/usr/src/app/schema.txt"
                };
                System.out.println("executing schema to the container");
                runCommand(commandCreateDB);
            }
            System.out.println("establishing connection");
            this.connectionUrl = "jdbc:sqlserver://localhost:" + port
                    + ";databaseName=calendarDB;user=" + this.dbUsername
                    + ";password=" + this.dbPassword
                    + ";encrypt=false;";

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static Database getInstance() {
        if (database == null) {
            synchronized (Database.class) {
                if (database == null) {
                    database = new Database();
                }
            }
        }
        return database;
    }

    private void runCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Map<String, String> env = processBuilder.environment();
        env.put("DB_USERNAME", dbUsername);
        env.put("DB_PASSWORD", dbPassword);
        env.put("LOCAL_PORT", Integer.toString(getPortFromEnv()));

        Process process = processBuilder.start();
        //large debugging bit of code
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = inputReader.readLine()) != null) {
            System.out.println(line);
        }
        while ((line = errorReader.readLine()) != null) {
            System.err.println(line);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Command failed with exit code: " + exitCode);
        }
        //end of debugging code
    }

    private boolean waitForPort(String host, int port, int maxRetries) throws InterruptedException {
        int retryCount = 0;
        while (retryCount < maxRetries) {
            System.out.println(retryCount + " waiting for port, " + port);
            try (Socket socket = new Socket(host, port)) {
                return true;
            } catch (IOException e) {
                System.out.println(e);
            }
            Thread.sleep(2000);
            retryCount++;
        }
        return false;
    }

    private boolean databaseAccessibleCheck(String host, int port) {
        try (Connection connection = DriverManager.getConnection("jdbc:sqlserver://" + host + ":" + port
                + ";databaseName=calendarDB;user=" + dbUsername + ";password=" + dbPassword
                + ";encrypt=false;")) {
            System.out.println("can connect to " + host + ":" + port);
            return true;
        } catch (SQLException e) {
            System.out.println("can't connect to " + host + ":" + port);
            return false;
        }
    }

    private int getPortFromEnv() {
        int port = 8765;
        return port;
    }

    public Boolean establishConnection() {
        try {
            System.out.println(this.connectionUrl);
            con = DriverManager.getConnection(this.connectionUrl);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUsertoTable(Profile profile) {
        String query = "INSERT INTO Users (username, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            //st.setString(1, profile.getFirstName());
            //st.setString(2, profile.getLastName());
            st.setString(1, profile.getUserName());
            st.setString(2, profile.getEmail());
            st.setString(3, profile.getPassword());
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public Profile getUserFromTable(Profile inProfile){
        Profile profile = null;
        String query = "SELECT (first_name, last_name, username, email, password) FROM Users" +
                "WHERE user_name = ?, email = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, inProfile.getUserName());
            st.setString(2, inProfile.getEmail());
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                profile = new Profile(rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"));
                profile.setFirstName(rs.getString("first_name"));
                profile.setLastName(rs.getString("last_name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return profile;
    }
    public boolean checkUserNameExists(String username){
        String query = "SELECT username FROM Users WHERE username = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, username);
            st.execute();
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
    public boolean verifyCredentials(String usernameOrEmail, String password) {
        String query = "SELECT password FROM Users WHERE username = ? or email = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, usernameOrEmail);
            st.setString(2, usernameOrEmail);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {

                String storedPassword = rs.getString("password");
                if (storedPassword.equals(password)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}