package org.example.projectcalendar;

import javafx.stage.Stage;
import org.example.projectcalendar.service.Database;
import org.example.projectcalendar.service.MenuHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

public class CalendarApplication extends javafx.application.Application {
    static Database db;
    MenuHandler menuHandler;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinWidth(500);
        stage.setMinHeight(500);

        //initialises menuHandler with first scene, found in constructor
        this.menuHandler = new MenuHandler(stage);

    }

    public static void main(String[] args) {

        /*
        Please make sure you have two environment variables:
        DB_USERNAME = your_username
        DB_PASSWORD = your_password
        where your_username and your_password is whatever you want it to be.
         */
        db = new Database();
        db.establishConnection();

        launch();
    }
}