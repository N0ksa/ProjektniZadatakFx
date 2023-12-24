package hr.java.project.projectfxapp.filter;

import java.time.LocalDate;

public class CompetitionFilter {
    private String name;
    private LocalDate dateOfCompetition;

    public CompetitionFilter(String name, LocalDate dateOfCompetition) {
        this.name = name;
        this.dateOfCompetition = dateOfCompetition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateOfCompetition() {
        return dateOfCompetition;
    }

    public void setDateOfCompetition(LocalDate dateOfCompetition) {
        this.dateOfCompetition = dateOfCompetition;
    }
}
