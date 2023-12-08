package com.example.brainanalizer.controller;

import com.example.brainanalizer.utill.ArduinoSerialReader;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import jssc.*;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @FXML
    private LineChart<String, Number> alphaLineChart;
    @FXML
    private LineChart<String, Number> betaLineChart;
    @FXML
    private LineChart<String, Number> thetaLineChart;
    @FXML
    private LineChart<String, Number> gammaLineChart;
    @FXML
    private LineChart<String, Number> deltaLineChart;
    private static final int MAX_DATA_POINTS = 50;

    ObservableList<String> devices = FXCollections.observableArrayList();
    private void setUi(String location)  {
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
        loadAllPorts();
        drawStaticAlphaChart();
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

    public void exportToCSV(ActionEvent actionEvent) {
    }
    private void drawStaticAlphaChart() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Alpha Values");
        alphaLineChart.getData().add(series);

    }

    // New method to update the alpha chart
    public void updateAlphaChart(int rawEegValue) {
        Platform.runLater(() -> {
            // Use LocalTime to get a human-readable time label
            LocalTime currentTime = LocalTime.now();

            // Format the time label to a more readable format (HH:mm:ss)
            String timeLabel = currentTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            // Get the series from the chart
            XYChart.Series<String, Number> series = alphaLineChart.getData().get(0);

            // Add a new data point to the series
            series.getData().add(new XYChart.Data<>(timeLabel, rawEegValue));

            // Optional: Limit the number of data points to prevent the chart from becoming too crowded
            if (series.getData().size() > MAX_DATA_POINTS) {
                series.getData().remove(0);
            }
        });
    }
}
