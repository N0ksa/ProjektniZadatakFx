package hr.java.project.projectfxapp.entities;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sučelje koje označava da je objekt moguće ocjeniti.
 */
public interface Gradable {

    /**
     * Izračunava sveukupne bodove.
     * @param competitionsResults Lista rezultata natjecanja koja služi za kalkulaciju bodova.
     * @param numberOfCollaborations Broj sudjelovanja na projektima koji služi za kalkulaciju bodova.
     * @return BigDecimal - ukupni bodovi
     */
    public BigDecimal calculateScore(List<CompetitionResult> competitionsResults, Integer numberOfCollaborations);
}
