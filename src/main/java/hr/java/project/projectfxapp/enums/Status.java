package hr.java.project.projectfxapp.enums;

public enum Status {

    FINISHED("Završeno"),
    ONGOING("U tijeku"),
    PLANNED("Planirano");

    private final String statusDescription;

    Status(String statusDescription){
        this.statusDescription = statusDescription;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public static Status getStatusFromString(String statusDescription){
        for (Status s: Status.values()){
            if (s.getStatusDescription().equalsIgnoreCase(statusDescription)){
                return s;
            }
        }
        throw new IllegalArgumentException("Ne postoji takav status");
    }

}
