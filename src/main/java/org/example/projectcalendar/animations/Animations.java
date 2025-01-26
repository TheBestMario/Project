package org.example.projectcalendar.animations;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class Animations {

    public static void applyLeftTransition(Node rootPane, double fromX, double toX, Runnable onFinished) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(500), rootPane);
        translateTransition.setFromX(fromX);
        translateTransition.setToX(toX);
        translateTransition.setOnFinished(event -> {
            onFinished.run();
            rootPane.setTranslateX(0);
        });
        translateTransition.play();
    }
}
