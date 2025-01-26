package org.example.projectcalendar;

import javafx.scene.Parent;
import org.example.projectcalendar.service.MenuHandler;

public abstract class Controller {
    private MenuHandler menuHandler;
    private Parent root;


    public void setMenuHandler(MenuHandler menuHandler){
        this.menuHandler = menuHandler;

    }
    public void setRoot(Parent root){
        this.root = root;
    }
    protected Parent getRoot(){
        return root;
    }
}
