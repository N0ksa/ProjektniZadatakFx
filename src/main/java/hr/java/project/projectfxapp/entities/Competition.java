package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.enums.Gender;
import hr.java.project.projectfxapp.enums.Status;
import hr.java.project.projectfxapp.utility.SessionManager;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Predstavlja matematičko natjecanje.
 */
public final class Competition extends NamedEntity implements Printable, Recordable<Competition> {

    private MathClub organizer;
    private String description;
    private Address address;
    private Auditorium auditorium;
    private LocalDateTime timeOfCompetition;
    private Set <CompetitionResult> competitionResults;


    /**
     * Konstruktor za stvaranje nove instance matematičkog natjecanja.
     * @param name Naziv natjecanja.
     * @param description Opis natjecanja.
     * @param address Adresa održavanja natjecanja.
     * @param timeOfCompetition Vrijeme održavanja natjecanja.
     * @param competitionResults Rezultati natjecatelja.
     */
    public Competition(Long competitionId, MathClub organizer, String name, String description, Address address,
                       Auditorium auditorium, LocalDateTime timeOfCompetition,
                       Set<CompetitionResult> competitionResults) {
        super(competitionId, name);
        this.description = description;
        this.address = address;
        this.auditorium = auditorium;
        this.timeOfCompetition = timeOfCompetition;
        this.competitionResults = competitionResults;
        this.organizer = organizer;
    }


    public Competition(Competition other) {
        super(other.getId(), other.getName());

        this.organizer = other.organizer;
        this.description = other.description;
        this.address = new Address(other.address);
        this.auditorium = new Auditorium(other.auditorium);
        this.timeOfCompetition = other.timeOfCompetition;

        this.competitionResults = new HashSet<>();
        for (CompetitionResult result : other.competitionResults) {
            this.competitionResults.add(result.withScore(result.score()));
        }
    }



    public MathClub getOrganizer() {
        return organizer;
    }

    public void setOrganizer(MathClub organizer) {
        this.organizer = organizer;
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


    public Optional <CompetitionResult> getCompetitionResultForParticipant(Student participant){

        return competitionResults.stream()
                .filter(result -> result.participant().equals(participant))
                .findFirst();
    }


    public boolean hasParticipant(Student participantToCheck){
        for (CompetitionResult competition : competitionResults){
            if (competition.participant().equals(participantToCheck)){
                return true;
            }
        }
        return false;
    }


    public Optional <Student> findWinner(){

       return competitionResults.stream()
               .max(Comparator.comparing(CompetitionResult::score))
               .map(CompetitionResult::participant);
    }

    public Integer getNumberOfParticipants(){
        return competitionResults.size();
    }

    public BigDecimal getAverageScoreForCompetition(){
        BigDecimal sumOfScores = BigDecimal.ZERO;
        for (CompetitionResult competitionResult : competitionResults){
            sumOfScores = sumOfScores.add(competitionResult.score());
        }

        if(sumOfScores.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }

        return sumOfScores.divide(BigDecimal.valueOf(competitionResults.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAverageFemaleScoreForCompetition(){
        BigDecimal sumOfScores = BigDecimal.ZERO;
        List<CompetitionResult> femaleResults = getCompetitionResults().stream()
                .filter(competitionResult -> competitionResult.participant().getGender().equals("Female")).toList();

        for (CompetitionResult competitionResult : femaleResults){
            sumOfScores = sumOfScores.add(competitionResult.score());
        }

        if(sumOfScores.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }

        return sumOfScores.divide(BigDecimal.valueOf(femaleResults.size()), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getAverageMaleScoreForCompetition(){
        BigDecimal sumOfScores = BigDecimal.ZERO;
        List<CompetitionResult> maleResults = getCompetitionResults().stream()
                .filter(competitionResult -> competitionResult.participant().getGender().equals("Male")).toList();

        for (CompetitionResult competitionResult : maleResults){
            sumOfScores = sumOfScores.add(competitionResult.score());
        }

        if(sumOfScores.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }

        return sumOfScores.divide(BigDecimal.valueOf(maleResults.size()), 2, RoundingMode.HALF_UP);
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


    @Override
    public Optional<Change> getChange(Competition newValueToCompare) {
        if (this.equals(newValueToCompare)) {
            return Optional.empty();
        } else {
            StringBuilder oldValueBuilder = new StringBuilder();
            StringBuilder newValueBuilder = new StringBuilder();

            compareAndAppend("Ime", this.getName(), newValueToCompare.getName(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Opis", this.getDescription(), newValueToCompare.getDescription(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Adresa", this.getAddress(), newValueToCompare.getAddress(), oldValueBuilder, newValueBuilder);
            compareAndAppend("", this.getAuditorium(), newValueToCompare.getAuditorium(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Vrijeme", this.getTimeOfCompetition(), newValueToCompare.getTimeOfCompetition(), oldValueBuilder, newValueBuilder);
            compareAndAppend("Rezultati", this.getCompetitionResults(), newValueToCompare.getCompetitionResults(), oldValueBuilder, newValueBuilder);

            return Optional.of(Change.create(
                    SessionManager.getInstance().getCurrentUser(),
                    oldValueBuilder.toString(),
                    newValueBuilder.toString(),
                    "Natjecanje/id:" + this.getId()
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
