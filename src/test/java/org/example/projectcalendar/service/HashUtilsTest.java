package org.example.projectcalendar.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HashUtilsTest {

    @Test
    void generateSalt_ShouldReturnNonNullString() {
        String salt = HashUtils.generateSalt();
        assertNotNull(salt);
        assertFalse(salt.isEmpty());
    }

    @Test
    void generateSalt_ShouldReturnDifferentValuesOnMultipleCalls() {
        String salt1 = HashUtils.generateSalt();
        String salt2 = HashUtils.generateSalt();
        assertNotEquals(salt1, salt2);
    }

    @Test
    void hashPassword_ShouldReturnConsistentHashForSameSaltAndPassword() throws Exception {
        String password = "testPassword123";
        String salt = HashUtils.generateSalt();
        
        String hash1 = HashUtils.hashPassword(password, salt);
        String hash2 = HashUtils.hashPassword(password, salt);
        
        assertEquals(hash1, hash2);
    }

    @Test
    void hashPassword_ShouldReturnDifferentHashesForDifferentPasswords() throws Exception {
        String salt = HashUtils.generateSalt();
        String hash1 = HashUtils.hashPassword("password1", salt);
        String hash2 = HashUtils.hashPassword("password2", salt);
        
        assertNotEquals(hash1, hash2);
    }

    @Test
    void hashPassword_ShouldReturnDifferentHashesForSamePasswordDifferentSalts() throws Exception {
        String password = "testPassword123";
        String salt1 = HashUtils.generateSalt();
        String salt2 = HashUtils.generateSalt();
        
        String hash1 = HashUtils.hashPassword(password, salt1);
        String hash2 = HashUtils.hashPassword(password, salt2);
        
        assertNotEquals(hash1, hash2);
    }
} 