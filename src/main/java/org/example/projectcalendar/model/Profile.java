package org.example.projectcalendar.model;

import java.util.ArrayList;
import java.util.Calendar;

import org.example.projectcalendar.service.ValidationUtils;

public class Profile {
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private static Profile instance;
    private boolean loggedIn = false;
    private ArrayList<Calendar> calendars;
    private int userId;


    public Profile(){}

    public static Profile getInstance() {
        if (instance == null) {
            instance = new Profile();
        }
        return instance;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return username;
    }

    public void setUsername(String username) {
        ValidationUtils.validateUsername(username);
        this.username = username;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email) {
        ValidationUtils.validateEmail(email);
        this.email = email;
    }

    public void setPassword(String password) {
        ValidationUtils.validatePassword(password);
        this.password = password;
    }

    public boolean checkPassword(String password){
        if (this.password == null) {
            return false;
        }
        return this.password.equals(password);
    }



    public String getPassword() {
        return password;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
}
