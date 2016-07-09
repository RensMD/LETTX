package nl.tudelft.lab.lettx.dao;

import jssc.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Rens Doornbusch on 7-7-2016.
 */
public class SerialPortCommDao implements SerialPortEventListener {

    // Communication parameters
    public static final int BAUD_RATE = 19200;
    public static final int DATA_BITS = 8;
    public static final int STOP_BITS = 1;
    public static final int PARITY = 0;

    private static SerialPort serialPort;

    private boolean startReceived;
    private String timeOld;
    private String elongation;
    private String force = null;
    private String time = null;
    private int lineNumber = 0;
    private Writer w;

    // number of test-workstation
    private String LETTNumber;

    private boolean stopNow = false;

    private String serialPortNumber;
    private String fileLocation;
    private String fileName;


    private String testString_Current;
    private String forceString_Current;
    private String speedString_Current;
    private int countBufferRead = 0;

   /**
     * Write command to SerialPort.
     * @param command
     */
    public void writeCommand(String command) {
        if (serialPort == null) {
            openSerialPort();
        }
        try {
            serialPort.writeBytes(command.getBytes());
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent event) {
        if (serialPort == null) {
            openSerialPort();
        }
        // If data is available
        if(event.isRXCHAR()){
            //Read data, if there are at least 7 bytes available in the input buffer for start and some data
            if(event.getEventValue() > 7) {
                String message = extractMessageFromBuffer(event);
                System.out.println("Message from buffer: " + message);

                String[] splitMessage = message.split(":");
                for (int i = 0; i < splitMessage.length; i++) {
                    splitMessage[i] = splitMessage[i].trim(); // Trim message
                }

                boolean beginFound = false;
                int n=0;
                while(!beginFound){
                    if (Objects.equals(splitMessage[n], "stop")) {
                        stopNow=true;
                    }
                    else if (Objects.equals(splitMessage[n], "data")) {
                        elongation = splitMessage[n+1];
                        force = splitMessage[n+2];
                        time = splitMessage[n+3];
                        beginFound=true;
                        if(!Objects.equals(timeOld, time)){
                            addDataLineToLogfile();
                            writeCommand(String.valueOf(lineNumber));
                            lineNumber++;
                            timeOld=time;
                        }
                    }
                    else if(Objects.equals(splitMessage[n], "@")){
                        if(Objects.equals(splitMessage[n+2], "#")) {
                            LETTNumber = splitMessage[n+1];
                            startReceived = true;
                            writeCommand("O");
                            beginFound = true;
                        }
                        else{
                            n++;
                        }
                    }
                    else{
                        n++;
                    }
                }

            }
        }

    }

    private void addDataLineToLogfile() {
        try {
            w.write(lineNumber + ":\t" + elongation + "\t" + force + "\t" + time + "\t" + System.getProperty("line.separator"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String extractMessageFromBuffer(SerialPortEvent event) {
         countBufferRead += 1;
         System.out.println("read input buffer count: " + countBufferRead);
         System.out.println("EventValue: " + event.getEventValue());

         // read all bytes from buffer
        byte[]  buffer = new byte[0];
        try {
            buffer = serialPort.readBytes(event.getEventValue());
        } catch (SerialPortException ex){
            System.out.println(ex);
        }
        return new String(buffer);
    }

    /**
     * Initialize and open the serial communication port.
     */
    private void openSerialPort() {
        serialPort = new SerialPort(serialPortNumber);
        try {
            serialPort.setParams(BAUD_RATE, DATA_BITS, STOP_BITS, PARITY);
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;
            serialPort.setEventsMask(mask);
            serialPort.addEventListener(this);
            serialPort.openPort();
        } catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }

    public void createTestLog() {
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
            w = new BufferedWriter(new FileWriter(textFile));
            w.write("Developed by:\t\tPieter Welling & Rens Doornbusch" + System.getProperty("line.separator"));
            w.write("\t\t\tTU Delft" + System.getProperty("line.separator"));
            w.write(System.getProperty("line.separator"));
            w.write("Test Name:\t\t"+ fileName + System.getProperty("line.separator"));
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            w.write("Date & Time:\t\t"+ dateFormat.format(date) + System.getProperty("line.separator"));
            w.write("Speed:\t\t\t" + speedString_Current + System.getProperty("line.separator"));
            w.write("Load Cell:\t\t" + forceString_Current + System.getProperty("line.separator"));
            w.write("Test Type:\t\t" + testString_Current  + System.getProperty("line.separator"));
            w.write("LETT #:\t\t\t" + LETTNumber + System.getProperty("line.separator"));
            w.write(System.getProperty("line.separator"));
            w.write("Time (s)\tDistance (mm)\tForce (N)" + System.getProperty("line.separator"));
            w.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* getting a list of the available serial ports */
    public String[] getAvailablePorts() {
        return SerialPortList.getPortNames();
    }

    public void setSerialPortNumber(String serialPortNumber) {
        this.serialPortNumber = serialPortNumber;
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
