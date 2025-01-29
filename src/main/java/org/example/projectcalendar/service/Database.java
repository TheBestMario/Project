package org.example.projectcalendar.service;

import org.example.projectcalendar.service.User.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Map;

public class Database {
    private String dbUsername;
    private String dbPassword;
    private String connectionUrl;
    private static Connection con;

    public Database() {
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");
        int port = getPortFromEnv();

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
                Thread.sleep(Duration.ofSeconds(10));

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
        String query = "INSERT INTO Users (first_name, last_name, username, email, password) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, profile.getFirstName());
            st.setString(2, profile.getLastName());
            st.setString(3, profile.getUserName());
            st.setString(4, profile.getEmail());
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}