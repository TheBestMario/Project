package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class Server implements AutoCloseable {
    private static final int DEFAULT_PORT = 8766;
    private static final int MAX_POOL_SIZE = 200;
    
    private final ExecutorService connectionPool;
    private ServerSocket serverSocket;
    private volatile boolean isRunning;
    private final Database database;

    public Server() {
        this.connectionPool = Executors.newFixedThreadPool(MAX_POOL_SIZE);
        this.database = Database.getInstance();
        this.isRunning = false;
    }

    public static void main(String[] args) {
        try (Server server = new Server()) {
            server.start();
        } catch (Exception e) {
            System.out.println("Failed to start server");
            System.exit(1);
        }
    }

    public void start() {
        if (isRunning) {
            System.out.println("Server is already running");
            return;
        }

        try {
            DockerManager.getInstance().startContainer();
            database.connect();
            
            serverSocket = new ServerSocket(DEFAULT_PORT);
            isRunning = true;
            System.out.println("Server started on port " + DEFAULT_PORT);

            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleNewClient(clientSocket);
                } catch (IOException e) {
                    if (isRunning) {
                        System.out.println("Error accepting client connection");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting server");
        }
    }

    private void handleNewClient(Socket clientSocket) {
        Connector clientHandler = new Connector(clientSocket, database);
        connectionPool.execute(clientHandler);
        System.out.println("New client connected from " + clientSocket.getInetAddress().getHostAddress());
    }

    @Override
    public void close() {
        shutdown();
    }

    private void shutdown() {
        isRunning = false;
        System.out.println("Shutting down server...");
        
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Error closing server socket");
        }

        connectionPool.shutdown();
        try {
            if (!connectionPool.awaitTermination(60, TimeUnit.SECONDS)) {
                connectionPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            connectionPool.shutdownNow();
        }
        database.close();
        DockerManager.getInstance().stopContainer();
        System.out.println("Server shutdown complete");
    }
} 