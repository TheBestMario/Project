package org.example.projectcalendar.service;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.projectcalendar.CalendarApplication;
import org.example.projectcalendar.Controller;

import java.io.IOException;

public class MenuHandler {
    private Scene currentScene;
    private static Stage primaryStage;
    private int width;
    private int height;
    private String stylesheet;
    private FXMLLoader loader;
    private StackPane root;
    private String title;

    public MenuHandler(Stage stage) throws IOException {
        this.width = 500;
        this.height = 500;
        this.loader = new FXMLLoader(CalendarApplication.class.getResource("start-view.fxml"));

        this.root = new StackPane();
        root.getChildren().add(loader.load());


        this.currentScene = new Scene(root, width, height);

        Controller controller = loader.getController();
        controller.setMenuHandler(this);
        controller.setRoot(root);

        this.stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        this.currentScene.getStylesheets().add(stylesheet);
        MenuHandler.primaryStage = stage;
        primaryStage.setScene(currentScene);
        primaryStage.setTitle("Calendar");
        primaryStage.show();
    }

    public void createScene(String sceneName, String title) {
        this.loader = new FXMLLoader(CalendarApplication.class.getResource(sceneName));
        this.title = title;
        StackPane root = new StackPane();
        this.currentScene = new Scene(root);
        this.currentScene.getStylesheets().add(stylesheet);
    }
    public void setSceneProperties() {
        Controller controller = loader.getController();
        controller.setMenuHandler(this);
        controller.setRoot(root);
        this.root.getChildren().add(loader.getRoot());
    }
    public void setStageScene() {
        primaryStage.setScene(currentScene);
        primaryStage.setTitle(title);
        primaryStage.show();
    }

    public Scene getCurrentScene(){
        return this.currentScene;
    }
}
