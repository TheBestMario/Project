package org.example.projectcalendar.controllers;

import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.LocalDatabaseStorage;
import org.example.projectcalendar.service.MenuHandler;

public interface BaseController {
    void setMenuHandler(MenuHandler menuHandler);
    void setLocalStorage(LocalDatabaseStorage storage);
    void setConnectionService(ConnectionService service);
    void onDependenciesSet();
} 