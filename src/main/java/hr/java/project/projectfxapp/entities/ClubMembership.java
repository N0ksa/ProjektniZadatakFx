package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Predstavlja članstvo u matematičkom klubu.
 */
public class ClubMembership implements Serializable {
    private Long clubId;
    private LocalDate joinDate;

    /**
     * Konstruktor za stvaranje instance članstva u matematičkom klubu.
     *
     * @param joinDate     Datum pridruživanja klubu.
     * @param clubId Identifikacijski broj članstva.
     */
    public ClubMembership(Long clubId, LocalDate joinDate) {
        this.clubId = clubId;
        this.joinDate = joinDate;
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
        return Objects.equals(joinDate, that.joinDate) && Objects.equals(clubId, that.clubId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joinDate, clubId);
    }
}
