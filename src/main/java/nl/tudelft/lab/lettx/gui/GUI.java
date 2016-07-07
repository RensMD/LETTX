package nl.tudelft.lab.lettx.gui;

import nl.tudelft.lab.lettx.dao.SerialPortCommDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Lettx application GUI.
 *
 * Created by Rens Doornbusch on 2-6-2016. *
 */

public class GUI extends JPanel {

    private JPanel LettxJpanel;

    private static JFrame frame = new JFrame("GUI");
    private JButton fileLocationButton;
    private JFileChooser fc;
    private String fileLocation = "";
    private JTextField fileNameField;
    private JButton gripUpButton;
    private JButton gripDownButton;
    private JComboBox<String> forceComboBox;
    private JComboBox<String> speedComboBox;
    private JComboBox<String> testComboBox;
    private String testString_Current;
    private String forceString_Current;
    private String speedString_Current;
    private JButton startButton;
    private boolean stopButton = false;
//    private JFrame frame2 = new JFrame("Serial Pop-Up");
    private JTextArea log;
    private JTextField COMField;
    private JButton COMButton;
    private boolean closed = false;


    private String fileName;
    private Writer w;

    private boolean stopNow = false;
    private boolean cancelled = false;
    private String selectedPort;
    private String LETTNumber;

    SerialPortCommDao serialCommDao;


//    private void selectComPort() {
//
//
//        int i, inp_num = 0;
//        String input;

        // getting a list of the available serial ports
//        String[] portNames = SerialPortList.getPortNames();

//        boolean valid_answer = false;
//        if(portNames.length == 1) {
//            frame.setVisible(false);
//            createCOMPopUp();
            // choosing the port to connect to
//            inp_num = 1;
//        } else if (portNames.length > 1) {
//                log.append("Multiple serial ports have been detected:\n");
//            } else if (portNames.length == 0) {
//                COMField.setVisible(false);
//                COMButton.setVisible(false);
//                log.append("Sorry, no serial ports were found on your computer.\n");
//                log.append("Program will exit soon...");
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.exit(0);
//            }
//            for (i = 0; i < portNames.length; ++i) {
//                log.append("\t" + Integer.toString(i + 1) + ":  " + portNames[i] + "\n");
//            }
//            while (!valid_answer) {
//                log.append("\n Please, enter the number in front of the port name to choose.");
//                try {
//                    input = textFieldInput();
//                    inp_num = Integer.parseInt(input);
//                    if ((inp_num < 1) || (inp_num >= portNames.length + 1))
//                        log.append("your input is not valid");
//                    else
//                        valid_answer = true;
//                } catch (NumberFormatException ex) {
//                    log.append("please enter a correct number");
//                }
//            }
//        }
//        else{
//            inp_num=1;
//        }
//        serialPort = new SerialPort(portNames[inp_num-1]);
//        try {
//            serialPort.openPort();//Open serial port
//            serialPort.setParams(19200, 8, 1, 0);//Set params.
//            int mask = SerialPort.MASK_RXCHAR + SerialPort.MASK_CTS + SerialPort.MASK_DSR;//Prepare mask
//            serialPort.setEventsMask(mask);//Set mask
//            serialPort.addEventListener(new SerialPortCommDao());//Add SerialPortEventListener
//        }
//        catch (SerialPortException ex) {
////            System.out.println(ex);
//        }
//
//        // Create new text File
//        fileName = fileNameField.getText();
//        System.out.println(fileLocation + fileName + ".txt");
//        File textFile = new File(fileLocation + fileName + ".txt");
//        FileOutputStream is = null;
//        try {
//            is = new FileOutputStream(textFile);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        assert is != null;
//        OutputStreamWriter osw = new OutputStreamWriter(is);
//        w = new BufferedWriter(osw);
//    }

    private void initGui() {

        JScrollPane logScrollPane = new JScrollPane(log);

        JPanel buttonPanel = new JPanel();

        log = new JTextArea(5,31);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);

        COMButton = new JButton("Choose Port");
        COMField = new JTextField("");
        COMField.setPreferredSize( new Dimension(200, 24));
        COMButton.addActionListener(actionEvent -> closed = true);

        buttonPanel.add(COMField);
        buttonPanel.add(COMButton);

        //Add the buttons and the log to this panel.
        add(buttonPanel, BorderLayout.PAGE_END);
        add(logScrollPane, BorderLayout.CENTER);
        //Create the log first, because the action listeners need to refer to it.

