package com.example.brainanalizer.utill;

import com.example.brainanalizer.controller.DashboardController;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;

public class ArduinoSerialReader {
    private static DashboardController dashboardController;

    //setter method through dependency injection
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    public void connection(String port) {
        SerialPort serialPort = SerialPort.getCommPort(port); // Adjust the port name

        if (serialPort.openPort()) {
            serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);

            try {
                while (true) {
                    if (serialPort.bytesAvailable() > 0) {
                        byte[] buffer = new byte[serialPort.bytesAvailable()];
                        int bytesRead = serialPort.readBytes(buffer, buffer.length);
                        // Process the received data
                        processReceivedData(buffer, bytesRead);
                    }
                    // Add a delay to avoid busy-waiting
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                serialPort.closePort();
            }
        } else {
            System.err.println("Error: Could not open the serial port.");
        }
    }

//    private static void processReceivedData(byte[] buffer, int bytesRead) {
//        // Assuming the message format matches the Arduino protocol
//        // You need to implement the logic to interpret the received data here
//        // For simplicity, let's just print the received bytes as integers
//        for (int i = 0; i < bytesRead; i++) {
//            System.out.print(buffer[i] & 0xFF); // Convert to unsigned byte
//            System.out.print(" ");
//        }
//        System.out.println();
//    }
private static void processReceivedData(byte[] buffer, int bytesRead) {
    // Assuming the message format matches the Arduino protocol
    // You need to implement the logic to interpret the received data here
    // For simplicity, let's just print the received values based on data types

    // Check if the message starts with the correct header
    if (bytesRead >= 4 && buffer[0] == (byte) 0xAA && buffer[1] == (byte) 0x55) {
        byte messageType = buffer[2];

        switch (messageType) {
            case 0x02: // DATA message
                byte dataType = buffer[4];

                switch (dataType) {
                    case 0x01: // RAW_EEG_DATA
                        int rawEegValue = buffer[5] & 0xFF; // Convert to unsigned byte
                        System.out.println("Received RAW_EEG_DATA: " + rawEegValue);
                    case 0x02: // FFT_DATA
                        int fftValue = buffer[14] & 0xFF;
                        System.out.println("Received FFT_DATA: " + fftValue);
                    case 0x03: // FREQUENCY_BAND_DATA
                        byte frequencyBand = buffer[23];
                        System.out.println("Received FREQUENCY_BAND_DATA: " + getFrequencyBandName(frequencyBand));
                    case 0x04: // SIGNAL_QUALITY_DATA
                        int signalQuality = buffer[32] & 0xFF;
                        System.out.println("Received SIGNAL_QUALITY_DATA: " + signalQuality);
                        if (dashboardController != null) {
                            dashboardController.setSignalQualityOnTopBar(signalQuality);
                        }
                    default:
                        System.out.println("Unknown data type: " + dataType);
                }
                break;
            default:
                System.out.println("Unknown message type: " + messageType);
                break;
        }
    } else {
        System.out.println("Invalid message header");
    }
}

    private static String getFrequencyBandName(byte frequencyBand) {
        switch (frequencyBand) {
            case 0x01:
                return "DELTA";
            case 0x02:
                return "THETA";
            case 0x03:
                return "ALPHA";
            case 0x04:
                return "BETA";
            case 0x05:
                return "GAMMA";
            default:
                return "Unknown Frequency Band";
        }
    }

}


