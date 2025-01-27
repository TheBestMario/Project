package org.example.projectcalendar.controllers;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;
import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.User.Profile;

public class RegisterViewController extends Controller {

    public CheckBox policyCheckBox;
    public Button submitButton;
    public TextField usernameField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Label informationLabel;
    public Node rootPane;

    @FXML
    protected void onBackButtonClicked() {
        try {
            //animates and switches scenes after finishing animation
            getMenuHandler().addNodeToRoot("start-view.fxml");
            Node startViewScene = getMenuHandler().getNodeFromRoot("start-view.fxml");
            this.rootPane = getMenuHandler().getNodeFromRoot("register-view.fxml");
            startViewScene.setLayoutX(-rootPane.getLayoutX());

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), startViewScene);
            currentTransition.setFromX(-rootPane.getBoundsInParent().getWidth());
            currentTransition.setToX(0);

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), rootPane);
            nextTransition.setFromX(0);
            nextTransition.setToX(rootPane.getBoundsInParent().getWidth() + startViewScene.getTranslateX());

            nextTransition.setOnFinished(event -> {
                getMenuHandler().getRoot().getChildren().remove(rootPane);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition, nextTransition);
            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick() {

        informationLabel.setText("");
        informationLabel.setStyle("-fx-text-fill: red");
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmedPassword = confirmPasswordField.getText();

        boolean passwordMatches = false;
        boolean userChecked = false;
        //Map<String, Boolean> map = new HashMap<>();
        boolean checkBoxState = policyCheckBox.isSelected();


        if (username.isBlank()){
            System.out.println("You must fill in the username field.");
            informationLabel.setText("You must fill in the username field.");
        } else if (username.length() > 20) {
            System.out.println("username is too long, it must be less than 20 characters.");
            informationLabel.setText("Username is too long");
        } else if (Profile.checkUserNameExists(username)) {
            System.out.println("username already exists");
            informationLabel.setText("Username already exists.");
        } else{
            userChecked = true;
        }

        if (password.isBlank()){
            System.out.println("please fill in the password field");
            informationLabel.setText("please fill in the password field");

        } else if (password.length() < 5) {
            System.out.println("password is too short, must be more than 5 characters");
            informationLabel.setText("password is too short, must be more than 5 characters");
        } else if (password.equals(confirmedPassword)){
            passwordMatches = true;
        } else if (!password.equals(confirmedPassword)){
            System.out.println("Passwords must match");
            informationLabel.setText("Passwords must match");
        }

        if (!checkBoxState){
            System.out.println("You must agree to our policies");
            informationLabel.setText("You must agree to our policies");
        }



        if (userChecked && passwordMatches && checkBoxState){
            informationLabel.setStyle("-fx-text-fill: green");
            informationLabel.setText("Creating account...");

            Profile.addProfile(new Profile(username,password));


            System.out.println("switch scene");

        }
    }
}
