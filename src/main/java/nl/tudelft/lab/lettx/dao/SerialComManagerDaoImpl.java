package nl.tudelft.lab.lettx.dao;

import com.embeddedunveiled.serial.SerialComException;
import com.embeddedunveiled.serial.SerialComManager;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rens on 22-7-2016.
 */
public class SerialComManagerDaoImpl implements SerialPortCommDao { //implements SerialPortCommDao{

    // Communication parameters
    public static final int BAUD_RATE = 19200;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = 1;
    public static final int PARITY = 0;

    // LETT test parameters
    public static final String LETT_START_DATA = "%";
    public static final String LETT_MESSAGE_SPLIT = ":";
    public static final String LETT_MESSAGE_END = "#";
    public static final String LETT_NUMBER_START = "$";
    public static final String LETT_STOP_TEST = "&";

    private static SerialPort serialPort;

    private boolean startReceived;
    private String timeOld;
    private String elongation;
    private String force = null;
    private String time = null;
    private int lineNumber = 0;
    private PrintWriter w;

    // number of test-workstation
    private String LETTNumber;

    private boolean stopNow = false;
    private boolean isAcknowledged = false;

    private String serialPortNumber;
    private String fileLocation;
    private String fileName;


    private String testString_Current;
    private String forceString_Current;
    private String speedString_Current;
    private int countBufferRead = 0;
    private long handle;

    SerialComManager serialComManager = new SerialComManager();

    /**
     * Write command to SerialPort.
     *
     * @param command
     */
    public void writeCommand(String command) {
        String data = "";
        try {
            while (!data.contains(command)) {
                serialComManager.writeBytes(handle, command.getBytes(), 0);
                System.out.println("Message to arduino: " + command);
                // try to read data from serial port
                data = serialComManager.readString(handle);
                System.out.println("data read is :" + data);
            }
        } catch (SerialComException e) {
            e.printStackTrace();
        }
    }



/*
    @Override
    public void serialEvent(SerialPortEvent event) {
        // If data is available
        if (event.isRXCHAR() && event.getEventValue() > 0) {
            //Read data
            String message = extractMessageFromBuffer(event);
            System.out.println("Message from buffer: " + message);

            String[] splitMessage = message.split(LETT_MESSAGE_SPLIT);
            for (int i = 0; i < splitMessage.length; i++) {
                splitMessage[i] = splitMessage[i].trim(); // Trim message
                isAcknowledged = splitMessage[i].contains("#$&");
            }

            boolean beginFound = false;
            int n = 0;
            while (!beginFound) {
                if (Objects.equals(splitMessage[n], LETT_STOP_TEST)) {
                    stopNow = true;
                } else if (Objects.equals(splitMessage[n], LETT_START_DATA)) {
                    elongation = splitMessage[n + 1];
                    force = splitMessage[n + 2];
                    time = splitMessage[n + 3];
                    beginFound = true;
                    if (!Objects.equals(timeOld, time)) {
                        addDataLineToLogfile();
                        writeCommand(String.valueOf(lineNumber));
                        lineNumber++;
                        timeOld = time;
                    }
                } else if (Objects.equals(splitMessage[n], LETT_NUMBER_START)) {
                    LETTNumber = splitMessage[n + 1];
                    startReceived = true;
                    //writeCommand("O");
                    beginFound = true;
                } else {
                    n++;
                }
            }
        } else {
            int type = event.getEventType();
            int value = event.getEventValue();
        }

    }
*/

    private void addDataLineToLogfile() {
        w.println(lineNumber + ":\t" + elongation + "\t" + force + "\t" + time + "\t" + System.getProperty("line.separator"));
    }

    private String extractMessageFromBuffer(SerialPortEvent event) {
        countBufferRead += 1;
        System.out.println("read input buffer count: " + countBufferRead);
        System.out.println("EventValue: " + event.getEventValue());

        // read all bytes from buffer
        byte[] buffer = new byte[0];
        try {
            buffer = serialPort.readBytes(event.getEventValue());
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }
        return new String(buffer);
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

    public void createTestReport() {
        // Create new text File
        File dir = new File(fileLocation + "\\lettx");
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        File textFile = new File(dir + "\\" + fileName + ".txt");
        System.out.println(textFile.getAbsolutePath());
        try {
            textFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write Standard information to file
        try {
            w = new PrintWriter(new FileWriter(textFile));
            w.println("Developed by:\t\tPieter Welling & Rens Doornbusch");
            w.println("\t\t\tTU Delft");
            w.println();
            w.println("Test Name:\t\t" + fileName);
            w.println("Date & Time:\t\t" + todDay());
            w.println("Speed:\t\t\t" + speedString_Current);
            w.println("Load Cell:\t\t" + forceString_Current);
            w.println("Test Type:\t\t" + testString_Current);
            w.println("LETT #:\t\t\t" + LETTNumber);
            w.println();
            w.println("Time (s)\tDistance (mm)\tForce (N)");

            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create formatted date of today.
     *
     * @return formatted String representation of today's date
     */
    private String todDay() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

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
        this.fileLocation = fileLocation;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setTestString_Current(String testString_Current) {
        this.testString_Current = testString_Current;
    }

    public void setForceString_Current(String forceString_Current) {
        this.forceString_Current = forceString_Current;
    }

    public void setSpeedString_Current(String speedString_Current) {
        this.speedString_Current = speedString_Current;
    }

}

