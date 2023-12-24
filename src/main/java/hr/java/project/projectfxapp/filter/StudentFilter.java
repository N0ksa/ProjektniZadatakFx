package hr.java.project.projectfxapp.filter;

import hr.java.project.projectfxapp.entities.MathClub;

public class StudentFilter {
    private String name;
    private String surname;

    private MathClub studentMathClub;


    public StudentFilter(String name, String surname, MathClub studentMathClub) {
        this.name = name;
        this.surname = surname;
        this.studentMathClub = studentMathClub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public MathClub getStudentMathClub() {
        return studentMathClub;
    }

    public void setStudentMathClub(MathClub studentMathClub) {
        this.studentMathClub = studentMathClub;
    }

}
