package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.utility.SessionManager;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * Predstavlja matematički projekt.
 */

public final class MathProject extends NamedEntity implements Serializable, Recordable<MathProject> {

    private MathClub organizer;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private Address address;
    private Map<MathClub, List<Student>> collaborators;

    /**
     * Konstruktor za stvaranje nove instance matematičkog projekta.
     * @param name Naziv matematičkog projekta.
     * @param description Opis projekta.
     * @param collaborators Mapa koja povezuje matematičke klubove s njihovim članovima koji sudjeluju u projektu.
     */
    public MathProject(Long projectId, MathClub organizer, LocalDate startDate, Address address, String name, String description,
                       Map<MathClub, List<Student>> collaborators) {
        super(projectId, name);
        this.address = address;
        this.description = description;
        this.collaborators = collaborators;
        this.organizer = organizer;
        this.startDate = startDate;
    }


    public MathProject(MathProject mathProjectToCopy) {
        super(mathProjectToCopy.getId(), mathProjectToCopy.getName());
        this.address = new Address(mathProjectToCopy.address);
        this.description = mathProjectToCopy.description;
        this.collaborators = new HashMap<>(mathProjectToCopy.collaborators);
        this.organizer = mathProjectToCopy.organizer;
        this.startDate = mathProjectToCopy.startDate;
        this.endDate = mathProjectToCopy.endDate;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public MathClub getOrganizer() {
        return organizer;
    }

    public void setOrganizer(MathClub organizer) {
        this.organizer = organizer;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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

    public Integer getTotalNumberOfCollaborators(){
        return collaborators.values().stream().mapToInt(List::size).sum();
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


    @Override
    public Optional<Change> getChange(MathProject newValueToCompare) {
        if (this.equals(newValueToCompare)){
            return Optional.empty();
        }
        else{
            StringBuilder oldValueBuilder = new StringBuilder();
            StringBuilder newValueBuilder = new StringBuilder();
            compareAndAppend("Naziv", this.getName(), newValueToCompare.getName(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Opis", this.getDescription(), newValueToCompare.getDescription(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Organizator", this.getOrganizer(), newValueToCompare.getOrganizer(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Početak", this.getStartDate(), newValueToCompare.getStartDate(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Kraj", this.getEndDate(), newValueToCompare.getEndDate(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Adresa", this.getAddress(), newValueToCompare.getAddress(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Sudionici", this.getCollaborators(), newValueToCompare.getCollaborators(), oldValueBuilder, newValueBuilder);

            return Optional.of(Change.create(
                    SessionManager.getInstance().getCurrentUser(),
                    oldValueBuilder.toString(),
                    newValueBuilder.toString(),
                    "Projekt/id:" + this.getId()
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
