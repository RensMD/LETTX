package nl.tudelft.lab.lettx.dao;

import nl.tudelft.lab.lettx.domain.LettTestData;

/**
 * Lettx application
 /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 */

public interface SerialPortCommDao {

    /**
     * Write command to SerialPort.
     *
     * @param command
     */
    void writeCommand(String command);


    /**
     * Start communication with serial port.
     */
    boolean startCommunication(String portNumber);


    static void refreshSerialPort() {
    }

    void createTestReport(LettTestData testData);

    /* getting a list of the available serial ports */
    String[] getAvailablePorts();

    void setFileLocation(String fileLocation);

    void setFileName(String fileName);

    void setTestString_Current(String testString_Current);

    void setForceString_Current(String forceString_Current);

    void setSpeedString_Current(String speedString_Current);

}
