package org.example.projectcalendar;

import java.io.IOException;
import java.sql.SQLException;

import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

import javafx.application.Platform;
import javafx.stage.Stage;

public class CalendarApplication extends javafx.application.Application {

    // Add configuration properties
    private static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    private static final int DEFAULT_SERVER_PORT = 8766;
    
    // Make fields final where possible
    private final LocalDatabaseStorage localDB;
    private ConnectionService connectionService;
    private Thread connectionThread;
    private MenuHandler menuHandler;

    public CalendarApplication() throws SQLException {
        this.localDB = new LocalDatabaseStorage();
    }

    @Override
    public void start(Stage stage) {
        try {
            initializeServices();
            initializeUI(stage);
            System.out.println("Application started.");
        } catch (IOException e) {
            handleStartupError(e);
        }
    }

    private void initializeServices() {
        String serverAddressForDB = "127.0.0.1";
        int serverPortForDB = 8766;
        
        try {
            connectionService = new ConnectionService(serverAddressForDB, serverPortForDB);
            connectionThread = new Thread(connectionService);
            connectionThread.start();
        } catch (RuntimeException e) {
            if (connectionThread != null) {
                connectionThread.interrupt();
            }
            throw new RuntimeException("Failed to initialize connection service", e);
        }
    }

    private void initializeUI(Stage stage) throws IOException {
        this.menuHandler = new MenuHandler(stage, connectionService, connectionThread, localDB);
    }

    private void handleStartupError(Exception e) {
        System.err.println("Failed to start application: " + e.getMessage());
        throw new RuntimeException("Application startup failed", e);
    }

    @Override
    public void stop() {
        if (connectionThread != null) {
            connectionThread.interrupt();
        }
        Platform.exit();
    }

    public static void main(String[] args) {
        launch();
    }
}
