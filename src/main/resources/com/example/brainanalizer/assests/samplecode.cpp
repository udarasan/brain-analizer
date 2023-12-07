
#include <Arduino.h>
// Protocol Constants
const byte HEADER[] = {0xAA, 0x55}; const byte FOOTER[] = {0x55, 0xAA};
// Message Types
const byte COMMAND = 0x01; const byte DATA = 0x02;
// Data Types
const byte RAW_EEG_DATA = 0x01;
const byte FFT_DATA = 0x02;
const byte FREQUENCY_BAND_DATA = 0x03; const byte SIGNAL_QUALITY_DATA = 0x04;
// Frequency Band Identifiers
const byte DELTA = 0x01;
const byte THETA = 0x02;
const byte ALPHA = 0x03;
const byte BETA = 0x04;
const byte GAMMA = 0x05;

void setup() { Serial.begin(9600);
}
void loop() {
// Example: Sending different types of data with random values
sendDataType(RAW_EEG_DATA, random(100)); // Random Raw EEG Data
sendDataType(FFT_DATA, random(100)); // Random FFT Data
sendDataType(FREQUENCY_BAND_DATA, ALPHA); // Frequency Band Data for Alpha
sendDataType(SIGNAL_QUALITY_DATA, random(0, 101)); // Random Signal Quality
delay(1000); // Wait for 1 second
}
void sendDataType(byte dataType, byte value) {
byte payload[2] = {dataType, value};
byte payloadLength = sizeof(payload);
byte checksum = calculateChecksum(payload, payloadLength);
// Sending the message
Serial.write(HEADER, sizeof(HEADER));
Serial.write(DATA);
Serial.write(payloadLength);
Serial.write(payload, payloadLength);
Serial.write(checksum);
Serial.write(FOOTER, sizeof(FOOTER));

}
byte calculateChecksum(byte *payload, byte length) { byte sum = 0;
    for (byte i = 0; i < length; i++) {
sum += payload[i]; }
return sum % 256; // Modulo 256 for checksum }