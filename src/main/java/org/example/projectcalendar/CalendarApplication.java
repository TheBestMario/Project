package org.example.projectcalendar;

import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import server.Database;

public class CalendarApplication extends javafx.application.Application {

    private Database db;
    private MenuHandler menuHandler;
    private String serverAddressForDB;
    private int serverPortForDB;
    private ConnectionService connectionService;
    private Thread connectionThread;

    @Override
    public void start(Stage stage) {
        serverAddressForDB = "127.0.0.1";
        serverPortForDB = 8766;
        LocalDatabaseStorage localDB = new LocalDatabaseStorage();
        
        try {
            connectionService = new ConnectionService(serverAddressForDB, serverPortForDB);
            connectionThread = new Thread(connectionService);
            connectionThread.start();
        } catch (RuntimeException e) {
            connectionThread.interrupt();
            throw new RuntimeException(e);
        }

        try {
            this.menuHandler = new MenuHandler(stage, connectionService, localDB);
            
            // Initialize with login view in a StackPane
            StackPane root = new StackPane();
            root.setId("root");
            menuHandler.setRoot(root);
            menuHandler.addNodeToRoot("Initial/login-view.fxml");
            menuHandler.showView(root);
            
            System.out.println("Application started.");
        } catch (RuntimeException e) {
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
