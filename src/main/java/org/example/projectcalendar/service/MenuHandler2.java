package org.example.projectcalendar.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.projectcalendar.CalendarApplication;

import java.io.IOException;

public class MenuHandler2 {

    private static Stage primaryStage;

    private int width;
    private int height;

    private String stylesheet;
    private BorderPane root;
    private String title;

    public MenuHandler2(Stage stage) throws IOException {

        this.width = 500;
        this.height = 500;
        //makes root component in the hierarchy
        //initializes scene to the root
        this.root = new BorderPane();
        root.setId("root");
        Scene scene = new Scene(root, width, height);

        // Load and set the menu bar
        FXMLLoader menuLoader = new FXMLLoader(CalendarApplication.class.getResource("app-sidebar.fxml"));
        root.setLeft(menuLoader.load());

        // Load and set the title bar
        FXMLLoader titleLoader = new FXMLLoader(CalendarApplication.class.getResource("title-bar.fxml"));
        root.setTop(titleLoader.load());

        // Load and set the content view
        FXMLLoader contentLoader = new FXMLLoader(CalendarApplication.class.getResource("Initial/content.fxml"));
        root.setCenter(contentLoader.load());

        this.stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage = stage;
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calendar");
        primaryStage.show();
    }
}