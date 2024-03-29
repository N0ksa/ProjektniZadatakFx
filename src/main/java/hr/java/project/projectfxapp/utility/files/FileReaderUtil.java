package hr.java.project.projectfxapp.utility.files;


import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Pomoćna klasa koja služi za učitavanje podataka iz datoteka.
 */
public class FileReaderUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileReaderUtil.class);

    public static List<User> getUsers() {
        List<User> userList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(Constants.USER_FILE_NAME))) {
            String line;
            while ((Optional.ofNullable(line = reader.readLine()).isPresent())) {

                String[] parts = line.split(":");

                if (parts.length == 3) {
                    String username = parts[0];
                    String hashedPassword = parts[1];
                    String roleName = parts[2];

                    UserRole role = UserRole.getRoleByName(roleName);
                    userList.add(new User(username , hashedPassword, role));

                } else {
                    logger.warn("Pogrešan broj argumenata");
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            String message = "Dogodila se pogreška kod čitanja datoteke: " + Constants.USER_FILE_NAME;
            logger.error(message, e);
            System.out.println(message);
        }

        return userList;
    }


}