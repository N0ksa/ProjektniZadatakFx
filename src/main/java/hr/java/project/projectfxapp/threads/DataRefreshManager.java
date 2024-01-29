package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.entities.Competition;
import hr.java.project.projectfxapp.entities.MathClub;
import hr.java.project.projectfxapp.entities.MathProject;
import hr.java.project.projectfxapp.utility.database.DatabaseUtil;
import hr.java.project.projectfxapp.utility.files.SerializationUtil;
import javafx.scene.chart.PieChart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DataRefreshManager {

    private static final Logger logger = LoggerFactory.getLogger(DataRefreshManager.class);

    private static boolean databaseInUse = false;

    public synchronized List<Competition> getCompetitions(){

        while(databaseInUse){
            try{
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

        }

        databaseInUse = true;

        List<Competition> competitions = DatabaseUtil.getCompetitions();

        databaseInUse =  false;

        notifyAll();

        return competitions;

    }

    public synchronized List<MathClub> getMathClubs(){

        while(databaseInUse){
            try{
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

        }

        databaseInUse = true;

        List<MathClub> mathClubs = DatabaseUtil.getMathClubs();

        databaseInUse =  false;

        notifyAll();

        return mathClubs;

    }

    public synchronized List<MathProject> getMathProjects(){

        while(databaseInUse){
            try{
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

        }

        databaseInUse = true;

        List<MathProject> mathProjects = DatabaseUtil.getProjects();

        databaseInUse =  false;

        notifyAll();

        return mathProjects;

    }


}
