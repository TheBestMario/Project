package org.example.projectcalendar;

import javafx.stage.Stage;
import org.example.projectcalendar.service.Database;
import org.example.projectcalendar.service.MenuHandler;

import java.io.IOException;

public class CalendarApplication extends javafx.application.Application {

    private Database db;
    private MenuHandler menuHandler;

    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinWidth(500);
        stage.setMinHeight(500);

        initializeDatabase();
        this.menuHandler = new MenuHandler(stage, db); // Pass Database instance
        System.out.println("Application started.");
    }

    public static void main(String[] args) {
        launch();
    }

    private void initializeDatabase() {
        try {
            this.db = new Database();
            db.establishConnection();
            System.out.println("Database connection established.");
        } catch (Exception e) {
            System.out.println("Failed to connect to database: " + e.getMessage());
        }
    }
}
