package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.enums.Status;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Predstavlja matematičko natjecanje.
 */
public class Competition extends NamedEntity implements Serializable {

    private MathClub organizer;
    private String description;
    private Address address;
    private Auditorium auditorium;
    private LocalDateTime timeOfCompetition;
    private Set <CompetitionResult> competitionResults;

    private Status status;

    /**
     * Konstruktor za stvaranje nove instance matematičkog natjecanja.
     * @param name Naziv natjecanja.
     * @param description Opis natjecanja.
     * @param address Adresa održavanja natjecanja.
     * @param timeOfCompetition Vrijeme održavanja natjecanja.
     * @param competitionResults Rezultati natjecatelja.
     */
    public Competition(Long competitionId, MathClub organizer, String name, String description, Address address,
                       Auditorium auditorium, LocalDateTime timeOfCompetition, Status status,
                       Set<CompetitionResult> competitionResults) {
        super(competitionId, name);
        this.description = description;
        this.address = address;
        this.auditorium = auditorium;
        this.timeOfCompetition = timeOfCompetition;
        this.competitionResults = competitionResults;
        this.status = status;
        this.organizer = organizer;
    }


    public MathClub getOrganizer() {
        return organizer;
    }

    public void setOrganizer(MathClub organizer) {
        this.organizer = organizer;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Auditorium getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(Auditorium auditorium) {
        this.auditorium = auditorium;
    }

    public LocalDateTime getTimeOfCompetition() {
        return timeOfCompetition;
    }

    public void setTimeOfCompetition(LocalDateTime timeOfCompetition) {
        this.timeOfCompetition = timeOfCompetition;
    }

    public Set<CompetitionResult> getCompetitionResults() {
        return competitionResults;
    }



    public void setCompetitionResults(Set<CompetitionResult> competitionResults) {
        this.competitionResults = competitionResults;
    }


    /**
     * Dohvaća rezultat natjecanja za određenog sudionika.
     * @param participant Student za kojeg se dohvaća rezultat natjecanja.
     * @return {@code Optional} koji sadrži rezultat natjecanja za sudionika ili prazan optional ako sudionik nije bio na natjecanju.
     */
    public Optional <CompetitionResult> getCompetitionResultForParticipant(Student participant){

        return competitionResults.stream()
                .filter(result -> result.participant().equals(participant))
                .findFirst();
    }

    /**
     * Provjerava je li određeni student sudionik ovog natjecanja.
     * @param participantToCheck Student za provjeru sudjelovanja.
     * @return <code>true</code> ako je student sudionik ovog natjecanja, inače <code>false</code>.
     */
    public boolean hasParticipant(Student participantToCheck){
        for (CompetitionResult competition : competitionResults){
            if (competition.participant().equals(participantToCheck)){
                return true;
            }
        }
        return false;
    }

    /**
     * Vraća pobjednika natjecanja.
     * @return Student - pobjednik natjecanja.
     */
    public Optional <Student> findWinner(){

       return competitionResults.stream()
               .max(Comparator.comparing(CompetitionResult::score))
               .map(CompetitionResult::participant);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Competition that = (Competition) o;
        return Objects.equals(description, that.description)
                && Objects.equals(address, that.address)
                && Objects.equals(auditorium, that.auditorium)
                && Objects.equals(timeOfCompetition, that.timeOfCompetition)
                && Objects.equals(competitionResults, that.competitionResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), description, address, auditorium, timeOfCompetition, competitionResults);
    }
}
