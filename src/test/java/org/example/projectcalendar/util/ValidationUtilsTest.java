package org.example.projectcalendar.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.example.projectcalendar.service.ValidationException;
import org.example.projectcalendar.service.ValidationUtils;
import org.junit.jupiter.api.Test;

class ValidationUtilsTest {
    /*
    since we're using exceptions to handle validation,
    we use assertDoesNotThrow to check if the method succeeds
    and assertThrows to check if the method doesn't succeed (throws an exception)
     */
    @Test
    void validateUsername_WithValidUsername_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("validUser123"));
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("user_name"));
        assertDoesNotThrow(() -> ValidationUtils.validateUsername("user-name"));
    }

    @Test
    void validateUsername_WithInvalidUsername_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> ValidationUtils.validateUsername(""));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateUsername(null));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateUsername("a".repeat(51)));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateUsername("invalid@username"));
    }

    @Test
    void validateEmail_WithValidEmail_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> ValidationUtils.validateEmail("user.name+tag@domain.com"));
    }

    @Test
    void validateEmail_WithInvalidEmail_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail(""));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail(null));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail("invalidemail"));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEmail("invalid@"));
    }

    @Test
    void validatePassword_WithValidPassword_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationUtils.validatePassword("ValidPass123"));
        assertDoesNotThrow(() -> ValidationUtils.validatePassword("StrongP@ssw0rd"));
    }

    @Test
    void validatePassword_WithInvalidPassword_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> ValidationUtils.validatePassword("short")); // too short
        assertThrows(ValidationException.class, () -> ValidationUtils.validatePassword("nouppercase123")); // no uppercase
        assertThrows(ValidationException.class, () -> ValidationUtils.validatePassword("NOLOWERCASE123")); // no lowercase
        assertThrows(ValidationException.class, () -> ValidationUtils.validatePassword("NoNumbers")); // no numbers
        assertThrows(ValidationException.class, () -> ValidationUtils.validatePassword(null));
    }

    @Test
    void validateEventTitle_WithValidTitle_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationUtils.validateEventTitle("Valid Event Title"));
        assertDoesNotThrow(() -> ValidationUtils.validateEventTitle("A"));
    }

    @Test
    void validateEventTitle_WithInvalidTitle_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEventTitle(""));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEventTitle(null));
        assertThrows(ValidationException.class, () -> ValidationUtils.validateEventTitle("a".repeat(101)));
    }

    @Test
    void validateEventDescription_WithValidDescription_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ValidationUtils.validateEventDescription(null)); // null is valid for description
        assertDoesNotThrow(() -> ValidationUtils.validateEventDescription("Valid description"));
        assertDoesNotThrow(() -> ValidationUtils.validateEventDescription("a".repeat(500)));
    }

    @Test
    void validateEventDescription_WithInvalidDescription_ShouldThrowException() {
        assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateEventDescription("a".repeat(501)));
    }
} 