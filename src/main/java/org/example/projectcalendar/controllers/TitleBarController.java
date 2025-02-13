package org.example.projectcalendar.controllers;

import java.net.URL;
import java.util.ResourceBundle;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.User.Profile;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

public class TitleBarController extends Controller implements Initializable {
    @FXML
    private Label usernameLabel;
    private Profile profile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        profile = Profile.getInstance();

        usernameLabel.setText(profile.getUserName());
    }

    @Override
    public void onDependenciesSet() {}
}
