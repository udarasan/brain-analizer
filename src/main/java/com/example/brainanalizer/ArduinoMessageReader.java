package com.example.brainanalizer;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

import java.io.UnsupportedEncodingException;

public class ArduinoMessageReader {

    private SerialPort serialPort;

    public void start(String portName) {
        serialPort = new SerialPort(portName);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.isRXCHAR() && event.getEventValue() > 0) {
                        try {
                            byte[] receivedData = serialPort.readBytes();
                            String message = new String(receivedData, "UTF-8"); // Assuming UTF-8 encoding
                            processMessage(message);
                        } catch (SerialPortException | UnsupportedEncodingException ex) {
                            System.err.println("Error in receiving data: " + ex.getMessage());
                        }
                    }
                }
            });

        } catch (SerialPortException ex) {
            System.err.println("Error opening port: " + ex.getMessage());
        }
    }
    private StringBuilder receivedMessage = new StringBuilder();
    private void processMessage(String message) {
        // Append the received message
        receivedMessage.append(message);

        // Check if the message ends with the delimiter
        if (message.endsWith("\n")) {
            // Process the complete message
            System.out.println("Received message: " + receivedMessage.toString());

            // Reset the StringBuilder for the next message
            receivedMessage.setLength(0);
        }
    }

    public void stop() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.removeEventListener();
                serialPort.closePort();
            } catch (SerialPortException ex) {
                System.err.println("Error closing port: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        ArduinoMessageReader reader = new ArduinoMessageReader();
        reader.start("COM4"); // Replace with your actual serial port
    }
}

