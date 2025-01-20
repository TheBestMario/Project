package org.example.projectcalendar;

import org.example.projectcalendar.service.MenuHandler;

public abstract class Controller {
    private MenuHandler menuHandler;
    public void setMenuHandler(MenuHandler menuHandler){
        this.menuHandler = menuHandler;

    }
}
