package nl.tudelft.lab.lettx.service;

import com.embeddedunveiled.serial.ISerialComDataListener;
import com.embeddedunveiled.serial.SerialComDataEvent;

/**
 * Created by Rens on 22-7-2016.
 */
public class DataListenerService implements ISerialComDataListener {
    boolean isDataReceived = false;
    boolean isTestStartReceived = false;
    boolean isTestEndReceived = false;
    StringBuilder message = new StringBuilder();

    @Override
    public void onNewSerialDataAvailable(SerialComDataEvent dataEvent) {
        int byteCount = dataEvent.getDataBytesLength();
        isDataReceived = byteCount > 0;
        byte[] receivedData = dataEvent.getDataBytes();
        String receivedString = new String(receivedData);
        message.append(receivedString);
        isTestStartReceived = message.indexOf("I") > -1;
        if (isTestStartReceived) {
            isTestEndReceived = message.indexOf("C") > -1;
            if (isTestEndReceived) {
                int splitPosStart = message.indexOf("I") + 1;
                int start = 0;
                message.delete(0, splitPosStart);
                String testMessage = message.toString();
                String[] splitMessage = testMessage.split("/n");
                String lettNr = splitMessage[0];
                System.out.println("lett: " + lettNr);
            }
        }
        System.out.println("Message from Arduino: " + dataEvent.getDataBytesLength() + " byte(s): " + receivedString);
        System.out.println("Message in Stringbuilder: " + message.toString());
    }

    @Override
    public void onDataListenerError(int errorNum) {
        System.out.println("Error: " + errorNum);
    }

}
