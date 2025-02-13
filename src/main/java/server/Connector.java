package server;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Connector implements Runnable {
    private final Socket clientSocket;
    private Scanner in;
    private PrintWriter out;
    private Database database;

    public Connector(Socket socket, Database database) {
        this.clientSocket = socket;
        this.database = database;
    }
    public void run() {
        try {
            System.out.println(clientSocket.getInetAddress().getHostAddress());
            in = new Scanner(clientSocket.getInputStream());
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            while (in.hasNextLine()) {
                String message = in.nextLine();

                if (message.startsWith("LOGIN")) {
                    handleLogin(message);
                } else if (message.startsWith("GET_SALT")) {
                    handleGetSalt(message);
                } else if (message.startsWith("GET_HASHED_PASSWORD")) {
                    handleGetHashedPassword(message);
                } else if (message.startsWith("CHECK_USERNAME_EXISTS")){
                    handleGetUserName(message);
                } else if (message.startsWith("SAVE_USER_PROFILE_TO_DB")){
                    handleCreateProfileToDB(message);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void handleLogin(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            if (database.verifyCredentials(username,password)) {
                Profile profile = database.getUserFromTable(username,password);
                System.out.println("username "+profile.getUserName());
                out.println("LOGIN_SUCCESS "
                +profile.getUserName()+" "+profile.getEmail());
            } else {
                out.println("LOGIN_FAILURE");
            }
        } else {
            out.println("INVALID_LOGIN_REQUEST");
        }
    }

    private void handleGetSalt(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String username = parts[1];
            String salt = database.getSalt(username);
            out.println("SALT " + salt);
        } else {
            out.println("INVALID_GET_SALT_REQUEST");
        }
    }

    private void handleCreateProfileToDB(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 5) {
            String username = parts[1];
            String email = parts[2];
            String password = parts[3];
            String salt = parts[4];
            database.addUsertoTable(username,email, salt, password);
            out.println("PROFILE_CREATED");
        } else {
            out.println("INVALID_CREATE_PROFILE_REQUEST");
        }
    }

    private void handleGetUserName(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String username = parts[1];
            boolean exists = database.checkUserNameExists(username);
            if (exists) {
                out.println("USERNAME_EXISTS");
            } else {
                out.println("USERNAME_DOES_NOT_EXIST");
            }
        } else {
            out.println("INVALID_CHECK_USERNAME_EXISTS_REQUEST");
        }

    }

    private void handleGetHashedPassword(String message) {
        String[] parts = message.split(" ");
        if (parts.length == 2) {
            String username = parts[1];
            String hashedPassword = database.getHashedPassword(username);
            out.println("HASHED_PASSWORD " + hashedPassword);
        } else {
            out.println("INVALID_GET_HASHED_PASSWORD_REQUEST");
        }
    }

}
