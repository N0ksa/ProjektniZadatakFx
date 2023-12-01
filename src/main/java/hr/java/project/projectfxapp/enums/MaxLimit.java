package hr.java.project.projectfxapp.enums;

/**
 * Predstavlja maksimalna ograničenja za različite kategorije.
 */
public enum MaxLimit {
     MAX_NUMBER_OF_STUDENTS(3),
     MAX_NUMBER_OF_PROFESSORS(0),
     MAX_NUMBER_OF_MATH_CLUBS(2),
     MAX_NUMBER_OF_MATH_PROJECTS(2),
     MAX_NUMBER_OF_MATH_COMPETITIONS(2);

     private Integer maxNumber;
     MaxLimit(Integer maxNumber){
         this.maxNumber = maxNumber;
     }

    public Integer getMaxNumber() {
        return maxNumber;
    }
}
