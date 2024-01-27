package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.constants.Constants;
import hr.java.project.projectfxapp.utility.manager.SessionManager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public final class Student extends NamedEntity implements Gradable, Serializable, Recordable<Student> {
    private String surname;
    private String gender;
    private String email;
    private Integer yearOfStudy;
    private Map<String, Integer> grades = new LinkedHashMap<>();
    private ClubMembership clubMembership;
    private Picture picture;


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


    public Student(Student studentToCopy) {
        super(studentToCopy.getId(), studentToCopy.getName());
        this.surname = studentToCopy.surname;
        this.gender = studentToCopy.gender;
        this.email = studentToCopy.email;
        this.yearOfStudy = studentToCopy.yearOfStudy;
        this.grades = new HashMap<>(studentToCopy.grades);
        this.clubMembership = studentToCopy.clubMembership;
        this.picture = new Picture(studentToCopy.picture.getPicturePath());
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
        return Objects.equals(surname, student.surname) && Objects.equals(gender, student.gender)
                && Objects.equals(email, student.email) && Objects.equals(yearOfStudy, student.yearOfStudy)
                && Objects.equals(grades, student.grades) && Objects.equals(clubMembership, student.clubMembership)
                && Objects.equals(picture, student.picture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), surname, gender, email, yearOfStudy, grades, clubMembership, picture);
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
            if (picturePath.isEmpty()) {
                picturePath = Constants.DEFAULT_PICTURE_MEMBER_ICON;
            }

            return new Student(studentId, name, surname, gender, email, yearOfStudy, grades, clubMembership, picturePath);

        }
    }

    @Override
    public Optional<Change> getChange(Student newValueToCompare) {
        if (this.equals(newValueToCompare)){
            return Optional.empty();
        }
        else{
            StringBuilder oldValueBuilder = new StringBuilder();
            StringBuilder newValueBuilder = new StringBuilder();

            compareAndAppend("Ime", this.getName(), newValueToCompare.getName(), oldValueBuilder,
                    newValueBuilder);
            compareAndAppend("Prezime", this.getSurname(), newValueToCompare.getSurname(), oldValueBuilder,
                    newValueBuilder);
            compareAndAppend("Spol", this.getGender(), newValueToCompare.getGender(), oldValueBuilder,
                    newValueBuilder);
            compareAndAppend("Email", this.getEmail(), newValueToCompare.getEmail(), oldValueBuilder,
                    newValueBuilder);
            compareAndAppend("Godina studija", this.getYearOfStudy(), newValueToCompare.getYearOfStudy(),
                    oldValueBuilder, newValueBuilder);
            compareAndAppend("Ocjene", this.getGrades(), newValueToCompare.getGrades(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Članstvo", this.getClubMembership(), newValueToCompare.getClubMembership(),
                    oldValueBuilder, newValueBuilder);
            compareAndAppend("Slika", this.getPicture().getPicturePath(),
                    newValueToCompare.getPicture().getPicturePath(), oldValueBuilder, newValueBuilder);

            return Optional.of(Change.create(
                    SessionManager.getInstance().getCurrentUser(),
                    oldValueBuilder.toString(),
                    newValueBuilder.toString(),
                    "Student/id:" + this.getId()
            ));
        }
    }


    private <T> void compareAndAppend(String fieldName, T oldValue, T newValue, StringBuilder oldValueBuilder, StringBuilder newValueBuilder) {
        if (!Objects.equals(oldValue, newValue)) {
            oldValueBuilder.append(fieldName + ": " + oldValue + ";");
            newValueBuilder.append(fieldName + ": " + newValue + ";");
        }
    }
}
