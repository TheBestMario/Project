package org.example.projectcalendar.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.example.projectcalendar.service.User.Profile;

public class ConnectionService implements AutoCloseable, Runnable {
    private static final int RESPONSE_TIMEOUT_SECONDS = 1;
    
    private final BlockingQueue<String> responseQueue;
    private final AtomicBoolean isRunning;
    private volatile Socket socket;
    private volatile BufferedReader in;
    private volatile PrintWriter out;

    public ConnectionService(String serverAddress, int port) {
        this.responseQueue = new LinkedBlockingQueue<>();
        this.isRunning = new AtomicBoolean(true);
        initializeConnection(serverAddress, port);
    }

    private void initializeConnection(String address, int port) {
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Failed to initialize connection");
        }
    }

    @Override
    public void close() {
        isRunning.set(false);
        closeQuietly(in);
        closeQuietly(out);
        closeQuietly(socket);
    }

    private void closeQuietly(AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception e) {
            System.out.println("Error closing resource");
        }
    }

    public static void connect(){

    }

    @Override
    public void run() {
        // Add connection logic here
        while (!Thread.currentThread().isInterrupted()) {
            checkConnection();
            try {
                Thread.sleep(1000); // Check connection every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public boolean checkConnection() {
        if (socket == null) {
            return false;
        }
        return socket.isConnected();
    }

    public void setServerAddress(String serverAddress) {
        // Implementation needed
    }
    public String getServerAddress() {
        // Implementation needed
        return null;
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

    private String waitForResponse() {
        try {
            System.out.println("before join");
            String response = responseQueue.poll(RESPONSE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            System.out.println("after join join");
            return response;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}