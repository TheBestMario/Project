package org.example.projectcalendar.controllers;

import org.example.projectcalendar.Controller;
import org.example.projectcalendar.service.MenuHandler;

public class CalendarViewController extends Controller {
    private MenuHandler menuHandler;
    @Override
    public void setMenuHandler(MenuHandler menuHandler) {
        this.menuHandler = menuHandler;
    }
}
