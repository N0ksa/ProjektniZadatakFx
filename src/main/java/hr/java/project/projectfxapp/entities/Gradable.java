package hr.java.project.projectfxapp.entities;

import java.math.BigDecimal;
import java.util.List;

public interface Gradable {

    public BigDecimal calculateScore(List<CompetitionResult> competitionsResults, Integer numberOfCollaborations);
}
