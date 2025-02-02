package docker;

import org.example.projectcalendar.service.User.Profile;

import javax.crypto.SecretKey;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Database {
    private String dbUsername;
    private String dbPassword;
    private String connectionUrl;
    private String containerName;
    private static Connection con;
    private static Database database;
    private static ServerSocket serverSocket;
    private static InetAddress serverAddress;
    private static ArrayList<Connector> clientList = new ArrayList<>();
    private SecretKey key;

    public static void main(String[] args){
        database = new Database();
        database.establishConnection();
        ExecutorService pool = Executors.newFixedThreadPool(200);

        try(ServerSocket serverSocket = new ServerSocket(8766)){
            serverAddress = InetAddress.getLocalHost();
            System.out.println(serverAddress);
            while (true) {
                pool.execute(new Connector(serverSocket.accept()));
                System.out.println("client "+serverSocket.getInetAddress()+":"+serverSocket.getLocalPort()+" connected");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally{
            pool.shutdown();
        }
    }

    public static ArrayList<Connector> getClientList() {
        return clientList;
    }

    public Database() {
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");
        containerName = "calendarDB";
        int port = getPortFromEnv();
        database = this;
        if (this.dbUsername == null || this.dbPassword == null) {
            throw new RuntimeException("Database credentials are missing. Set DB_USERNAME and DB_PASSWORD.");
        }

        this.connectionUrl = "jdbc:sqlserver://localhost:" + port
                + ";databaseName=calendarDB;encrypt=false;";


        try {
            // Check if the database is accessible
            if (!databaseAccessibleCheck("localhost", port)) {

                String[] dockerDesktopInit = {"docker", "desktop", "start"};
                if (runCommand(dockerDesktopInit) == 0) {
                } else {
                    System.out.println("docker starting...");
                    Thread.sleep(Duration.ofSeconds(4));
                }

                if (dockerContainerExists(containerName)) {
                    System.out.println("container already running...");
                }else{
                    System.out.println("cannot connect to container, making one now.");
                    // Start Docker container
                    String[] command = {"docker-compose", "up", "-d"};
                    runCommand(command);
                    System.out.println("finished executing docker");
                }
                // Wait for the SQL Server to be ready
                boolean isReady = waitForPort("localhost", port, 30);
                if (!isReady) {
                    throw new RuntimeException("Failed to set up the local Docker container with SQL Server.");
                }

                // Add a delay to ensure SQL Server is fully up and running
                //Like don't even remove this unless adding a listener for the server status.
                Thread.sleep(Duration.ofSeconds(12));

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
    //singleton
    public static Database getInstance() {
        if (database == null) {
            database = new Database();
        }
        return database;
    }
    private boolean dockerContainerExists(String containerName) throws IOException, InterruptedException {
        String[] command = {"docker", "ps", "-a", "--filter", "name=" + containerName, "--format", "{{.Names}}"};
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(containerName)) {
                return true;
            }
        }
        return false;
    }
    private int runCommand(String[] command) throws IOException, InterruptedException {
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
        return exitCode;
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
            wait(2000);
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
            con = DriverManager.getConnection(this.connectionUrl);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addUsertoTable(String userName,String email, String salt, String hashedPassword) {
        String query = "INSERT INTO Users (username, email, salt, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, userName);
            st.setString(2, email);
            st.setString(3, salt);
            st.setString(4, hashedPassword);
            st.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getSalt(String username) {
        String query = "SELECT salt FROM Users WHERE username = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("salt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHashedPassword(String username) {
        String query = "SELECT password FROM Users WHERE username = ?";
        try (PreparedStatement st = con.prepareStatement(query)) {
            st.setString(1, username);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                return rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Profile getUserFromTable(Profile inProfile){
        Profile profile = null;
        String query = "SELECT (first_name, last_name, username, email, password) FROM Users WHERE user_name = ?, email = ?";
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