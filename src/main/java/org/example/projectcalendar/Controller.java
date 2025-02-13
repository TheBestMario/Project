package org.example.projectcalendar;

import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

import javafx.fxml.FXML;
import javafx.scene.Parent;

public abstract class Controller {

    @FXML // Mark as FXML injectable if using Scene Builder
    private Parent root;
    private final Set<DependencyStatus> initializedDependencies = EnumSet.noneOf(DependencyStatus.class);
    private boolean initialized = false;

    // Use enum to track dependencies
    private enum DependencyStatus {
        MENU_HANDLER,
        ROOT,
        LOCAL_STORAGE,
        CONNECTION_SERVICE,
        CONNECTION_THREAD
    }

    private MenuHandler menuHandler;
    private ConnectionService connectionService;
    private Thread connectionThread;
    private LocalDatabaseStorage localStorage;

    public void setMenuHandler(MenuHandler menuHandler) {
        Objects.requireNonNull(menuHandler, "MenuHandler cannot be null");
        this.menuHandler = menuHandler;
        markDependencyInitialized(DependencyStatus.MENU_HANDLER);
    }

    public void setRoot(Parent root) {
        this.root = root;
        markDependencyInitialized(DependencyStatus.ROOT);
    }

    public void setLocalStorage(LocalDatabaseStorage localStorage) {
        this.localStorage = localStorage;
        markDependencyInitialized(DependencyStatus.LOCAL_STORAGE);
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
        markDependencyInitialized(DependencyStatus.CONNECTION_SERVICE);
    }

    public void setConnectionThread(Thread connectionThread) {
        this.connectionThread = connectionThread;
        markDependencyInitialized(DependencyStatus.CONNECTION_THREAD);
    }

    public Thread getConnectionThread(){
        return connectionThread;
    }

    protected void checkInitialize() {
        synchronized(initializedDependencies) {
            if (!initialized && initializedDependencies.size() == DependencyStatus.values().length) {
                initialized = true;
                System.out.println("Controller initialization complete");
                onDependenciesSet();
            }
        }
    }

    private void markDependencyInitialized(DependencyStatus status) {
        synchronized(initializedDependencies) {
            initializedDependencies.add(status);
            checkInitialize();
        }
    }

    // Make abstract to force implementation
    protected abstract void onDependenciesSet();

    public MenuHandler getMenuHandler() { return menuHandler; }
    public Parent getRoot() { return root; }
    public LocalDatabaseStorage getLocalStorage() { return localStorage; }
    public ConnectionService getConnectionService() { return connectionService; }
}