package org.example.projectcalendar;

import javafx.scene.Parent;
import org.example.projectcalendar.service.ConnectionService;
import org.example.projectcalendar.service.MenuHandler;

public abstract class Controller {
    private MenuHandler menuHandler;
    private Parent root;
    private ConnectionService connectionService;
    private Thread connectionThread;


    public void setMenuHandler(MenuHandler menuHandler){
        this.menuHandler = menuHandler;

    }
    public void setRoot(Parent root){
        this.root = root;
    }
    public MenuHandler getMenuHandler(){
        return menuHandler;
    }
    public Parent getRoot(){
        return root;
    }
    public void setConnectionService(ConnectionService connectionService){
        this.connectionService = connectionService;
    }
    public ConnectionService getConnectionService(){
        return connectionService;
    }

    public void setConnectionThread(Thread connectionThread) {
        this.connectionThread = connectionThread;
    }
}
