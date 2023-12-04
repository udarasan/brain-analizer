package com.example.brainanalizer;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
            serialPort.addEventListener(event -> {
                if (event.isRXCHAR() && event.getEventValue() > 0) {
                    try {
                        byte[] receivedData = serialPort.readBytes();
                        processData(receivedData);
                    } catch (SerialPortException ex) {
                        System.out.println("Error in receiving data: " + ex);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void processData(byte[] data) {
        // Implement data processing logic according to the protocol
        // Extract header, message type, payload, etc.
        if (data.length > 6) { // Minimum length check
            int header = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
            int messageType = data[2] & 0xFF;

            if (header == HEADER) {
                switch (messageType) {
                    case 0x01: // Command
                        processCommand(data);
                        break;
                    case 0x02: // Data
                        processDataMessage(data);
                        break;
                    default:
                        System.out.println(header);
                        System.out.println("Unknown message type");
                }
            }
        }
    }
    private void processCommand(byte[] data) {
        // Process command data
        // Extract command and act accordingly
        int command = data[3] & 0xFF;
        switch (command) {
            case 0x01: // Start Command
                System.out.println("Start Command Received");
                break;
            case 0x00: // Stop Command
                System.out.println("Stop Command Received");
                break;
            default:
                System.out.println("Unknown Command");
        }
    }

    private void processDataMessage(byte[] data) {
        // Process different types of data messages
        int dataType = data[3] & 0xFF;
        System.out.println(dataType);
        switch (dataType) {
            case 0x01: // Raw EEG Data
                processRawEEGData(Arrays.copyOfRange(data, 4, data.length - 2));
                break;
            case 0x02: // FFT Analysis Data
                processFFTData(Arrays.copyOfRange(data, 4, data.length - 2));
                break;
            case 0x03: // Frequency Band Data
                processFrequencyBandData(Arrays.copyOfRange(data, 4, data.length - 2));
                break;
            case 0x04: // Signal Quality Data
                processSignalQualityData(Arrays.copyOfRange(data, 4, data.length - 2));
                break;
            default:
                System.out.println("Unknown Data Type");
        }
    }

    private void processRawEEGData(byte[] data) {
        // Implement logic to process Raw EEG Data
        System.out.println("Raw EEG Data: " + bytesToHex(data));
        for (byte b : data) {
            System.out.println("Byte: " + (b & 0xFF));
        }
    }

    private void processFFTData(byte[] data) {
        // Implement logic to process FFT Analysis Data
        System.out.println("FFT Data: " + bytesToHex(data));
        for (byte b : data) {
            System.out.println("Byte: " + (b & 0xFF));
        }
    }

    private void processFrequencyBandData(byte[] data) {
        // Implement logic to process Frequency Band Data
        System.out.println("Frequency Band Data: " + bytesToHex(data));
        for (byte b : data) {
            System.out.println("Byte: " + (b & 0xFF));
        }
    }

    private void processSignalQualityData(byte[] data) {
        // Implement logic to process Signal Quality Data
        System.out.println("Signal Quality Data: " + bytesToHex(data));
        for (byte b : data) {
            System.out.println("Byte: " + (b & 0xFF));
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public byte[] readData() {
        try {
            return serialPort.readBytes();
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
        closeConnection();
        if (dropDownMenu.getValue() != null) {
            System.out.println(dropDownMenu.getValue());

            // Select the port
            String selectedPort = dropDownMenu.getValue().toString();
            // Connect to the port
            connect(selectedPort);
        }



    }
}
