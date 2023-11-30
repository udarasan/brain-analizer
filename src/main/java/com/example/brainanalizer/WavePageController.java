package com.example.brainanalizer;

/**
 * @TimeStamp 2023-11-29 19:43
 * @ProjectDetails brain-analizer
 */

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class WavePageController implements Initializable {
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

    private XYChart.Series<String, Number> series1 =new XYChart.Series<>();
    private XYChart.Series<String, Number> series2=new XYChart.Series<>();
    private XYChart.Series<String, Number> series3=new XYChart.Series<>();
    private XYChart.Series<String, Number> series4=new XYChart.Series<>();
    private XYChart.Series<String, Number> series5=new XYChart.Series<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        series1 = new XYChart.Series<>();
//        lineChart.getData().add(series1);
//
//        // Set up a timeline to generate random data every second
//        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> updateChart()));
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
        series1.getData().add(new XYChart.Data<>("1",2));
        series1.getData().add(new XYChart.Data<>("2",1));
        series1.getData().add(new XYChart.Data<>("3",4));
        series2.getData().add(new XYChart.Data<>("1",1));
        series2.getData().add(new XYChart.Data<>("2",3));
        series2.getData().add(new XYChart.Data<>("3",4));
        series3.getData().add(new XYChart.Data<>("1",1));
        series3.getData().add(new XYChart.Data<>("2",3));
        series3.getData().add(new XYChart.Data<>("3",4));
        series4.getData().add(new XYChart.Data<>("1",1));
        series4.getData().add(new XYChart.Data<>("2",3));
        series4.getData().add(new XYChart.Data<>("3",4));
        series5.getData().add(new XYChart.Data<>("1",1));
        series5.getData().add(new XYChart.Data<>("2",3));
        series5.getData().add(new XYChart.Data<>("3",4));

        alphaLineChart.getData().add(series1);
        betaLineChart.getData().add(series2);
        thetaLineChart.getData().add(series3);
        gammaLineChart.getData().add(series4);
        deltaLineChart.getData().add(series5);
        //exportChartDataToCSV();
    }
    /*private void updateChart() {
        Random random = new Random();
        int x = series1.getData().size() * 1;  // Assuming you want to add data every 1 units on the x-axis
        int y = random.nextInt(3) + 1;  // Random y value between 1 and 6

        series1.getData().add(new XYChart.Data<>(String.valueOf(x), y));

        // Optionally, remove data points to keep the chart from becoming too crowded
        if (series1.getData().size() > 100) {
            series1.getData().remove(0);
        }
    }*/

    public void exportToCSV(ActionEvent actionEvent) {
        // Create a FileChooser to let the user choose where to save the CSV file
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                // Write the header
                writer.write("X, Series1, Series2, Series3, Series4, Series5");
                writer.newLine();

                // Write the data
                for (int i = 0; i < series1.getData().size(); i++) {
                    writer.write(series1.getData().get(i).getXValue() + ",");
                    writer.write(series1.getData().get(i).getYValue() + ",");
                    writer.write(series2.getData().get(i).getYValue() + ",");
                    writer.write(series3.getData().get(i).getYValue() + ",");
                    writer.write(series4.getData().get(i).getYValue() + ",");
                    writer.write(series5.getData().get(i).getYValue() + ",");
                    writer.newLine();
                }

                System.out.println("CSV file saved successfully!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}