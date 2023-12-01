module hr.java.project.projectfxapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;


    opens hr.java.project.projectfxapp to javafx.fxml;
    exports hr.java.project.projectfxapp;
}