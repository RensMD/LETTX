package nl.tudelft.lab.lettx.dao;

import com.embeddedunveiled.serial.SerialComException;
import com.embeddedunveiled.serial.SerialComManager;
import nl.tudelft.lab.lettx.domain.LettTestData;
import nl.tudelft.lab.lettx.service.DataListenerService;

import java.io.IOException;

/**
 * Lettx application
 /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Implementation for Serial Port communication
 */

public class SerialComManagerDaoImpl implements SerialPortCommDao {

    // Serial ports
    private static final String WINDOWS_SERIAL_PORT = "COM";
    private static final String APPLE_SERIAL_PORT1 = "tty";
    private static final String APPLE_SERIAL_PORT2 = "dev";
    private static final String APPLE_SERIAL_PORT3 = "cu";

    // Messages
    private static final String MESSAGE_COM_INIT = "Communication initialized.";

    // Arduino commands
    private static final String LETT_TEST_START = "I";
    private static final String LETT_TEST_ABORT = "C";

    private String serialPortNumber;
    private long handle;
    private boolean isFirstCommand = true;

    private SerialComManager serialComManager = new SerialComManager();
    private DataListenerService dataListenerService = new DataListenerService();
    private LettTestData testData = new LettTestData();

    public SerialComManagerDaoImpl() throws IOException {
    }

    /**
     * Write command to SerialPort.
     *
     * @param command
     */
    public void writeCommand(String command) {
        try {
            //TODO unnecessary when data send directly? (send the "C" directly upon connecting and when pressing refresh)
            if (isFirstCommand) {
                initializeSerialCommunication();
                System.out.println(MESSAGE_COM_INIT);
                sendCommand(command);
                isFirstCommand = false;
            } else {
                sendCommand(command);
                if (command.contains(LETT_TEST_START)) {
                    dataListenerService.setLettTestData(testData);
                }
            }
        } catch (SerialComException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send command to Arduino.
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
        String firstCommand = LETT_TEST_ABORT;
        while (!response.contains(firstCommand)) {
            sendCommand(firstCommand);
            // try to read data from serial port
            response = serialComManager.readString(handle, 1);
            System.out.println("Data read is :" + response);
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
        // TODO: success bool usable for refresh?
        boolean succes = false;
        try {
            handle = serialComManager.openComPort(serialPortNumber, true, true, true);
            serialComManager.configureComPortData(handle, SerialComManager.DATABITS.DB_8, SerialComManager.STOPBITS.SB_1, SerialComManager.PARITY.P_NONE, SerialComManager.BAUDRATE.B19200, 0);
            serialComManager.configureComPortControl(handle, SerialComManager.FLOWCONTROL.NONE, 'x', 'x', false, false);
            serialComManager.registerDataListener(handle, dataListenerService);
        } catch (SerialComException e) {
            e.printStackTrace();
        }
        return succes=true;
    }

//    // TODO: Refresh working again
//    public void refreshSerialPort() {
//        try {
//            serialComManager.closeComPort(handle);
//        } catch (SerialComException e) {
//            e.printStackTrace();
//        }
//        if(getAvailablePorts()!=null){
//            try {
//                initializeSerialCommunication();
//            } catch (SerialComException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * Validate selected input for serial port.
     */
    private boolean isSelectedPortValid() {
        //TODO: could provide problem on apple in current state
        return this.serialPortNumber.contains(WINDOWS_SERIAL_PORT) || this.serialPortNumber.contains(APPLE_SERIAL_PORT1)|| this.serialPortNumber.contains(APPLE_SERIAL_PORT2)|| this.serialPortNumber.contains(APPLE_SERIAL_PORT3);
    }

    /**
     * Find available com ports.
     *
     * @return
     */
    public String[] getAvailablePorts() {
        String[] comPorts;
        comPorts = new String[]{};
        try {
            comPorts = serialComManager.listAvailableComPorts();
        } catch (SerialComException e) {
            e.getStackTrace();
        }
        return comPorts;
    }

    public void setFileLocation(String fileLocation_Current) {
        testData.setFileLocation(fileLocation_Current);
    }

    public void setFileName(String fileName_Current) {
        testData.setFileName(fileName_Current);
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

