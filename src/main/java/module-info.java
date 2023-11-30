module com.example.brainanalizer {
    requires javafx.controls;
    requires javafx.fxml;
    requires jssc;


    opens com.example.brainanalizer to javafx.fxml;
    exports com.example.brainanalizer;
}