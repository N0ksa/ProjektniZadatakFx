package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Predstavlja rezultat sudionika na natjecanju.
 */
public record CompetitionResult(Student participant, BigDecimal score) implements Serializable {
}
