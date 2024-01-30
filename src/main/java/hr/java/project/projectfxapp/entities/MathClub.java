package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public final class MathClub extends NamedEntity implements Gradable, Serializable {
    private Address address;
    private Set<Student> students;


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

        BigDecimal numberOfStudentsWeight = new BigDecimal("0.2");
        BigDecimal numberOfCollaborationsWeight = new BigDecimal("0.5");
        BigDecimal competitionResultsWeight = new BigDecimal("0.3");

       Integer numberOfStudents = getNumberOfMembers();

        return overallClubScore.add(numberOfStudentsWeight.multiply(BigDecimal.valueOf(numberOfStudents)))
                .add(numberOfCollaborationsWeight.multiply(BigDecimal.valueOf(numberOfCollaborations)))
                .add(competitionResultsWeight.multiply(collectAllScoresFromCompetitions(competitionsResults)));


    }


    private BigDecimal collectAllScoresFromCompetitions(List <CompetitionResult> studentsCompetitions){
        BigDecimal sumOfAllScores = BigDecimal.ZERO;
        for (CompetitionResult competition: studentsCompetitions){
            sumOfAllScores = sumOfAllScores.add(competition.score());
        }

        return sumOfAllScores;
    }


    public Integer getNumberOfMembers (){
        return students.size();
    }

    public Boolean hasMember(Student member){
        return getStudents().stream().anyMatch( student -> student.getId().equals(member.getId()));
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

    @Override
    public String toString() {
        return getName();
    }
}
