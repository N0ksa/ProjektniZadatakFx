package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import hr.java.project.projectfxapp.utility.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public final class User implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private String username;
    private String password;
    private UserRole role;


    public User(String username, String password, UserRole role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }


}
