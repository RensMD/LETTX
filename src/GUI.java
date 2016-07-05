import jssc.SerialPort;
import jssc.SerialPortException;
import jssc.SerialPortList;

import java.io.*;
import java.io.IOException;
import java.nio.file.Paths;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

/**
 * Created by Rens Doornbusch on 2-6-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 * - Extends the functionality of the LETT with compression test *
 */

public class GUI extends JPanel {

    //String elongationPrint;
    //Double pwmSignal = null;

    //Float velocity = null;
    private static String elongation;
    private static String force = null;
    private static String time = null;

    private JPanel LettxJpanel;
    private static JFrame frame = new JFrame("GUI");
    private JButton fileLocationButton;
    private JFileChooser fc;
    private static String fileLocation = null;
    private JTextField fileNameField;
    private JButton gripUpButton;
    private JButton gripDownButton;
    JComboBox<String> forceComboBox;
    JComboBox<String> speedComboBox;
    JComboBox<String> testComboBox;
    private static String testString_Current;
    private static String forceString_Current;
    private static String speedString_Current;
    private JButton startButton;
    private boolean startButtonStop = false;
    private static Boolean stopNow = false;
    private static JFrame frame2 = new JFrame("Serial Pop-Up");
    private static JTextArea log;
    private static JTextField COMField;
    private static JButton COMButton;
    private static boolean closed = false;

    private static SerialPort serialPort;

    private static boolean stopReading= false;
    private static String LETTnumber;


    public static void main(String[] args) {

        log = new JTextArea(5,31);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);

        COMButton = new JButton("Choose Port");
        COMField = new JTextField("");
        COMField.setPreferredSize( new Dimension(200, 24));
        COMButton.addActionListener(actionEvent -> closed = true);

