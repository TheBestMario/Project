package org.example.projectcalendar.controllers;

import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.HashUtils;

import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class RegisterViewController extends Controller implements Initializable {

    public CheckBox policyCheckBox;
    public Button submitButton;
    public TextField usernameField;
    public PasswordField passwordField;
    public PasswordField confirmPasswordField;
    public Label informationLabelEmail;
    public Label informationLabelPassword;
    public Label informationLabelCPassword;
    public Label warningLabelUsername;
    public Node rootPane;
    public TextField emailField;
    private ConnectionService connectionService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        emailField.textProperty().addListener(ChangeListener -> {
            if (!emailField.getText().contains("@")) {
                informationLabelEmail.setText("Not a valid email.");
                informationLabelEmail.setStyle("-fx-text-fill: red");
            } else {
                informationLabelEmail.setText("Valid email.");
                informationLabelEmail.setStyle("-fx-text-fill: green");
            }
        });
        // Add a listener to the password field to check the password length
        passwordField.textProperty().addListener(ChangeListener -> {
            if (passwordField.getText().length() < 5) {
                informationLabelPassword.setText("Password must be at least 5 characters long.");
                informationLabelPassword.setStyle("-fx-text-fill: red");
            } else {
                informationLabelPassword.setText("Password length is sufficient.");
                informationLabelPassword.setStyle("-fx-text-fill: green");
            }

        });
        confirmPasswordField.textProperty().addListener(ChangeListener -> {
            if (!confirmPasswordField.getText().equals(passwordField.getText())) {
                informationLabelCPassword.setText("Passwords do not match.");
                informationLabelCPassword.setStyle("-fx-text-fill: red");
            } else {
                informationLabelCPassword.setText("Passwords match.");
                informationLabelCPassword.setStyle("-fx-text-fill: green");
            }
        });
    }

    @Override
    public void setConnectionService(ConnectionService connectionService){
        this.connectionService = connectionService;
    }

    @FXML
    protected void onBackButtonClicked() {
        try {
            //animates and switches scenes after finishing animation
            getMenuHandler().addNodeToRoot("Initial/login-view.fxml");
            Node nextScene = getMenuHandler().getNodeFromRoot("login-view");
            this.rootPane = getMenuHandler().getNodeFromRoot("register-view");
            nextScene.setLayoutX(-getMenuHandler().getPrimaryStage().getWidth());

            //transition for first 'scene'
            TranslateTransition currentTransition = new TranslateTransition(Duration.millis(500), nextScene);
            currentTransition.setFromX(-getMenuHandler().getPrimaryStage().getWidth());
            currentTransition.setToX(0);

            //transition for second 'scene'
            TranslateTransition nextTransition = new TranslateTransition(Duration.millis(500), rootPane);
            nextTransition.setFromX(0);
            nextTransition.setToX(getMenuHandler().getPrimaryStage().getWidth());

            StackPane root = (StackPane) getMenuHandler().getRoot();
            nextTransition.setOnFinished(event -> {
                root.getChildren().remove(rootPane);
            });

            ParallelTransition parallelTransition = new ParallelTransition(
                    currentTransition, nextTransition);
            parallelTransition.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onRegisterButtonClick(){
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmedPassword = confirmPasswordField.getText();
        String email = emailField.getText();

        boolean passwordMatches = false;
        boolean userChecked = false;
        boolean validEmail = false;
        //Map<String, Boolean> map = new HashMap<>();
        boolean checkBoxState = policyCheckBox.isSelected();


        if (username.isBlank()){
        } else if (username.length() > 20) {
        } else if (connectionService.getUserNameExists(username)) {
            warningLabelUsername.setText("Username already exists");
        } else{
            warningLabelUsername.setText("");
            userChecked = true;
        }

        if (password.isBlank()){
            System.out.println("please fill in the password field");
        } else if (password.length() < 5) {
            System.out.println("password is too short, must be more than 5 characters");
        } else if (password.equals(confirmedPassword)){
            passwordMatches = true;
        } else if (!password.equals(confirmedPassword)){
            System.out.println("Passwords must match");
        }

        if (!email.contains("@")){
        } else{
            validEmail = true;
        }

        if (!checkBoxState){
            System.out.println("You must agree to our policies");
        }



        if (userChecked && passwordMatches && checkBoxState && validEmail){
            try {
                String salt = HashUtils.generateSalt();
                String hashedPassword = HashUtils.hashPassword(password,salt);
                if (connectionService.createProfile(username,email,hashedPassword,salt)){
                    getLocalStorage().saveUsernamePassword(username,email,hashedPassword,salt);

                    getMenuHandler().setNodeToRoot("Initial/account-created-view.fxml");

                }else{
                    System.out.println("failed to create profile");
                }

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }



        }
    }
    @FXML
    protected void onAccountCreatedReturnButtonPressed(){
        getMenuHandler().setNodeToRoot("Initial/login-view.fxml");
    }
}
