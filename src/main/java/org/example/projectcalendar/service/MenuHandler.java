package org.example.projectcalendar.service;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

import org.example.projectcalendar.CalendarApplication;
import org.example.projectcalendar.Controller;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuHandler {
    
    // UI Constants
    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 500;
    private static final int MIN_WIDTH = 500;
    private static final int MIN_HEIGHT = 500;
    private static final String DEFAULT_TITLE = "Calendar";
    private static final String BACKGROUND_IMAGE = "-fx-background-image: url(static/images/robert_walters_logo.jpeg)";
    
    // Resource paths
    private static final String LOGIN_VIEW_PATH = "Initial/login-view.fxml";
    private static final String STYLESHEET_PATH = "/static/calendar.css";
    
    private final Stage primaryStage;
    private final ConnectionService connectionService;
    private final Thread connectionThread;
    private final LocalDatabaseStorage localDB;
    
    private Scene scene;
    private Parent root;

    public MenuHandler(Stage stage, ConnectionService connectionService, 
                      Thread connectionThread, LocalDatabaseStorage localDB) {
        this.primaryStage = Objects.requireNonNull(stage, "Stage cannot be null");
        this.connectionService = Objects.requireNonNull(connectionService, "ConnectionService cannot be null");
        this.connectionThread = Objects.requireNonNull(connectionThread, "ConnectionThread cannot be null");
        this.localDB = Objects.requireNonNull(localDB, "LocalDB cannot be null");
        
        try {
            initializeUI();
        } catch (IOException e) {
            throw new UIInitializationException("Failed to initialize UI", e);
        }
    }

    private void initializeUI() throws IOException {
        initializeRoot();
        initializeLoginView();
        setupStage();
    }

    private void initializeRoot() {
        root = new StackPane();
        root.setId("root");
        scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        root.setStyle(BACKGROUND_IMAGE);
    }

    private void initializeLoginView() throws IOException {
        FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource("Initial/login-view.fxml"));
        Parent loginView = loader.load();
        
        ((StackPane)root).getChildren().add(loginView);
        ((StackPane)root).setAlignment(Pos.CENTER);
        
        initializeController(loader.getController());
        applyStylesheets();
    }

    private void initializeController(Controller controller) {
        controller.setMenuHandler(this);
        controller.setRoot(root);
        controller.setLocalStorage(localDB);
        controller.setConnectionService(connectionService);
        controller.setConnectionThread(connectionThread);
    }

    private void setupStage() {
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEFAULT_TITLE);
        primaryStage.show();
    }

    private void applyStylesheets() {
        String stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);
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

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.setTitle("Calendar");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void setNodeToRoot(String fxmlPath) throws IOException {
        loadNode(fxmlPath, true);
    }

    public void addNodeToRoot(String fxmlPath) throws IOException {
        loadNode(fxmlPath, false);
    }

    public void loadNode(String fxmlPath, boolean clearRoot) throws IOException {
        try {
            FXMLLoader loader = createConfiguredLoader(fxmlPath);
            Parent node = loader.load();
            setupController(loader.getController());
            updateRootNode(node, clearRoot);
        } catch (IOException e) {
            System.out.println("Failed to load FXML: " + fxmlPath);
            throw e;
        }
    }

    private FXMLLoader createConfiguredLoader(String fxmlPath) {
        URL resource = CalendarApplication.class.getResource(fxmlPath);
        if (resource == null) {
            System.out.println("FXML resource not found: " + fxmlPath);
        }
        return new FXMLLoader(resource);
    }

    private void setupController(Controller controller) {
        controller.setMenuHandler(this);
        controller.setRoot(root);
        controller.setLocalStorage(localDB);
        controller.setConnectionService(connectionService);
        controller.setConnectionThread(connectionThread);
    }

    private void updateRootNode(Parent node, boolean clearRoot) {
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

    // Add custom exceptions
    public static class UIInitializationException extends RuntimeException {
        public UIInitializationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}