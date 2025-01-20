package org.example.projectcalendar;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.projectcalendar.service.MenuHandler;

import java.io.IOException;

public class CalendarApplication extends javafx.application.Application {

    MenuHandler menuHandler;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinWidth(500);
        stage.setMinHeight(500);

        //initialises menuHandler with first scene, found in constructor
        this.menuHandler = new MenuHandler(stage);

    }

    public static void main(String[] args) {
        launch();
    }
}