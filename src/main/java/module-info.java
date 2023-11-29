module com.example.brainanalizer {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.brainanalizer to javafx.fxml;
    exports com.example.brainanalizer;
}