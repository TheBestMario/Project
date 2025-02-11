package org.example.projectcalendar.service;

import java.util.prefs.Preferences;

public class CredentialStorage {
    /*
    uses Preferences API to store credentials locally and securely,
    I was debating just throwing them into a .txt file but malware could access that

     */
    private static Preferences prefs = Preferences.userRoot().node("org.example.projectcalendar");

    public static void saveCredentials(String username, String password) {
        prefs.put("savedUsername", username);
        prefs.put("savedPassword", password);
    }

    public static String getSavedUsername() {
        return prefs.get("savedUsername", null);
    }

    public static String getSavedPassword() {
        return prefs.get("savedPassword", null);
    }

    public static void clearCredentials() {
        prefs.remove("savedUsername");
        prefs.remove("savedPassword");
    }
}
