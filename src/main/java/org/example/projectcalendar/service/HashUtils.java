
package org.example.projectcalendar.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;



/*one way SHA-256 hashing https://security.stackexchange.com/questions/17421/how-to-store-salt
* https://stackoverflow.com/questions/1617045/where-to-store-the-key-for-encrypting-and-decrypting-password
*
*
*
*
*
* */
public class HashUtils {

    private static final String ALGORITHM = "SHA-256";

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String password, String salt) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance(ALGORITHM);
        md.update(Base64.getDecoder().decode(salt));
        byte[] hashedPassword = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashedPassword);
    }
}