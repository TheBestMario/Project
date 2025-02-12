package org.example.projectcalendar;

import javafx.scene.Parent;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

public class Controller {
    private MenuHandler menuHandler;
    private Parent root;
    private ConnectionService connectionService;
    private Thread connectionThread;
    private LocalDatabaseStorage localStorage;
    private boolean initialized = false;
    /*
    pseudo global variables set for each controller
    add new ones here and in the checkInitialize method
    Override onDependenciesSet by children for post initialisation procedures
     */
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
        checkInitialize();
    }

    public void setRoot(Parent root) {
        this.root = root;
        checkInitialize();
    }

    public void setLocalStorage(LocalDatabaseStorage localStorage) {
        this.localStorage = localStorage;
        checkInitialize();
    }

    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
        checkInitialize();
    }

    public void setConnectionThread(Thread connectionThread) {
        this.connectionThread = connectionThread;
        checkInitialize();
    }

    protected void checkInitialize() {
        if (!initialized &&
                menuHandler != null &&
                root != null &&
                localStorage != null &&
                connectionService != null &&
                connectionThread != null) {
            initialized = true;
            System.out.println("finished initialising");
            onDependenciesSet();
        }
    }
    protected void onDependenciesSet() {
    }

    public MenuHandler getMenuHandler() { return menuHandler; }
    public Parent getRoot() { return root; }
    public LocalDatabaseStorage getLocalStorage() { return localStorage; }
    public ConnectionService getConnectionService() { return connectionService; }
}