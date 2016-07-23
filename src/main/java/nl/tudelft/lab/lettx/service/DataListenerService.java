package nl.tudelft.lab.lettx.service;

import com.embeddedunveiled.serial.ISerialComDataListener;
import com.embeddedunveiled.serial.SerialComDataEvent;

/**
 * Created by Rens on 22-7-2016.
 */
public class DataListenerService implements ISerialComDataListener{
    boolean isDataReceived = false;
    StringBuilder message = new StringBuilder();

    @Override
    public void onNewSerialDataAvailable(SerialComDataEvent dataEvent) {
        int byteCount = dataEvent.getDataBytesLength();
        isDataReceived = byteCount > 0;
        byte[] receivedData = dataEvent.getDataBytes();
        String receivedString = new String(receivedData);
        System.out.println("Message from Arduino: " + dataEvent.getDataBytesLength() + " byte(s): " + receivedString);
    }

    @Override
    public void onDataListenerError(int errorNum) {
        System.out.println("Error: " + errorNum);
    }

}
