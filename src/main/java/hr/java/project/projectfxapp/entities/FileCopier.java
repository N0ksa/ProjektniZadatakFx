package hr.java.project.projectfxapp.entities;

import java.io.File;
import java.io.IOException;

public interface FileCopier<T extends File>  {
    void copyToDirectory(T sourceFile, String destinationDirectory) throws IOException;
}
