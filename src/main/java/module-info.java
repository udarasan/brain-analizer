module com.example.brainanalizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires jssc;
    requires com.fazecast.jSerialComm;


    opens com.example.brainanalizer to javafx.fxml;
    exports com.example.brainanalizer;
    exports com.example.brainanalizer.controller;
    opens com.example.brainanalizer.controller to javafx.fxml;
    exports com.example.brainanalizer.utill;
    opens com.example.brainanalizer.utill to javafx.fxml;
    exports com.example.brainanalizer.unused;
    opens com.example.brainanalizer.unused to javafx.fxml;
    exports com.example.brainanalizer.model;
    opens com.example.brainanalizer.model to javafx.fxml;
}