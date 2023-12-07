package com.example.brainanalizer.controller;

import com.example.brainanalizer.utill.ArduinoSerialReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jssc.*;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * @TimeStamp 2023-11-29 17:53
 * @ProjectDetails brain-analizer
 */
public class DashboardController implements Initializable {

    public Pane context;
    public ListView panelId;
    public TextField portID;
    public TextArea messageArea;
    public ComboBox dropDownMenu;
    @FXML
    public Label txtSignalQualityId;
    @FXML
    public Label txtLeftAriPod;
    @FXML
    public Label txtRightAriPod;
    private SerialPort serialPort;
    private static final int HEADER = 0xAA55;
    private static final int FOOTER = 0x55AA;
    ObservableList<String> devices = FXCollections.observableArrayList();
    private void setUi(String location) throws IOException {
        System.out.println("SetUI");
        Platform.runLater(() -> {
            try {
                context.getChildren().clear();
                context.getChildren().add(FXMLLoader.
                        load(Objects.requireNonNull(this.getClass().
                                getResource("/com/example/brainanalizer/" + location + ".fxml"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void wavePageMenuItem(MouseEvent mouseEvent) throws IOException {
        setUi("wavepage");
    }
    public void setSignalQualityOnTopBar(int value){
        // Ensure that txtSignalQualityId is not null before using it
        if (txtSignalQualityId != null) {
            Platform.runLater(() -> {
                txtSignalQualityId.setText(String.valueOf(value)+"%");
                if (value > 0 && value < 50){
                    txtLeftAriPod.setText("Adjust or try a different Ear Tip");
                    txtRightAriPod.setText("Adjust or try a different Ear Tip");
                    txtLeftAriPod.setStyle("-fx-text-fill: red;");
                    txtRightAriPod.setStyle("-fx-text-fill: red;");
                }else {
                    txtLeftAriPod.setText("Good Seal");
                    txtRightAriPod.setText("Good Seal");
                    txtLeftAriPod.setStyle("-fx-text-fill: green;");
                    txtRightAriPod.setStyle("-fx-text-fill: green;");
                }
            });
        } else {
            System.out.println("txtSignalQualityId is null");
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            setUi("wavepage");
            loadAllPorts();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void loadAllPorts(){
        devices.clear();
        String[] portNames = SerialPortList.getPortNames();

        // Print the list of available ports
        System.out.println("Available Bluetooth Devices:");
        for (String portName : portNames) {
            System.out.println(portName);
            devices.add(portName);
        }
        panelId.setItems(devices);
        dropDownMenu.setItems(devices);
    }


    public void OnEdit(ListView.EditEvent editEvent) {

    }

    public void OnMouseClick(MouseEvent mouseEvent) {
        String selectedDevice = (String) panelId.getSelectionModel().getSelectedItem();
        if (selectedDevice != null) {
            System.out.println("Selected Bluetooth Device: " + selectedDevice);
            portID.setText(selectedDevice);
        } else {
            System.out.println("No device selected.");
        }
    }



    public void selectedOnAction(ActionEvent actionEvent) {
        if (dropDownMenu.getValue() != null) {
            System.out.println(dropDownMenu.getValue());

            // Select the port
            String selectedPort = dropDownMenu.getValue().toString();

            // Connect to the port
            new Thread(() -> {
                // Connect to the port
                ArduinoSerialReader arduinoSerialReader = new ArduinoSerialReader();
                arduinoSerialReader.setDashboardController(this); // Pass the controller to ArduinoSerialReader
                arduinoSerialReader.connection(selectedPort);
            }).start();
        }
    }
}
