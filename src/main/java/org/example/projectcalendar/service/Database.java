package org.example.projectcalendar.service;

import java.io.BufferedReader;
import java.io.FileReader;
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

    public Database() {
        this.dbUsername = System.getenv("DB_USERNAME");
        this.dbPassword = System.getenv("DB_PASSWORD");
        String port = getPortFromEnv();

        try {
            Path path = Paths.get(System.getProperty("user.dir"), "script.ps1");
            System.out.println(path);
            String command = "powershell.exe -File \"" + path + "\"";
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", command);
            Map<String, String> env = processBuilder.environment();
            env.put("DB_USERNAME", dbUsername);
            env.put("DB_PASSWORD", dbPassword);
            env.put("LOCAL_PORT",port);
            Process process = processBuilder.start();

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

        this.connectionUrl = "jdbc:sqlserver://localhost:" + port + ";databaseName=BankingAppDB;user=" + this.dbUsername + ";password=" + this.dbPassword + ";encrypt=false;";
    }

    private String getPortFromEnv() {
        String port = "8765";
        return port;
    }
}