package com.example.brainanalizer.controller;

import com.example.brainanalizer.model.TableData;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import jssc.*;

import java.io.File;
import java.io.FileWriter;
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
    public TextField portID;
    public TextArea messageArea;
    public ComboBox dropDownMenu;
    @FXML
    public Label txtSignalQualityId;
    @FXML
    public Label txtLeftAriPod;
    @FXML
    public Label txtRightAriPod;
    public TableView<TableData> rawDataTable;
    @FXML
    public TableColumn<TableData, String> colTimeStamp;
    @FXML
    public TableColumn<TableData, Integer> colEEGData;
    @FXML
    public TableColumn<TableData, Double> colFTTData;

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
    ObservableList<TableData> tableDataList = FXCollections.observableArrayList();

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
        initializeTable();
    }
    private void initializeTable() {
        colTimeStamp.setCellValueFactory(new PropertyValueFactory<>("timeStamp"));
        colEEGData.setCellValueFactory(new PropertyValueFactory<>("eegData"));
        colFTTData.setCellValueFactory(new PropertyValueFactory<>("fttData"));
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
        dropDownMenu.setItems(devices);
    }


    public void OnEdit(ListView.EditEvent editEvent) {

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
        // Prompt user to choose a file location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");

        // Set extension filter to only allow CSV files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog
        Window window = context.getScene().getWindow();
        File file = fileChooser.showSaveDialog(window);

        if (file != null) {
            saveTableDataToCSV(file);
        }
    }
    private void saveTableDataToCSV(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write CSV header
            writer.append("Time Stamp, EEG Data, FTT Data\n");

            // Write data to CSV
            ObservableList<TableData> data = rawDataTable.getItems();
            for (TableData tableData : data) {
                writer.append(tableData.getTimeStamp()).append(",");
                writer.append(String.valueOf(tableData.getEegData())).append(",");
                writer.append(String.valueOf(tableData.getFttData())).append("\n");
            }

            System.out.println("CSV file saved successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception as needed
            System.err.println("Error saving CSV file.");
        }
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
            XYChart.Series<String, Number> series1 = alphaLineChart.getData().get(0);
            //XYChart.Series<String, Number> series2 = deltaLineChart.getData().get(0);
            //XYChart.Series<String, Number> series3 = gammaLineChart.getData().get(0);
            //XYChart.Series<String, Number> series4 = betaLineChart.getData().get(0);
            //XYChart.Series<String, Number> series5 = thetaLineChart.getData().get(0);

            // Add a new data point to the series
            series1.getData().add(new XYChart.Data<>(timeLabel, rawEegValue));
            //series2.getData().add(new XYChart.Data<>(timeLabel, rawEegValue));
            //series3.getData().add(new XYChart.Data<>(timeLabel, rawEegValue));
            //series4.getData().add(new XYChart.Data<>(timeLabel, rawEegValue));
            //series5.getData().add(new XYChart.Data<>(timeLabel, rawEegValue));
            // Add data to the table
            TableData tableData = new TableData(timeLabel, rawEegValue, 5);
            tableDataList.add(tableData);
            rawDataTable.setItems(tableDataList);


            // Optional: Limit the number of data points to prevent the chart from becoming too crowded
            if (series1.getData().size() > MAX_DATA_POINTS) {
                series1.getData().remove(0);
            }
        });
    }
}
