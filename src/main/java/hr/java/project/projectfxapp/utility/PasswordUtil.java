package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.entities.User;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PasswordUtil {

    private static final Logger logger = LoggerFactory.getLogger(PasswordUtil.class);

    public static String hashPassword(String password) throws UnsupportedAlgorithmException{
        try {
            var digest = MessageDigest.getInstance("SHA-256")
                    .digest(password.getBytes());

            return new String(Base64.getEncoder()
                    .encode(digest));
        } catch (NoSuchAlgorithmException e) {
            logger.error("Sustav ne podržava SHA-256");
            throw new UnsupportedAlgorithmException("Sustav ne podržava SHA-256");

        }
    }


    public static boolean isPasswordCorrect(String enteredPassword, String hashedPassword){
        try {
            return hashPassword(enteredPassword).equals(hashedPassword);
        } catch (UnsupportedAlgorithmException e) {
            logger.error("Sustav ne podržava SHA-256");
            return false;
        }
    }



}

