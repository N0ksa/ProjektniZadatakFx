package hr.java.project.projectfxapp.utility;



import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.Competition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Pomoćna klasa koja služi za serijalizaciju i deserijalizaciju objekata.
 */
public class SerializationUtil {

    private static final Logger logger = LoggerFactory.getLogger(SerializationUtil.class);


    public static void serializeChanges(List<Change> changes){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(Constants.CHANGES_FILE_NAME))){
            oos.writeObject(changes);
        }
        catch (IOException ex){
            String message = "Greška prilikom serijalizacije objekta!";
            logger.error(message, ex);
        }

    }


    public static List<Change> deserializeChanges() {
        List<Change> changes = new ArrayList<>();

        File file = new File(Constants.CHANGES_FILE_NAME);

        if (file.length() == 0) {
            return changes;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(Constants.CHANGES_FILE_NAME))) {
            Object object = ois.readObject();
            if (object instanceof List<?>) {
                changes = (List<Change>) object;
            } else {
                logger.error("Nije moguće deserijalizirati listu promjena. Neočekivani format objekta.");
            }

        } catch (IOException | ClassNotFoundException ex) {
            String message = "Greška prilikom deserijalizacije!";
            logger.error(message, ex);
        }

        return changes;
    }



}








