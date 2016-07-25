package nl.tudelft.lab.lettx.dao;

import com.embeddedunveiled.serial.SerialComException;
import com.embeddedunveiled.serial.SerialComManager;
import nl.tudelft.lab.lettx.domain.LettTestData;
import nl.tudelft.lab.lettx.service.DataListenerService;

/**
 * Created by Rens on 22-7-2016.
 */
public class SerialComManagerDaoImpl implements SerialPortCommDao {

    private String serialPortNumber;
    private long handle;
    boolean isFirstCommand = true;

    private SerialComManager serialComManager = new SerialComManager();
    private DataListenerService dataListenerService = new DataListenerService();
    private LettTestData testData = new LettTestData();

    /**
     * Write command to SerialPort.
     *
     * @param command
     */
    public void writeCommand(String command) {
        try {
            if (isFirstCommand) {
                initializeSerialCommunication();
                System.out.println("Communication initialized.");
                sendCommand(command);
                isFirstCommand = false;
            } else {
                sendCommand(command);
                if (command.contains("I")) {
                    dataListenerService.setLettTestData(testData);
                }
            }
        } catch (SerialComException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send the command to Arduino.
     *
     * @param command
     * @throws SerialComException
     */
    private void sendCommand(String command) throws SerialComException {
        byte[] bytesCommand = command.getBytes();
        serialComManager.writeSingleByte(handle, bytesCommand[0]);
        System.out.println("Message to arduino: " + command);
    }

    /**
     * Initialize communication with Arduino.
     * On Windows 10 the communication sequence does not start without this procedure.
     *
     * @throws SerialComException
     */
    private void initializeSerialCommunication() throws SerialComException {
        String response = "";
        String firstCommand = "C";
        while (!response.contains(firstCommand)) {
            sendCommand(firstCommand);
            // try to read data from serial port
            response = serialComManager.readString(handle, 1);
            System.out.println("data read is :" + response);
        }
    }

    /**
     * Start communication with serial port.
     */
    public boolean startCommunication(String portNumber) {
        this.serialPortNumber = portNumber;
        if (isSelectedPortValid()) {
            openSerialPort();
        }
        return handle > 0;
    }

    /**
     * Initialize and open the serial communication port.
     */
    private boolean openSerialPort() {
        boolean succes = false;
        try {
            handle = serialComManager.openComPort(serialPortNumber, true, true, true);
            serialComManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_NONE, SerialComManager.BAUDRATE.B19200, 0);
            serialComManager.configureComPortControl(handle, SerialComManager.FLOWCONTROL.NONE, 'x', 'x', false, false);
            serialComManager.registerDataListener(handle, dataListenerService);
        } catch (SerialComException e) {
            e.printStackTrace();
        }
        return succes;
    }

    /**
     * Validate selected input for serial port.
     */
    private boolean isSelectedPortValid() {
        //TODO: could provide problem on apple in current state
        return this.serialPortNumber.contains("COM") || this.serialPortNumber.contains("tty");
    }

    public void createTestReport(LettTestData testdata) {
        // no implementation needed
    }

    /**
     * Find available com ports.
     *
     * @return
     */
    public String[] getAvailablePorts() {
        String[] comPorts = null;
        try {
            comPorts = serialComManager.listAvailableComPorts();
        } catch (SerialComException e) {
            e.printStackTrace();
        }
        return comPorts;
    }

    public void setFileLocation(String fileLocation) {
        testData.setFileLocation(fileLocation);
    }

    public void setFileName(String fileName) {
        testData.setName(fileName);
    }

    public void setTestString_Current(String testString_Current) {
        testData.setType(testString_Current);
    }

    public void setForceString_Current(String forceString_Current) {
        testData.setForce(forceString_Current);
    }

    public void setSpeedString_Current(String speedString_Current) {
        testData.setSpeed(speedString_Current);
    }

}

