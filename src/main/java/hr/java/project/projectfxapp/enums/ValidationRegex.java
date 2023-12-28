package hr.java.project.projectfxapp.enums;

import java.util.regex.Pattern;

/**
 * Predstavlja regularne izraze za provjeru valjanosti.
 */
public enum ValidationRegex {
    VALID_WEB_ADDRESS("www\\.[A-Za-z0-9]+\\.[A-Za-z]+"),
    VALID_POSTAL_CODE("[0-9]+"),

    VALID_LOCAL_DATE_REGEX ("dd.MM.yyyy."),
    VALID_LOCAL_DATE_TIME_REGEX ("dd.MM.yyyy HH:mm:ss"),
    VALID_LOCAL_TIME_REGEX("HH:mm"),

    VALID_EMAIL_ADDRESS("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"),

    VALID_HOUSE_NUMBER ("^\\d{1,4}$"),

    VALID_USERNAME_PATTERN ("^[A-Za-z0-9\\-_]{4,32}$");

    private final String regex;

    ValidationRegex(String regex) {
        this.regex = regex;
    }

    public String getRegex() {
        return regex;
    }
}