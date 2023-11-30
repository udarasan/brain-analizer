package com.example.brainanalizer;

/**
 * @TimeStamp 2023-11-30 14:56
 * @ProjectDetails brain-analizer
 */
import jssc.SerialPort;
import jssc.SerialPortList;

public class BluetoothManager {

    private SerialPort serialPort;

    public static String[] getAvailablePorts() {
        return SerialPortList.getPortNames();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String readData() {
        try {
            return serialPort.readBytes().toString();
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
}
