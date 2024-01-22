package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;


public record CompetitionResult(Student participant, BigDecimal score) implements Serializable {

    public CompetitionResult withScore(BigDecimal newScore) {
        return new CompetitionResult(this.participant, newScore);
    }


}