        //Create a file chooser
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // File location
        fileLocationButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int returnVal = fc.showOpenDialog(GUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    fileLocation = Paths.get(String.valueOf(fc.getSelectedFile())).normalize().toString();

                    log.append("Opening: " + file.getName() + ". \n");
                } else {
                    log.append("Open command cancelled by user.\n");
                }
                log.setCaretPosition(log.getDocument().getLength());
                System.out.println("Current Path:");
                System.out.println(fileLocation);
            }
        });

        /* Selection Boxes */
        // Test Type Select
        testComboBox.addActionListener(actionEvent -> {
            JComboBox testComboBox = (JComboBox) actionEvent.getSource();
            testString_Current = (String) testComboBox.getSelectedItem();
        });
        // Force Select
        forceComboBox.addActionListener(actionEvent -> {
            JComboBox forceComboBox = (JComboBox) actionEvent.getSource();
            forceString_Current = (String) forceComboBox.getSelectedItem();
        });
        // Speed Select
        speedComboBox.addActionListener(actionEvent -> {
            JComboBox speedComboBox = (JComboBox) actionEvent.getSource();
            speedString_Current = (String) speedComboBox.getSelectedItem();
        });

        /* Control */
        // Up
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                serialCommDao.writeCommand("A");
//                try {
//                    serialPort.writeBytes("A".getBytes());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
            }
        });
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                serialCommDao.writeCommand("H");
//                try {
//                    serialPort.writeBytes("H".getBytes());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
            }
        });
        // Down
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                serialCommDao.writeCommand("B");
//                try {
//                    serialPort.writeBytes("B".getBytes());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
            }
        });
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                serialCommDao.writeCommand("G");
//                try {
//                    serialPort.writeBytes("G".getBytes());
//                } catch (SerialPortException e) {
//                    e.printStackTrace();
//                }
            }
        });
        // Start
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (stopButton) {
                    stopNow = true;
                    cancelled=true;
                    if (serialCommDao != null) {
                        serialCommDao.writeCommand("C");
                    }
//                    try {
//                        serialPort.writeBytes("C".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                }
                else{
                    stopButton = true;
                    start();
                }
            }
        });


    }

