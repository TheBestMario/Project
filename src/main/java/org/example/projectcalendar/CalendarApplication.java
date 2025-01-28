package org.example.projectcalendar;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.projectcalendar.service.MenuHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CalendarApplication extends javafx.application.Application {

    MenuHandler menuHandler;
    @Override
    public void start(Stage stage) throws IOException {
        stage.setMinWidth(500);
        stage.setMinHeight(500);

        //initialises menuHandler with first scene, found in constructor
        this.menuHandler = new MenuHandler(stage);

    }

    public static void ConnectToDB(){
        try {
            Path path = Paths.get(System.getProperty("user.dir"),"script.ps1");
            System.out.println(path);

            String command = "powershell.exe -File \""+ path;

            System.out.println(command);

            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe","/c", command);

            Process process = processBuilder.start();

            // Read the output from the command debugging
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        ConnectToDB();
        launch();
    }
}