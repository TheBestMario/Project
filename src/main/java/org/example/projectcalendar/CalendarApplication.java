package org.example.projectcalendar;

import javafx.stage.Stage;
import docker.Database;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.MenuHandler;

import java.io.IOException;

public class CalendarApplication extends javafx.application.Application {

    private Database db;
    private MenuHandler menuHandler;
    private static String serverAddressForDB;
    private static int serverPortForDB;
    private ConnectionService connectionService;
    private Thread connectionThread;

    @Override
    public void start(Stage stage){
        serverAddressForDB = "127.0.0.1";
        serverPortForDB = 8766;
        try{
            connectionService = new ConnectionService(serverAddressForDB, serverPortForDB);
            connectionThread = new Thread(connectionService);
            connectionThread.start();
        } catch (RuntimeException e) {

        }

        try{
            this.menuHandler = new MenuHandler(stage, connectionService, connectionThread); // Pass Database instance
            System.out.println("Application started.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
