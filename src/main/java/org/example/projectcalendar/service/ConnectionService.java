package org.example.projectcalendar.service;

import org.example.projectcalendar.service.User.Profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ConnectionService implements Runnable {
    private String serverAddress;
    private BufferedReader in;
    private PrintWriter out;
    private int serverPort;
    private Socket socket;
    private BlockingQueue<String> responseQueue = new LinkedBlockingQueue<>();

    public ConnectionService(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.serverPort = port;
    }

    public void run() {
        try {
            socket = new Socket(serverAddress, serverPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            String line;
            while ((line = in.readLine()) != null) {
                responseQueue.put(line);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            System.out.println("No server detected, continuing ");
        }
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Waits for a response from the server for 1 second
     * I had a problem synchronising threads, I was trying to use thread.join()
     * but because the app works on main it means that it crashes as it waits for the thread
     * with the connection to finish. I looked online and saw that you can give it leeway through
     * the blockingqueue object which is similar to queue.
     */
    private String waitForResponse() {
        try {
            System.out.println("before join");
            String response = responseQueue.poll(1, TimeUnit.SECONDS);
            System.out.println("after join join");
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public boolean sendLoginRequest(String username, String password) {
        try {
            out.println("LOGIN " + username + " " + password);
            String response = waitForResponse();
            if (response.startsWith("LOGIN_SUCCESS")) {
                String[] parts = response.split(" ");
                if (parts.length == 3) {
                    username = parts[1];
                    String email = parts[2];

                    Profile.getInstance().setUsername(username);
                    Profile.getInstance().setEmail(email);
                    System.out.println("profile updated");
                }
                return true;
            } else if (response.equals("LOGIN_FAILURE")) {
                System.out.println("credentials don't match on the system");
            } else if (response.equals("INVALID_LOGIN_REQUEST")) {
                System.out.println("Request to login rejected by server");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean getUserNameExists(String username){
        out.println("CHECK_USERNAME_EXISTS " + username);
        String response = waitForResponse();
        if (response != null && response.equals("USERNAME_EXISTS")) {
            return true;
        } else {
            return false;
        }
    }

    public String getSalt(String username) {
        try {
            out.println("GET_SALT " + username);
            String response = waitForResponse();
            if (response != null && response.startsWith("SALT ")) {
                return response.substring(5);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getHashedPassword(String username) {
        try {
            out.println("GET_HASHED_PASSWORD " + username);
            String response = waitForResponse();
            if (response != null && response.startsWith("HASHED_PASSWORD ")) {
                return response.substring(16);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createProfile(String username, String email, String hashedPassword, String salt) {
        try{
            out.println("SAVE_USER_PROFILE_TO_DB "+username+" "+email+" "+hashedPassword+" "+salt);
            String response = waitForResponse();
            if (response != null && response.startsWith("PROFILE_CREATED")){
                return true;
            }else return false;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}