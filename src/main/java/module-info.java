module org.example.projectcalendar {
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires java.sql;
    requires com.calendarfx.view;
    requires java.prefs;
    requires jdk.compiler;

    opens org.example.projectcalendar to javafx.fxml;
    exports org.example.projectcalendar;
    exports org.example.projectcalendar.controllers;
    opens org.example.projectcalendar.controllers to javafx.fxml;
}