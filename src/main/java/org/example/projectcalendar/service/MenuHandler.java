package org.example.projectcalendar.service;

import org.example.projectcalendar.CalendarApplication;
import org.example.projectcalendar.Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import java.net.URL;

public class MenuHandler {
    private final Stage primaryStage;
    private final ConnectionService connectionService;
    private final LocalDatabaseStorage localDB;
    private Scene scene;
    private Parent root;

    public MenuHandler(Stage stage, ConnectionService connectionService, LocalDatabaseStorage localDB) {
        this.primaryStage = stage;
        this.connectionService = connectionService;
        this.localDB = localDB;
    }

    public <T extends Controller> T loadView(String fxmlPath) {
        try {
            URL resource = CalendarApplication.class.getResource(fxmlPath);
            if (resource == null) {
                System.out.println("Could not find resource: " + fxmlPath);
                throw new RuntimeException("Resource not found: " + fxmlPath);
            }
            System.out.println("Loading FXML from: " + resource);
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            T controller = loader.getController();
            initializeController(controller);
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load view: " + fxmlPath, e);
        }
    }

    public void initializeController(Controller controller) {
        controller.setMenuHandler(this);
        controller.setLocalStorage(localDB);
        controller.setConnectionService(connectionService);
    }

    public void setScene(Scene scene) {
        this.scene = scene;
        primaryStage.setScene(scene);
    }

    public void showView(Parent view) {
        if (scene == null) {
            scene = new Scene(view);
            // Add CSS files
            scene.getStylesheets().add(CalendarApplication.class.getResource("/static/menubar.css").toExternalForm());
            scene.getStylesheets().add(CalendarApplication.class.getResource("/static/calendar.css").toExternalForm());
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(view);
        }
        primaryStage.show();
    }

    public Node getNodeFromRoot(String fxmlName) {
        if (root instanceof StackPane) {
            for (Node node : ((StackPane) root).getChildren()) {
                if (node.getId() != null && node.getId().equals(fxmlName)) {
                    return node;
                }
            }
        } else if (root instanceof BorderPane) {
            Node centerNode = ((BorderPane) root).getCenter();
            if (centerNode != null && centerNode.getId() != null && 
                centerNode.getId().equals(fxmlName)) {
                return centerNode;
            }
        }
        return null;
    }

    public Parent getRoot() {
        return root;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void addNodeToRoot(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource(fxmlPath));
            Parent node = loader.load();
            Controller controller = loader.getController();
            initializeController(controller);

            if (root instanceof StackPane) {
                ((StackPane) root).getChildren().add(node);
            } else if (root instanceof BorderPane) {
                ((BorderPane) root).setCenter(node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to add node: " + fxmlPath, e);
        }
    }

    public void setNodeToRoot(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource(fxmlPath));
            Parent node = loader.load();
            Controller controller = loader.getController();
            initializeController(controller);

            if (root instanceof StackPane) {
                ((StackPane) root).getChildren().clear();
                ((StackPane) root).getChildren().add(node);
            } else if (root instanceof BorderPane) {
                ((BorderPane) root).setCenter(node);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to set node: " + fxmlPath, e);
        }
    }

    public void switchToCalendarMenu() {
        BorderPane borderPane = new BorderPane();
        borderPane.setId("root");

        // Load views and controllers
        FXMLLoader titleLoader = new FXMLLoader(CalendarApplication.class.getResource("title-bar.fxml"));
        FXMLLoader sidebarLoader = new FXMLLoader(CalendarApplication.class.getResource("app-sidebar.fxml"));
        FXMLLoader contentLoader = new FXMLLoader(CalendarApplication.class.getResource("Initial/content.fxml"));

        try {
            Parent titleBar = titleLoader.load();
            Parent sidebar = sidebarLoader.load();
            Parent content = contentLoader.load();

            initializeController(titleLoader.getController());
            initializeController(sidebarLoader.getController());
            initializeController(contentLoader.getController());

            borderPane.setTop(titleBar);
            borderPane.setLeft(sidebar);
            borderPane.setCenter(content);

            this.root = borderPane;
            showView(borderPane);
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch to calendar menu", e);
        }
    }

    public void setRoot(Parent root) {
        this.root = root;
    }
}