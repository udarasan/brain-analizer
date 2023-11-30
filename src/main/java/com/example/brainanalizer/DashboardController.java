package com.example.brainanalizer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @TimeStamp 2023-11-29 17:53
 * @ProjectDetails brain-analizer
 */
public class DashboardController implements Initializable {
    public Pane context;

    private void setUi(String location) throws IOException {
        context.getChildren().clear();
        context.getChildren().add(FXMLLoader.
                load(this.getClass().
                        getResource("/com/example/brainanalizer/"+location+".fxml")));
    }
    public void wavePageMenuItem(MouseEvent mouseEvent) throws IOException {
        setUi("wavepage");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setUi("wavepage");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}