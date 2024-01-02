package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.entities.ClubMembership;
import hr.java.project.projectfxapp.entities.Gradable;
import hr.java.project.projectfxapp.entities.NamedEntity;
import hr.java.project.projectfxapp.utility.Picture;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Student extends NamedEntity implements Gradable, Serializable {
    private String surname;
    private String gender;
    private String email;
    private Integer yearOfStudy;
    private Map<String, Integer> grades = new LinkedHashMap<>();
    private ClubMembership clubMembership;
    private Picture picture;


    /**
     * Constructor for creating a "Student" object.
     *
     * @param studentId   Identifikacijski broj studenta.
     * @param name        Ime studenta.
     * @param surname     Prezime studenta.
     * @param gender      Spol studenta.
     * @param email       Adresa elektroničke pošte studenta.
     * @param yearOfStudy Trenutna godina studija studenta.
     * @param grades      Mapa koja sadrži ocjene studenta.
     * @param membership  Članstvo studenta u klubu.
     * @param picturePath Putanja do slike studenta.
     */
    private Student(Long studentId, String name, String surname, String gender, String email,
                    Integer yearOfStudy, Map<String, Integer> grades, ClubMembership membership,
                    String picturePath) {
        super(studentId, name);
        this.gender = gender;
        this.surname = surname;
        this.email = email;
        this.yearOfStudy = yearOfStudy;
        this.grades.putAll(grades);
        this.clubMembership = membership;
        this.picture = new Picture(picturePath);

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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
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
     *
     * @return BigDecimal - Prosječna ocjena studenta, ako student nema ocjena vraća se nula.
     */
    public BigDecimal calculateAverageGrade() {
        int counter = 0;
        BigDecimal sumOfGrades = new BigDecimal(0);

        for (Integer grade : grades.values()) {
            if (grade == 1) {
                return BigDecimal.ONE;
            }
            sumOfGrades = sumOfGrades.add(BigDecimal.valueOf(grade));
            counter++;
        }

        if (counter == 0) {
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
     *
     * @param studentCompetitions Lista rezultata svih natjecanja u kojima je student sudjelovao.
     * @return BigDecimal - ukupan broj bodova osvojenih na natjecanjima.
     */
    private BigDecimal calculateTotalCompetitionScores(List<CompetitionResult> studentCompetitions) {
        BigDecimal sumOfAllScores = BigDecimal.ZERO;
        for (CompetitionResult competition : studentCompetitions) {
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

    public static class StudentBuilder {
        private Long studentId;
        private String name;
        private String surname;
        private String gender;
        private String email;
        private Integer yearOfStudy;
        private Map<String, Integer> grades = new LinkedHashMap<>();
        private ClubMembership clubMembership;
        private String picturePath;

        public StudentBuilder(Long studentId, String name, String surname) {
            this.studentId = studentId;
            this.name = name;
            this.surname = surname;
        }

        public StudentBuilder gender(String gender) {
            this.gender = gender;
            return this;
        }

        public StudentBuilder email(String email) {
            this.email = email;
            return this;
        }

        public StudentBuilder yearOfStudy(Integer yearOfStudy) {
            this.yearOfStudy = yearOfStudy;
            return this;
        }

        public StudentBuilder grades(Map<String, Integer> grades) {
            this.grades.putAll(grades);
            return this;
        }

        public StudentBuilder clubMembership(ClubMembership membership) {
            this.clubMembership = membership;
            return this;
        }

        public StudentBuilder picturePath(String picturePath) {
            this.picturePath = picturePath;
            return this;
        }

        public Student build() {
            if (picturePath == null) {
                picturePath = Constants.DEFAULT_PICTURE_PATH;
            }

            return new Student(studentId, name, surname, gender, email, yearOfStudy, grades, clubMembership, picturePath);

        }
    }

}