//    private class SerialPortCommDao implements SerialPortEventListener {
//
//        public void serialEvent(SerialPortEvent event) {
//            if(event.isRXCHAR()){//If data is available
//                if(event.getEventValue() > 7) {//Check bytes count in the input buffer
//                    //Read data, if 10 bytes available
//                    byte[] buffer = new byte[0];
//                    try {
//                        // TODO: fixed buffer size -> should be adapting to incoming string size?
//                        buffer = serialPort.readBytes(50);
//                    } catch (SerialPortException ex) {
////                        System.out.println(ex);
//                    }
//                    String message = new String(buffer);
//                    String[] splitMessage = message.split("\n");
//                    for (int i = 0; i < splitMessage.length; i++) splitMessage[i] = splitMessage[i].trim(); // Trim message
//
//                    boolean beginFound = false;
//                    int n=0;
//                    while(!beginFound){
//                        if (Objects.equals(splitMessage[n], "stop")) {
//                            stopNow=true;
//                        }
//                        else if (Objects.equals(splitMessage[n], "data")) {
//                            elongation = splitMessage[n+1];
//                            force = splitMessage[n+2];
//                            time = splitMessage[n+3];
//                            beginFound=true;
//                            if(!Objects.equals(timeOld, time)){
//                                try {
//                                    w.write(lineNumber + ":\t" + elongation + "\t" + force + "\t" + time + "\t" + System.getProperty("line.separator"));
//
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                try {
//                                    serialPort.writeBytes(String.valueOf(lineNumber).getBytes());
//                                } catch (SerialPortException e) {
//                                    e.printStackTrace();
//                                }
//                                lineNumber++;
//                                timeOld=time;
//                            }
//
//                        }
//                        else if(Objects.equals(splitMessage[n], "start")){
//                            LETTNumber = splitMessage[n+1];
//                            startReceived=true;
//                            try {
//                                serialPort.writeBytes("O".getBytes());
//                            } catch (SerialPortException e) {
//                                e.printStackTrace();
//                            }
//                            beginFound=true;
//                        }
//                        else{
//                            n++;
//                        }
//                    }
//
//                }
//            }
//        }
//    }

    public GUI() {
        super(new BorderLayout());
        frame.setContentPane(LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        initGui();
    }

    private void start() {
        // TODO - replace setting filename and filelocation with default choice on textfields
        serialCommDao = new SerialPortCommDao();
        String[] serialPorts = serialCommDao.getAvailablePorts();
        if (serialPorts.length == 0) {
            COMField.setText("no comport available");
        } else {
            COMField.setText(serialPorts[0]);
        }
        if (!COMField.getText().equalsIgnoreCase("no comport available")) {
            startButton.setText("STOP");
            //Check for empty fields
            if (Objects.equals(fileNameField.getText(), "")) {
                fileNameField.setText("test");
//            System.out.println("please input text");
            }
            serialCommDao.setFileName(fileNameField.getText());
            if (Objects.equals(fileLocation, "")) {
                fileLocation = "C:";

//                System.out.println("No File location selected\n");
            }
            serialCommDao.setFileLocation(fileLocation);
            serialCommDao.setPortNumber(COMField.getText());
//        selectComPort();

            //Send information about current test to Arduino
            switch (testString_Current) {
                case "Tension": {
                    serialCommDao.writeCommand("T");
//                    try {
//                        serialPort.writeBytes("T".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                case "Compression": {
                    serialCommDao.writeCommand("R");
//                    try {
//                        serialPort.writeBytes("R".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                default:
                    System.out.println("Wrong Test input\n");
                    break;
            }
            switch (forceString_Current) {
                case "100Kg": {
                    serialCommDao.writeCommand("E");
//                    try {
//                        serialPort.writeBytes("E".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                case "500Kg": {
                    serialCommDao.writeCommand("F");
//                    try {
//                        serialPort.writeBytes("F".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                default:
                    System.out.println("Wrong Force input\n");
                    break;
            }
            switch (speedString_Current) {
                case "10 mm/min": {
                    serialCommDao.writeCommand("1");
//                    try {
//                        serialPort.writeBytes("1".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                case "20 mm/min": {
                    serialCommDao.writeCommand("2");
//                    try {
//                        serialPort.writeBytes("2".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                case "50 mm/min": {
                    serialCommDao.writeCommand("3");
//                    try {
//                        serialPort.writeBytes("3".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                case "100 mm/min": {
                    serialCommDao.writeCommand("4");
//                    try {
//                        serialPort.writeBytes("4".getBytes());
//                    } catch (SerialPortException e) {
//                        e.printStackTrace();
//                    }
                    break;
                }
                default:
                    System.out.println("Wrong Speed input\n");
                    break;
            }

            // TODO: Right way of waiting on events to finish?
//                while(!startReceived){
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

//            // Write Standard information to file
            serialCommDao.setSpeedString_Current(speedString_Current);
            serialCommDao.setTestString_Current(testString_Current);
            serialCommDao.setForceString_Current(forceString_Current);
            serialCommDao.createTestLog();

//            try {
//                w.write("Developed by:\t\tPieter Welling & Rens Doornbusch" + System.getProperty("line.separator"));
//                w.write("\t\t\tTU Delft" + System.getProperty("line.separator"));
//                w.write(System.getProperty("line.separator"));
//                w.write("Test Name:\t\t"+ fileName + System.getProperty("line.separator"));
//                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//                Date date = new Date();
//                w.write("Date & Time:\t\t"+ dateFormat.format(date) + System.getProperty("line.separator"));
//                w.write("Speed:\t\t\t" + speedString_Current + System.getProperty("line.separator"));
//                w.write("Load Cell:\t\t" + forceString_Current + System.getProperty("line.separator"));
//                w.write("Test Type:\t\t" + testString_Current  + System.getProperty("line.separator"));
//                w.write("LETT #:\t\t" + LETTNumber + System.getProperty("line.separator"));
//                w.write(System.getProperty("line.separator"));
//                w.write("Time (s)\tDistance (mm)\tForce (N)" + System.getProperty("line.separator"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }

            //Start test
            if (!stopNow) {
                serialCommDao.writeCommand("I");
            }

            // TODO: Right way of waiting on events to finish?
//                while(!stopNow){
//                    try {
//                        this.wait();
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }

            if(!cancelled){
                startButton.setText("Finished!");
            }
            else{
                startButton.setText("CANCELLED, please restart application!");
            }

//            // Close the file
//            try {
//                w.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        }

//    private void writeCommand(String command) {
//        try {
//            serialPort.writeBytes(command.getBytes());
//        } catch (SerialPortException e) {
//            e.printStackTrace();
//        }
//    }
//        }

//    }

//    private String textFieldInput() {
//        while(!closed){
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        String idInput = COMField.getText();
//        frame.setVisible(true);
//        frame2.dispose();
//        return idInput;
//    }

    private void createUIComponents() {
        String[] testStrings = {"Tension", "Compression"};
        String[] forceStrings = {"500Kg", "100Kg"};
        String[] speedStrings = {"100 mm/min", "20 mm/min", "50 mm/min", "10 mm/min"};
        testComboBox = new JComboBox<>(testStrings);
        forceComboBox = new JComboBox<>(forceStrings);
        speedComboBox = new JComboBox<>(speedStrings);
        testString_Current = testStrings[0];
        forceString_Current = forceStrings[0];
        speedString_Current = speedStrings[0];
        fileNameField = new JTextField(20);
    }

    // TODO - choose COM port from comboBox
//    private void createCOMPopUp() {
//        //Create and set up the window.
//        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//
//        //Add content to the window.
//        frame2.add(new GUI());
//
//        //Display the window.
//        frame2.pack();
//        frame2.setLocationRelativeTo(null);
//        frame2.setVisible(true);
//    }

}





