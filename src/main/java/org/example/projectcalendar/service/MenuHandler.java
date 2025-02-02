package org.example.projectcalendar.service;

import docker.Database;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.projectcalendar.CalendarApplication;
import org.example.projectcalendar.Controller;

import java.io.IOException;

public class MenuHandler {
    private static Stage primaryStage;

    private int width;
    private int height;

    private String stylesheet;
    private FXMLLoader loader;
    private Parent root;
    private String title;
    private Scene scene;
    private ConnectionService connectionService;
    private Thread connectionThread;

    public MenuHandler(Stage stage, ConnectionService connectionService,Thread connectionThread) throws IOException {
        primaryStage = stage;
        this.connectionService = connectionService;
        this.connectionThread = connectionThread;
        initializeUI();
    }

    private void initializeUI() throws IOException {
        // Start with a StackPane as the root
        root = new StackPane();
        root.setId("root");
        scene = new Scene(root, 600, 500);

        FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource("Initial/login-view.fxml"));
        ((StackPane)root).getChildren().add(loader.load());
        ((StackPane)root).setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-image: url(static/images/robert_walters_logo.jpeg)");

        Controller controller = loader.getController();
        controller.setMenuHandler(this);
        controller.setRoot(root);
        controller.setConnectionService(connectionService);
        controller.setConnectionThread(connectionThread);

        String stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(500);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Calendar");
        primaryStage.show();
    }

    public void switchToCalendarMenu() throws IOException {
        BorderPane borderPane = new BorderPane();
        borderPane.setId("root");

        // Load and set the menu bar
        FXMLLoader menuLoader = new FXMLLoader(CalendarApplication.class.getResource("app-sidebar.fxml"));
        borderPane.setLeft(menuLoader.load());

        // Load and set the title bar
        FXMLLoader titleLoader = new FXMLLoader(CalendarApplication.class.getResource("title-bar.fxml"));
        borderPane.setTop(titleLoader.load());

        // Load and set the content view
        FXMLLoader contentLoader = new FXMLLoader(CalendarApplication.class.getResource("Initial/content.fxml"));
        borderPane.setCenter(contentLoader.load());

        // Update the scene's root
        scene.setRoot(borderPane);
        this.root = borderPane;

        // Apply stylesheets
        String stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
        stylesheet = CalendarApplication.class.getResource("/static/menubar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
        primaryStage.setTitle("Calendar");
        primaryStage.show();
    }

    public void switchToStartViewMenu() throws IOException {
        StackPane stackPane = new StackPane();
        stackPane.setId("root");

        FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource("Initial/start-view.fxml"));
        stackPane.getChildren().add(loader.load());
        stackPane.setAlignment(Pos.CENTER);
        stackPane.setStyle("-fx-background-image: url(/static/images/robert_walters_logo.jpeg)");

        // Update the scene's root
        scene.setRoot(stackPane);
        this.root = stackPane;

        // Apply stylesheets
        String stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
    }

    public void setNodeToRoot(String fxmlPath) throws IOException {
        loadNode(fxmlPath, true);
    }

    public void addNodeToRoot(String fxmlPath) throws IOException {
        loadNode(fxmlPath, false);
    }

    public void loadNode(String fxmlPath, boolean clearRoot) throws IOException {
        FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource(fxmlPath));
        Parent node = loader.load();
        Controller controller = loader.getController();
        controller.setMenuHandler(this);
        controller.setRoot(root);
        controller.setConnectionService(connectionService);

        switch (root.getClass().getSimpleName()) {
            case "StackPane":
                if (clearRoot) {
                    ((StackPane) root).getChildren().clear();
                }
                ((StackPane) root).getChildren().add(node);
                break;
            case "BorderPane":
                if (clearRoot) {
                    ((BorderPane) root).getChildren().clear();
                }
                ((BorderPane) root).setCenter(node);
                break;
        }
    }

    public Node getNodeFromRoot(String fxmlName) {
        if (root instanceof StackPane) {
            for (Node node : ((StackPane) root).getChildren()) {
                if (node.getId().equals(fxmlName)) {
                    return node;
                }
            }
        } else if (root instanceof BorderPane) {
            Node centerNode = ((BorderPane) root).getCenter();
            if (centerNode != null && centerNode.getProperties().get("file").equals(fxmlName)) {
                return centerNode;
            }
        }
        return null;
    }

    public Parent getRoot() {
        return this.root;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}