package org.example.projectcalendar.service;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.projectcalendar.CalendarApplication;
import org.example.projectcalendar.Controller;

import java.io.IOException;

public class MenuHandler extends Node {

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
        //makes root component in the heirarchy
        //initialises scene to the root
        this.root = new StackPane();
        root.setId("root");
        Scene scene = new Scene(root, width, height);

        /*loads start view to the root children i.e.

               root
                 -> vbox (start-view.fxml)
                            -> label
                            -> textfield
                            -> etc.
         */

        root.getChildren().add(loader.load());
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-image: url(/static/images/robert_walters_logo.jpeg)");


        Controller controller = loader.getController();
        controller.setMenuHandler(this);
        controller.setRoot(root);

        this.stylesheet = CalendarApplication.class.getResource("/static/calendar.css").toExternalForm();
        scene.getStylesheets().add(stylesheet);

        primaryStage = stage;
        primaryStage.setScene(scene);
        primaryStage.setTitle("Calendar");
        primaryStage.show();

    }
    public void setNodeToRoot(String fxml) throws IOException {
        loader = new FXMLLoader(CalendarApplication.class.getResource(fxml));
        root.getChildren().clear();
        root.getChildren().add(loader.load());
        Controller controller = loader.getController();
        controller.setMenuHandler(this);
        controller.setRoot(root);
    }

    public void addNodeToRoot(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(CalendarApplication.class.getResource(fxml));
        root.getChildren().add(loader.load());

    }
    public Node getNodeFromRoot(String fxml){
        ObservableList<Node> scenesList = root.getChildren();
        for (Node scene : scenesList){

            if (scene.getProperties().get("file").equals(fxml)){
                System.out.println("found "+fxml);
                return scene;
            }
        }
        return null;
    }
    public StackPane getRoot(){
        return this.root;
    }

}