        frame.setContentPane(new GUI().LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        int i, inp_num = 0;
        String input;

        // getting a list of the available serial ports
        String[] portNames = SerialPortList.getPortNames();

        boolean valid_answer = false;
        if(portNames.length !=1) {
            frame.setVisible(false);
            createCOMPopUp();
            // choosing the port to connect to
            if (portNames.length > 0) {
                log.append("Multiple serial ports have been detected:\n");
            } else {
                COMField.setVisible(false);
                COMButton.setVisible(false);
                log.append("Sorry, no serial ports were found on your computer.\n");
                log.append("Program will exit soon...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
            for (i = 0; i < portNames.length; ++i) {
                log.append("    " + Integer.toString(i + 1) + ":  " + portNames[i] + "\n");
            }
            while (!valid_answer) {
                log.append("\n Please, enter the number in front of the port name to choose.");
                try {
                    input = textFieldInput();
                    inp_num = Integer.parseInt(input);
                    if ((inp_num < 1) || (inp_num >= portNames.length + 1))
                        log.append("your input is not valid");
                    else
                        valid_answer = true;
                } catch (NumberFormatException ex) {
                    log.append("please enter a correct number");
                }
            }
        }
        else{
            inp_num=1;
        }
        serialPort = new SerialPort(portNames[inp_num-1]);
        try {
            serialPort.openPort();//Open serial port
            serialPort.setParams(19200, 8, 1, 0);//Set params.
        }
        catch (SerialPortException ex) {
            System.out.println(ex);
        }
        parseInput();
    }

    private static void parseInput() {

        while (!stopReading) {
            byte[] buffer = new byte[0];//Read 10 bytes from serial port
            try {
                buffer = serialPort.readBytes(200);
            } catch (SerialPortException e) {
                e.printStackTrace();
            }
            String message = new String(buffer);
            String[] splitMessage = message.split("\n");
            for (int i = 0; i < splitMessage.length; i++) splitMessage[i] = splitMessage[i].trim(); // Trim message
            if(Objects.equals(splitMessage[0], "start")) {
//                log.append(splitMessage[0] + '\n');
//                log.append(splitMessage[1] + '\n');
//                log.append(splitMessage[2] + '\n');
//                log.append(splitMessage[3] + '\n');
//                log.append(splitMessage[4] + '\n');

                LETTnumber = splitMessage[1];
                elongation = splitMessage[2];
                force = splitMessage[3];
                time = splitMessage[4];
                stopReading=true;
            }
            else{
                // TODO: solve problem with repetitive error
            }
        }
        stopReading=false;
    }


    private GUI() {
        super(new BorderLayout());
        JScrollPane logScrollPane = new JScrollPane(log);
        JPanel buttonPanel = new JPanel();
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
                try {
                    serialPort.writeBytes("A".getBytes());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        });
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                try {
                    serialPort.writeBytes("H".getBytes());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        });
        // Down
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                try {
                    serialPort.writeBytes("B".getBytes());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        });
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                try {
                    serialPort.writeBytes("G".getBytes());
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        });
        // Start
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (startButtonStop) {
                    stopNow = true;
                    try {
                        serialPort.writeBytes("C".getBytes());
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                    startButton.setText("STOPPED!");
                }
                else{
                    startButtonStop = true;
                    startButton.setText("STOP");
                    start();
                }
            }
        });
    }

    private void start() {
        //Check for empty fields
        if (Objects.equals(fileNameField.getText(), "")) {
            System.out.println("please input text");
        } else {
            if (Objects.equals(fileLocation, "")) {
                System.out.println("No File location selected\n");
            } else {

                //Send information about current test to Arduino
                switch (testString_Current) {
                    case "Tension": {
                        try {
                            serialPort.writeBytes("T".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "Compression": {
                        try {
                            serialPort.writeBytes("R".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        System.out.println("Wrong Test input\n");
                        break;
                }
                switch (forceString_Current) {
                    case "100Kg": {
                        try {
                            serialPort.writeBytes("E".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "500Kg": {
                        try {
                            serialPort.writeBytes("F".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        System.out.println("Wrong Force input\n");
                        break;
                }
                switch (speedString_Current) {
                    case "10 mm/min": {
                        try {
                            serialPort.writeBytes("1".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "20 mm/min": {
                        try {
                            serialPort.writeBytes("2".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "50 mm/min": {
                        try {
                            serialPort.writeBytes("3".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    case "100 mm/min": {
                        try {
                            serialPort.writeBytes("4".getBytes());
                        } catch (SerialPortException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                    default:
                        System.out.println("Wrong Speed input\n");
                        break;
                }

                // Create new text File

                String fileName = fileNameField.getText();
                System.out.println(fileLocation + "\\" + fileName + ".txt");
                File textFile = new File(fileLocation + "\\" + fileName + ".txt");
                FileOutputStream is = null;
                try {
                    is = new FileOutputStream(textFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                assert is != null;
                OutputStreamWriter osw = new OutputStreamWriter(is);
                Writer w = new BufferedWriter(osw);

                // Write Standard information to file
                parseInput();
                try {
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
                    w.write("LETT #:\t\t" + LETTnumber  + System.getProperty("line.separator"));
                    w.write(System.getProperty("line.separator"));
                    w.write("Time (s)\tDistance (mm)\tForce (N)" + System.getProperty("line.separator"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Start test
                if (!stopNow || !Objects.equals(elongation, "a")) {
                    try {
                        serialPort.writeBytes("I".getBytes());
                    } catch (SerialPortException e) {
                        e.printStackTrace();
                    }
                }

                parseInput();

//                // TODO: procedure loop!
//                while (!stopNow || !Objects.equals(elongation, "a")) {
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                // done or cancelled
//                int temp[] = {'C'};
//                network.writeSerial(1, temp);
//                network.writeSerial("test");
//                // TODO: reader
//                // network.SerialReader();
//                // Stop timer
//                if (stopNow) {
//                    System.out.println("cancelled");
//                }

                // Close the file
                try {
                    w.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private static String textFieldInput() {
        while(!closed){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String idInput = COMField.getText();
        frame.setVisible(true);
        //TODO: reenter dispose after tests
        //frame2.dispose();
        return idInput;
    }

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

    private static void createCOMPopUp() {
        //Create and set up the window.
        frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame2.add(new GUI());

        //Display the window.
        frame2.pack();
        frame2.setLocationRelativeTo(null);
        frame2.setVisible(true);
    }

}





