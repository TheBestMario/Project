package org.example.projectcalendar.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.HashUtils;
import org.example.projectcalendar.service.ValidationException;
import org.example.projectcalendar.service.ValidationUtils;

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
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                ValidationUtils.validateEmail(newValue);
                showValidationSuccess(informationLabelEmail, "Valid email");
            } catch (ValidationException e) {
                showValidationError(informationLabelEmail, e.getMessage());
            }
        });

        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                ValidationUtils.validatePassword(newValue);
                showValidationSuccess(informationLabelPassword, "Password meets requirements");
            } catch (ValidationException e) {
                showValidationError(informationLabelPassword, e.getMessage());
            }
        });

        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                ValidationUtils.validateUsername(newValue);
                // Only check uniqueness if the basic validation passes
                if (connectionService != null) {  // Make sure service is available
                    ValidationUtils.validateUsernameUnique(newValue, connectionService);
                    showValidationSuccess(warningLabelUsername, "Username is available");
                }
            } catch (ValidationException e) {
                showValidationError(warningLabelUsername, e.getMessage());
            }
        });

        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(passwordField.getText())) {
                showValidationSuccess(informationLabelCPassword, "Passwords match");
            } else {
                showValidationError(informationLabelCPassword, "Passwords do not match");
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

    private void showValidationError(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: red");
    }

    private void showValidationSuccess(Label label, String message) {
        label.setText(message);
        label.setStyle("-fx-text-fill: green");
    }

    @FXML
    protected void onRegisterButtonClick() {
        boolean isValid = true;
        
        // Clear previous validation messages
        warningLabelUsername.setText("");
        informationLabelEmail.setText("");
        informationLabelPassword.setText("");
        informationLabelCPassword.setText("");

        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        try {
            ValidationUtils.validateUsername(username);
            ValidationUtils.validateUsernameUnique(username, getConnectionService());
            showValidationSuccess(warningLabelUsername, "Username is available");
        } catch (ValidationException e) {
            showValidationError(warningLabelUsername, e.getMessage());
            isValid = false;
        }

        try {
            ValidationUtils.validateEmail(email);
            showValidationSuccess(informationLabelEmail, "Email is valid");
        } catch (ValidationException e) {
            showValidationError(informationLabelEmail, e.getMessage());
            isValid = false;
        }

        try {
            ValidationUtils.validatePassword(password);
            showValidationSuccess(informationLabelPassword, "Password meets requirements");
        } catch (ValidationException e) {
            showValidationError(informationLabelPassword, e.getMessage());
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            showValidationError(informationLabelCPassword, "Passwords do not match");
            isValid = false;
        } else {
            showValidationSuccess(informationLabelCPassword, "Passwords match");
        }

        if (isValid) {
            try {
                // Proceed with registration
                String salt = HashUtils.generateSalt();
                String hashedPassword = HashUtils.hashPassword(password, salt);
                
                if (getConnectionService().createProfile(username, email, hashedPassword, salt)) {
                    // Registration successful
                    getMenuHandler().setNodeToRoot("Initial/login-view.fxml");
                } else {
                    showValidationError(warningLabelUsername, "Registration failed. Please try again.");
                }
            } catch (Exception e) {
                showValidationError(warningLabelUsername, "An error occurred during registration");
                e.printStackTrace();
            }
        }
    }

    @FXML
    protected void onAccountCreatedReturnButtonPressed(){
        getMenuHandler().setNodeToRoot("Initial/login-view.fxml");
    }
}
