module hr.java.project.projectfxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires java.sql;


    opens hr.java.project.projectfxapp to javafx.fxml;
    exports hr.java.project.projectfxapp;
    exports hr.java.project.projectfxapp.controllers;
    opens hr.java.project.projectfxapp.controllers to javafx.fxml;
    exports hr.java.project.projectfxapp.enums;
}