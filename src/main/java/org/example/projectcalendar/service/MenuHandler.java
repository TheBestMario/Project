package org.example.projectcalendar.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.projectcalendar.CalendarApplication;
import org.example.projectcalendar.Controller;

import java.io.IOException;

public class MenuHandler {
    private Scene currentScene;
    private static Stage currentStage;
    private int width;
    private int height;
    private String stylesheet;
    private FXMLLoader loader;

    public MenuHandler(Stage stage) throws IOException {
        this.width = 500;
        this.height = 500;
        this.loader = new FXMLLoader(CalendarApplication.class.getResource("start-view.fxml"));
        this.currentScene = new Scene(loader.load(), width, height);
        this.stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        this.currentScene.getStylesheets().add(stylesheet);
        MenuHandler.currentStage = stage;
        currentStage.setScene(currentScene);
        currentStage.setTitle("Calendar");
        currentStage.show();
        Controller controller = loader.getController();
        controller.setMenuHandler(this);
    }

    public void setSceneOnStage(String sceneName, String title) throws IOException {
        this.loader = new FXMLLoader(CalendarApplication.class.getResource(sceneName));
        this.currentScene = new Scene(loader.load());
        this.currentScene.getStylesheets().add(stylesheet);

        Controller controller = loader.getController();
        controller.setMenuHandler(this);

        currentStage.setScene(currentScene);
        currentStage.setTitle(title);
        currentStage.show();
    }
}
