module hr.java.project.projectfxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;
    requires javafx.web;

    opens hr.java.project.projectfxapp to javafx.fxml;
    exports hr.java.project.projectfxapp.enums;
    opens hr.java.project.projectfxapp.controllers.admin to javafx.fxml;
    exports hr.java.project.projectfxapp.controllers.admin;
    exports hr.java.project.projectfxapp.controllers.users;
    opens hr.java.project.projectfxapp.controllers.users to javafx.fxml;
    opens hr.java.project.projectfxapp.controllers.shared to javafx.fxml;
    exports hr.java.project.projectfxapp.controllers.shared;
    exports hr.java.project.projectfxapp;
}