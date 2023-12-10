package com.example.brainanalizer.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableData {

    private final SimpleStringProperty timeStamp;
    private final SimpleIntegerProperty eegData;
    private final SimpleDoubleProperty fttData;

    public TableData(String timeStamp, int eegData, double fttData) {
        this.timeStamp = new SimpleStringProperty(timeStamp);
        this.eegData = new SimpleIntegerProperty(eegData);
        this.fttData = new SimpleDoubleProperty(fttData);
    }

    public String getTimeStamp() {
        return timeStamp.get();
    }

    public int getEegData() {
        return eegData.get();
    }

    public double getFttData() {
        return fttData.get();
    }
}
