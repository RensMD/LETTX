package nl.tudelft.lab.lettx.dao;

/**
 * Created by Rens Doornbusch on 7-7-2016.
 */
public interface SerialPortCommDao {

    /**
     * Write command to SerialPort.
     *
     * @param command
     */
    public void writeCommand(String command);


    /**
     * Start communication with serial port.
     */
    public boolean startCommunication(String portNumber);


    public static void refreshSerialPort() {
    }

    public void createTestReport();

    /* getting a list of the available serial ports */
    public String[] getAvailablePorts();

    public void setFileLocation(String fileLocation);

    public void setFileName(String fileName);

    public void setTestString_Current(String testString_Current);

    public void setForceString_Current(String forceString_Current);

    public void setSpeedString_Current(String speedString_Current);

}
