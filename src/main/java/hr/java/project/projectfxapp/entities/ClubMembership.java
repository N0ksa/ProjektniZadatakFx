package hr.java.project.projectfxapp.entities;

import hr.java.project.projectfxapp.enums.ValidationRegex;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Predstavlja članstvo u matematičkom klubu.
 */
public class ClubMembership implements Serializable {

    private Long clubMembershipId;
    private Long clubId;
    private LocalDate joinDate;

    /**
     * Konstruktor za stvaranje instance članstva u matematičkom klubu.
     *
     * @param joinDate     Datum pridruživanja klubu.
     * @param clubId Identifikacijski broj članstva.
     */
    public ClubMembership(Long clubMembershipId, Long clubId, LocalDate joinDate) {
        this.clubMembershipId = clubMembershipId;
        this.clubId = clubId;
        this.joinDate = joinDate;
    }

    public Long getClubMembershipId() {
        return clubMembershipId;
    }

    public void setClubMembershipId(Long clubMembershipId) {
        this.clubMembershipId = clubMembershipId;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMembership that = (ClubMembership) o;
        return Objects.equals(clubMembershipId, that.clubMembershipId) &&
                Objects.equals(clubId, that.clubId)
                && Objects.equals(joinDate, that.joinDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clubMembershipId, clubId, joinDate);
    }


    @Override
    public String toString() {
        return "Datum pridruživanja: " + joinDate.format(DateTimeFormatter
                .ofPattern(ValidationRegex.VALID_LOCAL_DATE_REGEX.getRegex()))
                + ", identifikacijski broj članstva: " + clubMembershipId;
    }
}
