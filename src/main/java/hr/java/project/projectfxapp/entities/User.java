package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.utility.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public final class User implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    private String username;
    private String hashedPassword;
    private UserRole role;
    private Long mathClubId;


    public User(String username, String hashedPassword, UserRole role, Long mathClubId) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.mathClubId = mathClubId;
    }

    public User(String username, String hashedPassword, UserRole role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }

    public Long getMathClubId() {
        return mathClubId;
    }

    public void setMathClubId(Long mathClubId) {
        this.mathClubId = mathClubId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }


}
