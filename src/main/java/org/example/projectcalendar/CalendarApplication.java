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
        /*
        Initialize local database connection for storage
        Checks if it can reach server
        Initialises menu handler and initial view
         */
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
            stage.setWidth(800);
            stage.setHeight(600);
            stage.setMinWidth(700);
            stage.setMinHeight(450);
            StackPane root = new StackPane();
            root.setStyle("-fx-background-image: url('static/images/robert_walters_logo.jpeg')");
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

}
