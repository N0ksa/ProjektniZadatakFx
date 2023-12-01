package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Predstavlja matematički projekt.
 */

public class MathProject extends NamedEntity implements Serializable {
    private String description;
    private Map<MathClub, List<Student>> collaborators;

    /**
     * Konstruktor za stvaranje nove instance matematičkog projekta.
     * @param name Naziv matematičkog projekta.
     * @param description Opis projekta.
     * @param collaborators Mapa koja povezuje matematičke klubove s njihovim članovima koji sudjeluju u projektu.
     */
    public MathProject(Long projectId, String name, String description, Map<MathClub, List<Student>> collaborators) {
        super(projectId, name);
        this.description = description;
        this.collaborators = collaborators;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<MathClub, List<Student>> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Map<MathClub, List<Student>> collaborators) {
        this.collaborators = collaborators;
    }


    /**
     * Provjerava da li određeni student sudjeluje u projektu.
     * @param studentToCheck Student koji se provjerava da li sudjeluje u projektu.
     * @return <code>true</code> ako je student sudionik projekta, inače <code>false</code>.
     */
    public boolean hasStudentCollaborator(Student studentToCheck){
        for (List <Student> student : collaborators.values()){
            if (student.contains(studentToCheck)){
                return true;
            }
        }

        return false;
    }

    /**
     * Provjerava da li određeni matematički klub sudjeluje u projektu.
     * @param mathClubToCheck Matematički klub koji se provjerava da li sudjeluje u projektu.
     * @return <code>true</code> ako je student sudionik projekta, inače <code>false</code>.
     */
    public boolean hasMathClubCollaborator(MathClub mathClubToCheck){
        return collaborators.containsKey(mathClubToCheck);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        MathProject that = (MathProject) o;
        return Objects.equals(description, that.description)
                && Objects.equals(collaborators, that.collaborators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, collaborators);
    }
}
