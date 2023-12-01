package hr.java.project.projectfxapp.utility;



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
    private static final String COMPETITION_RESULTS_SERIALIZATION_FILE_NAME = "dat/serialized-competitionResults.txt";




    public static void serializeCompetitionResults(List<Competition> competitionResults){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(COMPETITION_RESULTS_SERIALIZATION_FILE_NAME))){
            oos.writeObject(competitionResults);
        }
        catch (IOException ex){
            String message = "Greška prilikom serijalizacije objekta!";
            logger.error(message, ex);
        }
    }


    public static List<Competition> deserializeCompetitionResults(){
        List<Competition> competitionResults = new ArrayList<>();
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(COMPETITION_RESULTS_SERIALIZATION_FILE_NAME))){
            Object object = ois.readObject();
            if (object instanceof List<?>){
                competitionResults = (List<Competition>) object;
            }
            else {
                logger.error("Nije moguće deserijalizirati listu natjecanja. Neočekivani format objekta.");
            }


        }
        catch(IOException | ClassNotFoundException ex){
            String message = "Greška prilikom deserijalizacije!";
            logger.error(message, ex);
        }

        return competitionResults;

    }
}








