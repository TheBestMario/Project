package org.example.projectcalendar;

import org.example.projectcalendar.controllers.BaseController;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

public abstract class Controller implements BaseController {
    protected MenuHandler menuHandler;
    protected ConnectionService connectionService;
    protected LocalDatabaseStorage localStorage;
    private boolean initialized = false;
    /*
    pseudo global variables set for each controller
    add new ones here and in the checkInitialize method
    Override onDependenciesSet by children for post initialisation procedures
     */

    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
        checkInitialize();
    }

    @Override
    public void setLocalStorage(LocalDatabaseStorage localStorage) {
        this.localStorage = localStorage;
        checkInitialize();
    }

    @Override
    public void setConnectionService(ConnectionService connectionService) {
        this.connectionService = connectionService;
        checkInitialize();
    }

    protected void checkInitialize() {
        if (!initialized && 
            menuHandler != null && 
            localStorage != null && 
            connectionService != null) {
            initialized = true;
            System.out.println("finished initialising");
            onDependenciesSet();
        }
    }

    @Override
    public void onDependenciesSet() {
        // Default empty implementation
    }

    public MenuHandler getMenuHandler() { return menuHandler; }
    public LocalDatabaseStorage getLocalStorage() { return localStorage; }
    public ConnectionService getConnectionService() { return connectionService; }
}