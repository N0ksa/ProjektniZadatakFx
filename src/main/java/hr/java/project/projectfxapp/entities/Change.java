package hr.java.project.projectfxapp.entities;


import java.io.Serializable;
import java.time.LocalDateTime;

public record Change(String dataType, String userRole, LocalDateTime timestamp, String oldValue, String newValue) implements Serializable {
    public static Change create(User user, String oldValue, String newValue, String dataType) {
        return new Change(dataType,
                user.getRole().toString() + "/username:" + user.getUsername(),
                LocalDateTime.now(),
                oldValue,
                newValue);
    }
}