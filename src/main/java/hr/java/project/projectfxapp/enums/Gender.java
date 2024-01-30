package hr.java.project.projectfxapp.enums;

import hr.java.project.projectfxapp.exception.NoSuchGenderException;

public enum Gender {
    Male("Male"),
    Female("Female");
    private final String gender;
    Gender(String gender){
        this.gender = gender;
    }

    public String getGender() {
        return gender;
    }

    public static Gender getGenderFromString(String gender){
        for (Gender g: Gender.values()){
            if (g.getGender().equalsIgnoreCase(gender)){
                return g;
            }
        }
        throw new NoSuchGenderException("Ne postoji takav spol");

    }
}
