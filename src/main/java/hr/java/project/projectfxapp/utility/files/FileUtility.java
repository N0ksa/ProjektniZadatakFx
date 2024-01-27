package hr.java.project.projectfxapp.utility.files;

import hr.java.project.projectfxapp.entities.FileCopier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtility implements FileCopier<File> {

    private static final Logger logger = LoggerFactory.getLogger(FileUtility.class);
    @Override
    public void copyToDirectory(File sourceFile, String destinationDirectory) throws IOException {

        String imageName = sourceFile.getName();
        String destinationPath = destinationDirectory + imageName;

        Path sourcePath = sourceFile.toPath();
        Path destinationPathObject = Path.of(destinationPath);


        Files.copy(sourcePath, destinationPathObject, StandardCopyOption.REPLACE_EXISTING);

    }

}
