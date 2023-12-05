module com.example.brainanalizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires jssc;
    requires com.fazecast.jSerialComm;


    opens com.example.brainanalizer to javafx.fxml;
    exports com.example.brainanalizer;
}