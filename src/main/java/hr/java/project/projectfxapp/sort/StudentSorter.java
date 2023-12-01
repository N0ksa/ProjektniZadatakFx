package hr.java.project.projectfxapp.sort;



import hr.java.project.projectfxapp.entities.Student;

import java.util.Comparator;

/**
 * Implementira sučelje {@link Comparator} za sortiranje objekata tipa {@link Student} po prezimenu.
 */
public class StudentSorter implements Comparator <Student> {

    private boolean ascendingOrder;

    /**
     * Konstruktor za stvaranje instance ProductionSorter.
     * @param ascendingOrder Ako je true sortira se uzlazno, ako je false sortira se silazno.
     */
    public StudentSorter(boolean ascendingOrder){
        this.ascendingOrder = ascendingOrder;
    }

    @Override
    public int compare(Student s1, Student s2) {
        if (ascendingOrder){
            return s1.getSurname().compareTo(s2.getSurname());
        }
        else{
            return s2.getSurname().compareTo(s1.getSurname());
        }

    }


}
