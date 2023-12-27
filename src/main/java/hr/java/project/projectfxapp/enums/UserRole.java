package hr.java.project.projectfxapp.enums;

public enum UserRole {
    ADMIN("Admin"),
    USER("User");

    private final String name;
    UserRole(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }


    public static UserRole getRoleByName(String name) {
        for (UserRole role : values()) {
            if (role.name.equalsIgnoreCase(name)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Ne postoji u enumeraciji navedena uloga: " + name);

    }
}
