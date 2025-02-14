package org.example.projectcalendar.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CredentialStorageTest {

    @BeforeEach
    void setUp() {
        CredentialStorage.clearCredentials();
    }

    @Test
    void saveAndGetCredentials_ShouldWorkCorrectly() {
        String username = "testUser";
        String password = "testPass";
        
        CredentialStorage.saveCredentials(username, password);
        
        assertEquals(username, CredentialStorage.getSavedUsername());
        assertEquals(password, CredentialStorage.getSavedPassword());
    }

    @Test
    void getSavedCredentials_ShouldReturnNullWhenNoCredentialsStored() {
        assertNull(CredentialStorage.getSavedUsername());
        assertNull(CredentialStorage.getSavedPassword());
    }

    @Test
    void clearCredentials_ShouldRemoveStoredCredentials() {
        CredentialStorage.saveCredentials("testUser", "testPass");
        CredentialStorage.clearCredentials();
        
        assertNull(CredentialStorage.getSavedUsername());
        assertNull(CredentialStorage.getSavedPassword());
    }

    @Test
    void saveCredentials_ShouldOverwriteExistingCredentials() {
        CredentialStorage.saveCredentials("user1", "pass1");
        CredentialStorage.saveCredentials("user2", "pass2");
        
        assertEquals("user2", CredentialStorage.getSavedUsername());
        assertEquals("pass2", CredentialStorage.getSavedPassword());
    }
} 