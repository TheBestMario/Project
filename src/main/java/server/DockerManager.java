package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;


public class DockerManager {
    private static volatile DockerManager instance;
    
    private final String containerName;
    private final int dbPort;

    private DockerManager() {
        this.containerName = "calendarDB";
        this.dbPort = 8766;

    }

    public static DockerManager getInstance() {
        if (instance == null) {
            synchronized (DockerManager.class) {
                if (instance == null) {
                    instance = new DockerManager();
                }
            }
        }
        return instance;
    }

    public void startContainer() {
        if (!isContainerRunning()) {
            executeDockerCommand("docker run -d --name " + containerName + " -p " + dbPort + ":1433 -e ...");
            waitForContainer();
        }
    }

    public void stopContainer() {
        if (isContainerRunning()) {
            executeDockerCommand("docker stop " + containerName);
            executeDockerCommand("docker rm " + containerName);
        }
    }

    private boolean isContainerRunning() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "ps", "-q", "-f", "name=" + containerName);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String containerId = reader.readLine();
            return containerId != null && !containerId.isEmpty();
        } catch (IOException e) {
            System.out.println(e);
            System.out.println("Failed to check container status");
            return false;
        }
    }

    private void waitForContainer() {
        int maxAttempts = 30;
        int attempts = 0;
        while (attempts < maxAttempts) {
            try {
                Process process = Runtime.getRuntime().exec(
                    "docker exec " + containerName + " /opt/mssql-tools/bin/sqlcmd -S localhost -U " + 
                    System.getenv("DB_USERNAME") + " -P " + System.getenv("DB_PASSWORD") + " -Q \"SELECT 1\""
                );
                if (process.waitFor() == 0) {
                    System.out.println("Database container is ready");
                    return;
                }
                Thread.sleep(1000);
                attempts++;
            } catch (Exception e) {
                System.out.println("Waiting for database container... Attempt " + attempts + "/" + maxAttempts);
                attempts++;
            }
        }
        throw new ContainerInitializationException("Database container failed to start in time");
    }

    private void executeDockerCommand(String command) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", command);
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String error = errorReader.lines().collect(Collectors.joining("\n"));
                throw new DockerCommandException("Docker command failed: " + error);
            }
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new DockerCommandException("Failed to execute docker command: " + command, e);
        }
    }

    public static class DockerCommandException extends RuntimeException {
        public DockerCommandException(String message) {
            super(message);
        }

        public DockerCommandException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ContainerInitializationException extends RuntimeException {
        public ContainerInitializationException(String message) {
            super(message);
        }
    }
} 