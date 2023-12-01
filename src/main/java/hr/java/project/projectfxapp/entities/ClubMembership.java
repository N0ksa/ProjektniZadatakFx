package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Predstavlja članstvo u matematičkom klubu.
 */
public class ClubMembership implements Serializable {
    private String membershipId;
    private LocalDate joinDate;

    /**
     * Konstruktor za stvaranje instance članstva u matematičkom klubu.
     *
     * @param joinDate     Datum pridruživanja klubu.
     * @param membershipId Identifikacijski broj članstva.
     */
    public ClubMembership(String membershipId, LocalDate joinDate) {
        this.membershipId = membershipId;
        this.joinDate = joinDate;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getMembershipId() {
        return membershipId;
    }

    public void setMembershipId(String membershipId) {
        this.membershipId = membershipId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClubMembership that = (ClubMembership) o;
        return Objects.equals(joinDate, that.joinDate) && Objects.equals(membershipId, that.membershipId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(joinDate, membershipId);
    }
}
