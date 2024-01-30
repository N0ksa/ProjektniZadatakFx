package hr.java.project.projectfxapp.entities;

import java.io.Serializable;

public record Auditorium (String building, String hall){
    @Override
    public String toString() {
        return "Auditorium{"
                + "zgrada ='" + building + '\''
                + ", dvorana='" + hall + '\''
                + '}';
    }

    public Auditorium(Auditorium original) {
        this(original.building(), original.hall());
    }

}
