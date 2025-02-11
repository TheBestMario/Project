package org.example.projectcalendar;

import javafx.stage.Stage;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import server.Database;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.MenuHandler;

import java.io.IOException;

public class CalendarApplication extends javafx.application.Application {

    private Database db;
    private MenuHandler menuHandler;
    private String serverAddressForDB;
    private int serverPortForDB;
    private ConnectionService connectionService;
    private Thread connectionThread;

    @Override
    public void start(Stage stage){

        serverAddressForDB = "127.0.0.1";
        serverPortForDB = 8766;
        LocalDatabaseStorage localDB = new LocalDatabaseStorage();
        try{
            connectionService = new ConnectionService(serverAddressForDB, serverPortForDB);
            connectionThread = new Thread(connectionService);
            connectionThread.start();
        } catch (RuntimeException e) {

        }

        try{
            this.menuHandler = new MenuHandler(stage, connectionService, connectionThread, localDB); // Pass Database instance
            System.out.println("Application started.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void stop() {
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }

//    private void initializeDatabase() {
//        try {
//            this.db = new Database();
//            db.establishConnection();
//            System.out.println("Database connection established.");
//        } catch (Exception e) {
//            System.out.println("Failed to connect to database: " + e.getMessage());
//        }
//    }
}
