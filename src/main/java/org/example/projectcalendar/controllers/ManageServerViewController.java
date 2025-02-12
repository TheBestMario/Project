package org.example.projectcalendar.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;
import org.example.projectcalendar.Controller;
import server.Database;

import java.io.IOException;
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
    @FXML
    private Label addressInfoLabel;

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
        String address = serverAddressTextField.getText();
        String[] parts = address.split("\\.");
        System.out.println(parts.length);
        if (address.isEmpty()){
            addressInfoLabel.setText("No address provided");
            addressInfoLabel.setStyle("-fx-text-fill: red");

        } else if (parts.length != 4){
            System.out.println("Invalid address");
        } else {
            getConnectionService().setServerAddress(address);
            getConnectionThread().interrupt(); // Stop the current thread if running
            Thread connectionThread = new Thread(getConnectionService()); // Create a new thread with updated address
            setConnectionThread(connectionThread);
            connectionThread.start();
            updateStatus();

        }
    }
    @FXML
    public void onStatusButtonClicked(){
        String address = serverAddressTextField.getText();
        System.out.println(address);
        if (address.isEmpty()){
            addressInfoLabel.setText("No address provided");
            addressInfoLabel.setStyle("-fx-text-fill: red");
        } else {
            addressInfoLabel.setText("Starting server");
            addressInfoLabel.setStyle("-fx-text-fill: green");
            try {
                Thread serverThread = new Thread(() -> {
                    try {
                        Database.main(new String[0]);
                    } catch (Exception e) {
                        addressInfoLabel.setText("server already running");
                        addressInfoLabel.setStyle("-fx-text-fill: orange");
                        throw new RuntimeException(e);
                    }
                });
                serverThread.start();
                addressInfoLabel.setText("Server started with address: " + address);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        updateStatus();
    }
    @FXML
    public void onBackButtonClicked() throws IOException {
        getMenuHandler().setNodeToRoot("Initial/login-view.fxml");
    }
}
