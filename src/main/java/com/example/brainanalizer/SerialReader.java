package com.example.brainanalizer;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

public class SerialReader {

    private static final int[] HEADER = { 0xAA, 0x55 };
    private static final int[] FOOTER = { 0x55, 0xAA };

    private static final int HEADER_LENGTH = 2;
    private static final int FOOTER_LENGTH = 2;
    private static final int MIN_MESSAGE_LENGTH = HEADER_LENGTH + 1 + 1 + FOOTER_LENGTH; // Header + DataType + Length + Footer

    private static SerialPort serialPort;

    public static void main(String[] args) {
        serialPort = new SerialPort("COM4"); // Replace with your actual serial port

        try {
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0);
            serialPort.addEventListener(new SerialPortEventListener() {
                @Override
                public void serialEvent(SerialPortEvent event) {
                    if (event.isRXCHAR() && event.getEventValue() > 0) {
                        try {
                            byte[] buffer = serialPort.readBytes(event.getEventValue());
                            processReceivedData(buffer);
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private static void processReceivedData(byte[] buffer) {
        System.out.println(buffer.length);
        for (int i = 0; i < buffer.length; i++) {
            if (buffer[i] == HEADER[0] && (i + 1) < buffer.length && buffer[i + 1] == HEADER[1]) {
                // Found the start of a message
                if (buffer.length - i >= MIN_MESSAGE_LENGTH) {
                    byte dataType = buffer[i + HEADER_LENGTH];
                    byte length = buffer[i + HEADER_LENGTH + 1];
                    byte[] payload = new byte[length];

                    if (buffer.length - i >= MIN_MESSAGE_LENGTH + length) {
                        // Extract the payload
                        System.arraycopy(buffer, i + HEADER_LENGTH + 2, payload, 0, length);

                        // Print the payload value based on data type
                        switch (dataType) {
                            case 0x01:
                                System.out.println("Raw EEG Data: " + payload[0]);
                                break;
                            case 0x02:
                                System.out.println("FFT Data: " + payload[0]);
                                break;
                            case 0x03:
                                System.out.println("Frequency Band Data: " + payload[0]);
                                break;
                            case 0x04:
                                System.out.println("Signal Quality: " + payload[0]);
                                break;
                            default:
                                System.out.println("Unknown Data Type");
                        }

                        // Move the index to the end of the message
                        i += MIN_MESSAGE_LENGTH + length - 1;
                    }
                }
            }
        }
    }
}
