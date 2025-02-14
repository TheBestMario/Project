package org.example.projectcalendar.service;

public class ValidationUtils {
    /*
    https://www.highlight.io/blog/3-levels-of-data-validation-in-a-full-stack-application-with-react

    I decided to quickly change the user input validation so that there are common rules and can be used
    on all ends of the application

     */
    public static void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (username.length() > 50) {
            throw new ValidationException("Username cannot be longer than 50 characters");
        }
        if (!username.matches("^[a-zA-Z0-9_-]{3,50}$")) {
            throw new ValidationException("Username can only contain letters, numbers, underscores, and hyphens");
        }
    }

    public static void validateEmail(String email) {
        /*
        https://stackoverflow.com/questions/201323/how-can-i-validate-an-email-address-using-a-regular-expression
        Realistically this would send an email for confirmation but for now we'll just check format.
         */
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format");
        }
    }

    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must be at least 8 characters");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new ValidationException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new ValidationException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*\\d.*")) {
            throw new ValidationException("Password must contain at least one number");
        }
    }

    public static void validateEventTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Event title cannot be empty");
        }
        if (title.length() > 100) {
            throw new ValidationException("Event title cannot be longer than 100 characters");
        }
    }

    public static void validateEventDescription(String description) {
        if (description != null && description.length() > 500) {
            throw new ValidationException("Event description cannot be longer than 500 characters");
        }
    }

    public static void validateUsernameUnique(String newValue, ConnectionService connectionService) {
        if (connectionService.checkUsernameExists(newValue)) {
            throw new ValidationException("Username is already taken");
        }
    }
} 