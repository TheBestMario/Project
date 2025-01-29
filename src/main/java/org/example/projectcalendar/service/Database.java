package org.example.projectcalendar.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private String dbUsername;
    private String dbPassword;
    private String connectionUrl;
    private static Connection con;

    // create a BankingAppDB within your azure data studios
    // DB == BankingAppDB

    public Database() {

        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");

        try {
            /*builds powershell command by finding path of script.ps1 in this project
            * ProcessBuilder sticks it together
            *
            * */
            Path path = Paths.get(System.getProperty("user.dir"),"script.ps1");
            System.out.println(path);
            String command = "powershell.exe -File \""+ path;
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe","/c", command);
            Map<String, String> env = processBuilder.environment();
            env.put("DB_USERNAME",dbUsername);
            env.put("DB_PASSWORD",dbPassword);
            Process process = processBuilder.start();

            // Read the output from the command debugging
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("Exited with code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=BankingAppDB;user=" + this.dbUsername + ";password=" + this.dbPassword + ";encrypt=false;";

    }
}