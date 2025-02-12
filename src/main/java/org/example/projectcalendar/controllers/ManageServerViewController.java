package org.example.projectcalendar.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import org.example.projectcalendar.Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class ManageServerViewController extends Controller implements Initializable {
    @FXML
    private TextField serverAddressTextField;
    @FXML
    private Button addressEditingButton;
    @FXML
    private Circle statusCircle;
    @FXML
    private Button connectButton;
    @FXML
    private Button backButton;
    @FXML
    private Button statusButton;
    @FXML
    private Label statusLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverAddressTextField.setDisable(true);
        connectButton.setVisible(false);

        addressEditingButton.setOnMouseClicked(event -> {
            if (serverAddressTextField.isDisabled()){
                serverAddressTextField.setDisable(false);
                connectButton.setVisible(true);
            } else {
                serverAddressTextField.setDisable(true);
                connectButton.setVisible(false);
            }
        });

    }
    @Override
    protected void onDependenciesSet() {
        updateStatus();
    }

    public void updateStatus() {
        System.out.println("hello");
        if (getConnectionService() == null) {
            statusCircle.setStyle("-fx-fill: red");
            statusLabel.setText("Not Connected");
        }else if(getConnectionService().checkConnection()) {
            statusCircle.setStyle("-fx-fill: green");
            statusLabel.setText("Connected");
        } else {
            statusCircle.setStyle("-fx-fill: red");
            statusLabel.setText("Not Connected");
        }
    }


    public void onConnectButtonClicked() {

    }
    @FXML
    public void onStatusButtonClicked(){

    }
}
