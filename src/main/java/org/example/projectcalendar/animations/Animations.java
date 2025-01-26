package org.example.projectcalendar.animations;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Animations {

    public static void applyLeftTransition(Node currentPane, Node nextPane, double fromX, double toX, Runnable onFinished) {
        TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), currentPane);
        currentTransition.setFromX(fromX);
        currentTransition.setToX(toX);
        System.out.println("fromX: " + fromX + " toX: " + toX);

        TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), nextPane);
        nextTransition.setFromX(nextPane.getLayoutX() + nextPane.getTranslateX());
        nextTransition.setToX(fromX);

        currentTransition.setOnFinished(event -> {
            onFinished.run();
        });

        nextTransition.setOnFinished(event -> {

            nextPane.setTranslateX(0);
        });

        currentTransition.play();
        nextTransition.play();
    }
}