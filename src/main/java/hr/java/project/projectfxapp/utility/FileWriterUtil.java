package hr.java.project.projectfxapp.utility;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import hr.java.project.projectfxapp.enums.ValidationRegex;
import hr.java.project.projectfxapp.exception.UnsupportedAlgorithmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class FileWriterUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileWriterUtil.class);


    public static void saveUsers(List<User> users) {
        File usersFile = new File(Constants.USER_FILE_NAME);

        try (PrintWriter pw = new PrintWriter(usersFile)) {
            for (User user : users) {
                pw.println(user.getUsername() + ":" + user.getHashedPassword() + ":" + user.getRole().getName());

            }
        } catch (IOException ex) {
            String message ="Dogodila se pogreška kod pisanja datoteke - + " + Constants.USER_FILE_NAME;
            logger.error(message, ex);
            System.out.println(message);
        }
    }


}
