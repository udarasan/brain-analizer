package com.example.brainanalizer;



import com.fazecast.jSerialComm.SerialPort;

public class ArduinoSerialReader {

    public static void main(String[] args) {
        SerialPort serialPort = SerialPort.getCommPort("COM4"); // Adjust the port name

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

    private static void processReceivedData(byte[] buffer, int bytesRead) {
        // Assuming the message format matches the Arduino protocol
        // You need to implement the logic to interpret the received data here
        // For simplicity, let's just print the received bytes as integers
        for (int i = 0; i < bytesRead; i++) {
            System.out.print(buffer[i] & 0xFF); // Convert to unsigned byte
            System.out.print(" ");
        }
        System.out.println();
    }
}


