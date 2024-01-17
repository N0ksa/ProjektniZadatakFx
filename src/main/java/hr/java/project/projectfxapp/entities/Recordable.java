package hr.java.project.projectfxapp.entities;

import java.util.Optional;

public sealed interface Recordable <T> permits Competition, MathProject, Student {
    Optional<Change> getChange(T newValueToCompare);

}
