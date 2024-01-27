package hr.java.project.projectfxapp.filter;

import hr.java.project.projectfxapp.entities.MathClub;

public class StudentFilter {
    private String name;
    private String surname;


    public StudentFilter(String name, String surname) {
        this.name = name;
        this.surname = surname;
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


}
