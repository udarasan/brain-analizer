package com.example.brainanalizer.unused;

import jssc.*;

import java.util.Arrays;


public class EEGSerialReader {
    private SerialPort serialPort;
    private static final int HEADER = 0xAA55;
    private static final int FOOTER = 0x55AA;
    private static final int STATE_IDLE = 0;
    private static final int STATE_HEADER_FOUND = 1;
    private static final int STATE_DATA_FOUND = 2;

    private static int currentState = STATE_IDLE;
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
                            processReceivedData(receivedData);
                        } catch (SerialPortException ex) {
                            System.out.println("Error in receiving data: " + ex);
                        }
                    }
                }
            });

        } catch (SerialPortException ex) {
            System.out.println("There are an error opening port: " + ex);
        }
    }
    private static void processReceivedData(byte[] buffer) {
        // Print the raw byte values in hexadecimal
        System.out.print("Received Data: ");
        for (byte b : buffer) {
            System.out.print(String.format("%02X ", b));
        }
        System.out.println();

        for (byte b : buffer) {
            switch (currentState) {
                case STATE_IDLE:
                    if (b == (byte) 0xAA) {
                        currentState = STATE_HEADER_FOUND;
                    }
                    break;
                case STATE_HEADER_FOUND:
                    if (b == (byte) 0x55) {
                        currentState = STATE_DATA_FOUND;
                    } else {
                        currentState = STATE_IDLE;
                    }
                    break;
                case STATE_DATA_FOUND:
                    // Process the data
                    if (buffer.length >= 3) {
                        byte dataType = buffer[3];
                        byte value = buffer[4];

                        switch (dataType) {
                            case 0x01:
                                // RAW_EEG_DATA
                                System.out.println("RAW EEG Data: " + value);
                                break;
                            case 0x02:
                                // FFT_DATA
                                System.out.println("FFT Data: " + value);
                                break;
                            case 0x03:
                                // FREQUENCY_BAND_DATA
                                System.out.println("Frequency Band Data: " + value);
                                break;
                            case 0x04:
                                // SIGNAL_QUALITY_DATA
                                System.out.println("Signal Quality Data: " + value);
                                break;
                            default:
                                // Unknown data type
                                System.out.println("Unknown Data Type");
                        }

                        currentState = STATE_IDLE;
                    }
                    break;
                default:
                    currentState = STATE_IDLE;
                    break;
            }
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
    }

    private void processFFTData(byte[] data) {
        // Implement logic to process FFT Analysis Data
        System.out.println("FFT Data: " + bytesToHex(data));
    }

    private void processFrequencyBandData(byte[] data) {
        // Implement logic to process Frequency Band Data
        System.out.println("Frequency Band Data: " + bytesToHex(data));
    }

    private void processSignalQualityData(byte[] data) {
        // Implement logic to process Signal Quality Data
        System.out.println("Signal Quality Data: " + bytesToHex(data));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }

    public void stop() {
        if (serialPort != null && serialPort.isOpened()) {
            try {
                serialPort.removeEventListener();
                serialPort.closePort();
            } catch (SerialPortException ex) {
                System.out.println("Error closing port: " + ex);
            }
        }
    }

    public static void main(String[] args) {
        EEGSerialReader reader = new EEGSerialReader();
        reader.start("COM4"); // Replace with your actual serial port
    }
}