package hr.java.project.projectfxapp.entities;

import java.math.BigDecimal;
import java.util.List;

public sealed interface Gradable permits MathClub, Student {
    BigDecimal calculateScore(List<CompetitionResult> competitionsResults, Integer numberOfCollaborations);
}
