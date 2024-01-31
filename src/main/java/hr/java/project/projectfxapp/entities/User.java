package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.enums.UserRole;
import hr.java.project.projectfxapp.utility.manager.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public final class User {
    private String username;
    private String hashedPassword;
    private UserRole role;
    private Long mathClubId;
    private Picture picture;


    public User(String username, String hashedPassword, UserRole role, Long mathClubId, Picture picture) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
        this.mathClubId = mathClubId;
        this.picture = picture;
    }

    public User(String username, String hashedPassword, UserRole role) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.role = role;
    }


    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
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
