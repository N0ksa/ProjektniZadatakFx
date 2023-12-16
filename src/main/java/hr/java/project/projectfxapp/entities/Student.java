package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Predstavlja studenta.
 * Implementira sučelje {@link Gradable}, što znači da se može ocjenjivati.
 */
public class Student extends NamedEntity implements Gradable, Serializable {
    private String surname;
    private String email;
    private Integer yearOfStudy;
    private Map<String, Integer> grades = new LinkedHashMap<>();
    private ClubMembership clubMembership;


    /**
     * Konstruktor za stvaranje objekta razreda "Student".
     * @param name Ime studenta.
     * @param surname Prezime studenta.
     * @param studentId Identifikacijski broj studenta.
     * @param email Adresa elektroničke pošte studenta.
     * @param yearOfStudy Trenutna godina studija studenta.
     * @param grades Mapa koja sadrži ocjene studenta.
     */
    public Student(Long studentId, String name, String surname, String email, Integer yearOfStudy, Map<String,
            Integer> grades, ClubMembership membership) {

        super(studentId, name);
        this.surname = surname;
        this.email = email;
        this.yearOfStudy = yearOfStudy;
        this.grades.putAll(grades);
        this.clubMembership = membership;
    }


    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getYearOfStudy() {
        return yearOfStudy;
    }

    public void setYearOfStudy(Integer yearOfStudy) {
        this.yearOfStudy = yearOfStudy;
    }

    public Map<String, Integer> getGrades() {
        return grades;
    }

    public void setGrades(Map<String, Integer> grades) {
        this.grades = grades;
    }

    public ClubMembership getClubMembership() {
        return clubMembership;
    }

    public void setClubMembership(ClubMembership clubMembership) {
        this.clubMembership = clubMembership;
    }


    /**
     * Izračunava prosječnu ocjenu studenta.
     * @return BigDecimal - Prosječna ocjena studenta, ako student nema ocjena vraća se nula.
     */
    public BigDecimal calculateAverageGrade(){
        int counter = 0;
        BigDecimal sumOfGrades = new BigDecimal(0);

        for (Integer grade : grades.values()){
            if (grade == 1){
                return BigDecimal.ONE;
            }
            sumOfGrades = sumOfGrades.add(BigDecimal.valueOf(grade));
            counter++;
        }

        if (counter == 0){
            return BigDecimal.ZERO;
        }

        return sumOfGrades.divide(BigDecimal.valueOf(counter), 2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculateScore(List<CompetitionResult> competitionsResults, Integer numberOfCollaborations) {

        BigDecimal averageGradeWeight = new BigDecimal(0.2);
        BigDecimal numberOfCollaborationsWeight = new BigDecimal(0.3);
        BigDecimal competitionResultsWeight = new BigDecimal(0.5);

        BigDecimal normalizedAverageGrade = calculateAverageGrade().multiply(BigDecimal.TWO);
        BigDecimal scoreFromAllCompetitions = calculateTotalCompetitionScores(competitionsResults);

        return scoreFromAllCompetitions.multiply(competitionResultsWeight)
                .add(normalizedAverageGrade.multiply(averageGradeWeight))
                .add(numberOfCollaborationsWeight.multiply(new BigDecimal(numberOfCollaborations)));


    }

    /**
     * Zbraja sve bodove koje je student osvojio na svim natjecanjima.
     * @param studentCompetitions Lista rezultata svih natjecanja u kojima je student sudjelovao.
     * @return BigDecimal - ukupan broj bodova osvojenih na natjecanjima.
     */
    private BigDecimal calculateTotalCompetitionScores(List <CompetitionResult> studentCompetitions){
        BigDecimal sumOfAllScores = BigDecimal.ZERO;
        for (CompetitionResult competition: studentCompetitions){
            sumOfAllScores = sumOfAllScores.add(competition.score());
        }

        return sumOfAllScores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Student student = (Student) o;
        return Objects.equals(surname, student.surname)
                && Objects.equals(email, student.email)
                && Objects.equals(yearOfStudy, student.yearOfStudy)
                && Objects.equals(grades, student.grades)
                && Objects.equals(clubMembership, student.clubMembership);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), surname, email, yearOfStudy, grades, clubMembership);
    }

    @Override
    public String toString() {
        return getName() + " " + getSurname();
    }
}
