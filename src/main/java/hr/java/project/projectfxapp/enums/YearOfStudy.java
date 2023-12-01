package hr.java.project.projectfxapp.enums;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * Predstavlja različite godine studija sa popisom predmeta koji se pohađaju u svakoj godini.
 */
public enum YearOfStudy implements Serializable {
    FIRST_YEAR(1, Arrays.asList("Elementarna matematika 1", "Linearna algebra 1",
                                    "Matematička analiza 1", "Programiranje 1",
                                    "Elementarna matematika 2", "Linearna algebra 2",
                                    "Matematička analiza 2", "Programiranje 2")),

    SECOND_YEAR(2, Arrays.asList("Diferencijalni račun funkcija više varijabli", "Diskretna matematika",
                                        "Strukture podataka i algoritmi", "Vjerojatnost", "Uvod u analizu podataka",
                                       "Integrali funkcija više varijabli", "Numerička matematika", "Teorija brojeva",
                                        "Računarski praktikum 1")),

    THIRD_YEAR (3, Arrays.asList("Algebarske strukture", "Obične diferencijalne jednadžbe", "Teorija skuppova",
            "Vektorski prostori", "Matematička logika", "Kompleksna analiza", "Mjera i integral", "Statistika",
            "Metode matematičke fizike"));


    private Integer year;
    private List<String> availableSubjects;


     YearOfStudy(Integer year, List <String> availableSubjects){
        this.year = year;
        this.availableSubjects = availableSubjects;
    }

    public Integer getYear() {
        return year;
    }

    public List<String> getAvailableSubjects() {
        return availableSubjects;
    }

}
