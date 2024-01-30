package hr.java.project.projectfxapp.threads;

import hr.java.project.projectfxapp.entities.Change;
import hr.java.project.projectfxapp.utility.files.SerializationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class ChangesManagerThread {

    private static final Logger logger = LoggerFactory.getLogger(ChangesManagerThread.class);

    private static boolean changesDirectoryInUse = false;

    public synchronized void writeChangesToFile(List<Change> changes){

            while(changesDirectoryInUse){
                try{
                    wait();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }

            }

            changesDirectoryInUse = true;

            SerializationUtil.serializeChanges(changes);

            changesDirectoryInUse =  false;

            notifyAll();

    }


    public synchronized List<Change> readChangesFromFile(){

        while(changesDirectoryInUse){
            try{
                wait();
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

        }

        changesDirectoryInUse = true;

        List<Change> changes = SerializationUtil.deserializeChanges();

        changesDirectoryInUse =  false;

        notifyAll();

        return changes;

    }

}
