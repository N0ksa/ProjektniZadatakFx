package hr.java.project.projectfxapp.utility.files;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

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
        }
    }


}
