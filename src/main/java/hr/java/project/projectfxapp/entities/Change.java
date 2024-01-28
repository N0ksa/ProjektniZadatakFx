package hr.java.project.projectfxapp.entities;


import hr.java.project.projectfxapp.enums.ValidationRegex;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public record Change(String dataType, String userRole, String timestamp, String oldValue, String newValue) implements Serializable {
    public static Change create(User user, String oldValue, String newValue, String dataType) {
        return new Change(dataType,
                user.getRole().toString() + "/username:" + user.getUsername(),
                LocalDateTime.now().
                        format(DateTimeFormatter.ofPattern(ValidationRegex.VALID_LOCAL_DATE_TIME_REGEX.getRegex())),
                oldValue,
                newValue);
    }
}