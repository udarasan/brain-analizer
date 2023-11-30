package com.example.brainanalizer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jssc.*;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
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
    private SerialPort serialPort;
    ObservableList<String> devices = FXCollections.observableArrayList();
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


    public void searchPort(ActionEvent actionEvent) {
      connect(portID.getText());
    }

    public void connect(String portName) {
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(
                    SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE
            );
            System.out.println("Done");
            System.out.println(readData());
            messageArea.appendText(readData().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String readData() {
        try {
            return serialPort.readString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void closeConnection() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.closePort();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void OnEdit(ListView.EditEvent editEvent) {

    }

    public void searchDevices(ActionEvent actionEvent) {
        devices.clear();
        String[] portNames = SerialPortList.getPortNames();

        // Print the list of available ports
        System.out.println("Available Bluetooth Devices:");
        for (String portName : portNames) {
            System.out.println(portName);
            devices.add(portName);
        }
        panelId.setItems(devices);
    }

    public void OnMouseClick(MouseEvent mouseEvent) {
        String selectedDevice = panelId.getSelectionModel().getSelectedItem().toString();
        if (selectedDevice != null) {
            System.out.println("Selected Bluetooth Device: " + selectedDevice);
            portID.setText(selectedDevice);
        } else {
            System.out.println("No device selected.");
        }
    }
}
