package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Predstavlja matematički klub.
 * Implementira sučelje {@link Gradable}, što znači da se može ocjenjivati.
 */
public class MathClub extends NamedEntity implements Gradable, Serializable {
    private Address address;
    private Set<Student> students;

    /**
     * Konstruktor za stvaranje objekta razreda "MathClub".
     * @param name Naziv matematičkog kluba.
     * @param address Adresa kluba.
     * @param students Lista studenata koji su članovi kluba.
     */
    public MathClub(Long clubId, String name, Address address, Set<Student> students) {
        super(clubId, name);
        this.address = address;
        this.students = students;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }


    @Override
    public BigDecimal calculateScore(List<CompetitionResult> competitionsResults, Integer numberOfCollaborations){
        BigDecimal overallClubScore = BigDecimal.ZERO;

        BigDecimal numberOfStudentsWeight = new BigDecimal(0.2);
        BigDecimal numberOfCollaborationsWeight = new BigDecimal(0.5);
        BigDecimal competitionResultsWeight = new BigDecimal(0.3);

       Integer numberOfStudents = getNumberOfMembers();

        return overallClubScore.add(numberOfStudentsWeight.multiply(BigDecimal.valueOf(numberOfStudents)))
                .add(numberOfCollaborationsWeight.multiply(BigDecimal.valueOf(numberOfCollaborations)))
                .add(competitionResultsWeight.multiply(collectAllScoresFromCompetitions(competitionsResults)));


    }


    /**
     * Služi za zbrajanje svih bodova koje su članovi matematičkog kluba ostvarili na svim natjecanjima.
     * @param studentsCompetitions Lista rezultata natjecanja u kojima su članovi kluba sudjelovali.
     * @return BigDecimal - ukupan broj bodova matematičkog kluba ostvarenih na natjecanjima.
     */
    private BigDecimal collectAllScoresFromCompetitions(List <CompetitionResult> studentsCompetitions){
        BigDecimal sumOfAllScores = BigDecimal.ZERO;
        for (CompetitionResult competition: studentsCompetitions){
            sumOfAllScores = sumOfAllScores.add(competition.score());
        }

        return sumOfAllScores;
    }

    /**
     * Služi da dobivanje broja članova.
     * @return Integer - broj članova matematičkog kluba.
     */
    public Integer getNumberOfMembers (){
        return students.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MathClub mathClub = (MathClub) o;
        return Objects.equals(address, mathClub.address) && Objects.equals(students, mathClub.students);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address, students);
    }
}
