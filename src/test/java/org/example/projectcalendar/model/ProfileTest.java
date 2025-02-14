package org.example.projectcalendar.model;

import org.example.projectcalendar.service.ValidationException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProfileTest {

    private Profile profile;

    @BeforeEach
    void setUp() {
        Profile.getInstance().setUsername("testUser");
        Profile.getInstance().setPassword(null);
        Profile.getInstance().setEmail(null);
        profile = Profile.getInstance();
    }

    @Test
    void getInstance_ShouldReturnSameInstance() {
        Profile instance1 = Profile.getInstance();
        Profile instance2 = Profile.getInstance();
        
        assertSame(instance1, instance2);
    }

    @Test
    void setUsername_WithValidUsername_ShouldWork() {
        String username = "validUser123";
        profile.setUsername(username);
        assertEquals(username, profile.getUserName());
    }

    @Test
    void setUsername_WithInvalidUsername_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> profile.setUsername(""));
        assertThrows(ValidationException.class, () -> profile.setUsername(null));
        assertThrows(ValidationException.class, () -> profile.setUsername("a".repeat(51)));
        assertThrows(ValidationException.class, () -> profile.setUsername("invalid@username"));
    }

    @Test
    void setEmail_WithValidEmail_ShouldWork() {
        String email = "test@example.com";
        profile.setEmail(email);
        assertEquals(email, profile.getEmail());
    }

    @Test
    void setEmail_WithInvalidEmail_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> profile.setEmail(""));
        assertThrows(ValidationException.class, () -> profile.setEmail(null));
        assertThrows(ValidationException.class, () -> profile.setEmail("invalidemail"));
        assertThrows(ValidationException.class, () -> profile.setEmail("invalid@"));
    }

    @Test
    void setPassword_WithValidPassword_ShouldWork() {
        String password = "ValidPass123";
        profile.setPassword(password);
        assertEquals(password, profile.getPassword());
    }

    @Test
    void setPassword_WithInvalidPassword_ShouldThrowException() {
        assertThrows(ValidationException.class, () -> profile.setPassword("short")); // too short
        assertThrows(ValidationException.class, () -> profile.setPassword("nouppercase123")); // no uppercase
        assertThrows(ValidationException.class, () -> profile.setPassword("NOLOWERCASE123")); // no lowercase
        assertThrows(ValidationException.class, () -> profile.setPassword("NoNumbers")); // no numbers
        assertThrows(ValidationException.class, () -> profile.setPassword(null));
    }

    @Test
    void checkPassword_ShouldReturnFalseForNullPassword() {
        assertFalse(profile.checkPassword("anyPassword"));
    }

    @Test
    void checkPassword_ShouldReturnTrueForMatchingPassword() {
        String password = "ValidPass123";
        profile.setPassword(password);
        assertTrue(profile.checkPassword(password));
    }
} 