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

    private static SerialPort serialPort;

    private boolean startReceived;
    private String timeOld;
    private String elongation;
    private String force = null;
    private String time = null;
    private int lineNumber = 0;
    private Writer w;
    private String LETTNumber;

    private boolean stopNow = false;

    private String portNumber;
    private String fileLocation;
    private String fileName;


    private String testString_Current;
    private String forceString_Current;
    private String speedString_Current;


   /**
     * Write command to SerialPort.
     * @param command
     */
    public void writeCommand(String command) {
        if (serialPort == null) {
            initSerialPort();
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
            initSerialPort();
        }
        if(event.isRXCHAR()){//If data is available
            if(event.getEventValue() > 7) {//Check bytes count in the input buffer
                //Read data, if 10 bytes available
                byte[] buffer = new byte[0];
                try {
                    // TODO: fixed buffer size -> should be adapting to incoming string size?
                    buffer = serialPort.readBytes(50);
                } catch (SerialPortException ex) {
//                        System.out.println(ex);
                }
                String message = new String(buffer);
                String[] splitMessage = message.split("\n");
                for (int i = 0; i < splitMessage.length; i++) splitMessage[i] = splitMessage[i].trim(); // Trim message

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
                            try {
                                w.write(lineNumber + ":\t" + elongation + "\t" + force + "\t" + time + "\t" + System.getProperty("line.separator"));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                serialPort.writeBytes(String.valueOf(lineNumber).getBytes());
                            } catch (SerialPortException e) {
                                e.printStackTrace();
                            }
                            lineNumber++;
                            timeOld=time;
                        }

                    }
                    else if(Objects.equals(splitMessage[n], "start")){
                        LETTNumber = splitMessage[n+1];
                        startReceived=true;
                        try {
                            serialPort.writeBytes("O".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        beginFound=true;
                    }
                    else{
                        n++;
                    }
                }

            }
        }

    }

    private void initSerialPort() {
        serialPort = new SerialPort(portNumber);
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(19200, 8, 1, 0);//Set params.
            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
            serialPort.setEventsMask(mask);//Set mask
            serialPort.addEventListener(this);//Add SerialPortEventListener
        } catch (SerialPortException ex) {
//            System.out.println(ex);
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
            w.write("LETT #:\t\t" + LETTNumber + System.getProperty("line.separator"));
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

    public void setPortNumber(String portNumber) {
        this.portNumber = portNumber;
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
