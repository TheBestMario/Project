package org.example.projectcalendar.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ConnectionService {
    private String serverAddress;
    private Scanner in;
    private PrintWriter out;
    private int serverPort;

    public ConnectionService(String serverAddress, int port){
        this.serverAddress = serverAddress;
        this.serverPort = port;


    }

    public void run() {
        try {
            Socket socket = new Socket(serverAddress, serverPort);
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
            while (in.hasNextLine()) {
                System.out.println(in.nextLine());
            }
            out.println("HELLO SERVER");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
