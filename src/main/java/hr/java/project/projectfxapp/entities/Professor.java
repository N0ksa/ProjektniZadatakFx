package hr.java.project.projectfxapp.entities;

import java.io.Serializable;
import java.util.Objects;

/**
 * Predstavlja profesora.
 */

public class Professor extends NamedEntity implements Serializable {
    private String surname;
    private String email;

    /**
     * Konstruktor za stvaranje nove instance profesora".
     * @param id Id profesora.
     * @param name  Ime profesora.
     * @param surname  Prezime profesora.
     * @param email Adresa elektroničke pošte profesora.
     */
    public Professor(Long id, String name, String surname, String email) {
        super(id, name);
        this.surname = surname;
        this.email = email;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Professor professor = (Professor) o;
        return Objects.equals(surname, professor.surname) && Objects.equals(email, professor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), surname, email);
    }
}
