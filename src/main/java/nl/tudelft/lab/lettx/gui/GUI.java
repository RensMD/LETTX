package nl.tudelft.lab.lettx.gui;

import nl.tudelft.lab.lettx.dao.SerialComManagerDaoImpl;
import nl.tudelft.lab.lettx.dao.SerialPortCommDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * Lettx application
 /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Graphical User Interface class for user interaction
 */

public class GUI extends JPanel {

    // Arduino command for test
    private static final String ARDUINO_START_TEST = "I";
    private static final String ARDUINO_STOP = "C";

    // Arduino commands for Grip
    private static final String ARDUINO_GRIP_DOWN_START = "B";
    private static final String ARDUINO_GRIP_DOWN_STOP = "G";
    private static final String ARDUINO_GRIP_UP_START = "A";
    private static final String ARDUINO_GRIP_UP_STOP = "H";

    private JPanel LettxJpanel;
    private static JFrame frame = new JFrame("GUI");
    private JButton refreshButton;
    private JButton fileLocationButton;
    private JFileChooser fc;
    private JTextField fileNameField;
    private JButton gripUpButton;
    private JButton gripDownButton;
    private JComboBox<String> forceComboBox;
    private JComboBox<String> speedComboBox;
    private JComboBox<String> testComboBox;
    private JComboBox<String> commComboBox;
    private String testString_Current;
    private String forceString_Current;
    private String speedString_Current;
    private String commString_Current;
    private String fileLocation_Current;
    private JButton startButton;
    private JLabel resultsLabel;
    private JTextArea log;

    private boolean stopButton = false;
    private boolean cancelled = false;

    private SerialPortCommDao serialCommDao;
    public static boolean isComActive = false;
    public static boolean testFinished;

    public GUI() {
        super(new BorderLayout());
        frame.setContentPane(LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        initGui();
    }

    private void initGui() {

        JScrollPane logScrollPane = new JScrollPane(log);
        JPanel buttonPanel = new JPanel();

        log = new JTextArea(5, 31);
        log.setMargin(new Insets(5, 5, 5, 5));
        log.setEditable(false);

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
                    fileLocation_Current = Paths.get(String.valueOf(fc.getSelectedFile())).normalize().toString();
                    log.append("Opening: " + file.getName() + ". \n");
                } else {
                    log.append("Open command cancelled by user.\n");
                }
                log.setCaretPosition(log.getDocument().getLength());
                System.out.println("Current Path:");
                System.out.println(fileLocation_Current);
                fileLocationButton.setText(fileLocation_Current);
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
        // Serial Port Select
        commComboBox.addActionListener(actionEvent -> {
            JComboBox commComboBox = (JComboBox) actionEvent.getSource();
            isComActive=false;
            commString_Current = (String) commComboBox.getSelectedItem();
            //TODO: fix refreshing
            //SerialPortCommDao.refreshSerialPort();
            isComActive = serialCommDao.startCommunication(commString_Current);
        });

        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                isComActive=false;

                if (serialCommDao.getAvailablePorts().length > 0) {
                    commComboBox.setModel(new DefaultComboBoxModel (serialCommDao.getAvailablePorts()));
                    commString_Current = (String) commComboBox.getSelectedItem();
                    startButton.setEnabled(true);
                }
                else{
                    commComboBox.setModel(new DefaultComboBoxModel (new String[]{"No port available"}));
                    startButton.setEnabled(false);
                }
//                //TODO: make this work
//                SerialPortCommDao.refreshSerialPort();
                isComActive = serialCommDao.startCommunication(commString_Current);
            }
        });

        /* Controls for Grip */
        // Grip Up when pressed.
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                if(!isComActive) {
                    isComActive = serialCommDao.startCommunication(commString_Current);
                }
                serialCommDao.writeCommand(ARDUINO_GRIP_UP_START);
            }
        });
        // Stop Grip Up when released.
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                serialCommDao.writeCommand(ARDUINO_GRIP_UP_STOP);
            }
        });
        // Grip Down when pressed.
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                if(!isComActive) {
                    isComActive = serialCommDao.startCommunication(commString_Current);
                }
                serialCommDao.writeCommand(ARDUINO_GRIP_DOWN_START);
            }
        });
        // Stop Grip Down when released
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                serialCommDao.writeCommand(ARDUINO_GRIP_DOWN_STOP);
            }
        });
        // Start
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                //TODO: not a stop button after finished
                if (stopButton) {
                    serialCommDao.writeCommand(ARDUINO_STOP);
                    cancelled = true;
                    startButton.setText("CANCELLED!");
                } else {
                    stopButton = true;
                    startButton.setText("STOP");
                    start();
                }
            }
        });
        if (serialCommDao.getAvailablePorts().length == 0) {
            startButton.setEnabled(false);
        }
    }

    private void start() {

        if (!commString_Current.equalsIgnoreCase("No port available")) {
            resultsLabel.setText("Conducting Test...");
            if(!isComActive) {
                isComActive = serialCommDao.startCommunication(commString_Current);
            }
            //TODO: default location for mac OS
            if (Objects.equals(fileLocation_Current, "")) {
                fileLocation_Current = "C:";
                fileLocationButton.setText(fileLocation_Current);
            }
            if(!isComActive) {
                isComActive = serialCommDao.startCommunication(commString_Current);
            }
            //Send information about current test to Arduino
            switch (testString_Current) {
                case "Tension": {
                    serialCommDao.writeCommand("T");
                    break;
                }
                case "Compression": {
                    serialCommDao.writeCommand("R");
                    break;
                }
                default:
                    System.out.println("Wrong Test input\n");
                    break;
            }
            switch (forceString_Current) {
                case "100Kg": {
                    serialCommDao.writeCommand("E");
                    break;
                }
                case "500Kg": {
                    serialCommDao.writeCommand("F");
                    break;
                }
                default:
                    System.out.println("Wrong Force input\n");
                    break;
            }
            switch (speedString_Current) {
                case "10 mm/min": {
                    serialCommDao.writeCommand("1");
                    break;
                }
                case "20 mm/min": {
                    serialCommDao.writeCommand("2");
                    break;
                }
                case "50 mm/min": {
                    serialCommDao.writeCommand("3");
                    break;
                }
                case "100 mm/min": {
                    serialCommDao.writeCommand("4");
                    break;
                }
                default:
                    System.out.println("Wrong Speed input\n");
                    break;
            }

            // Write selections to serialCommDao
            serialCommDao.setFileLocation(fileLocation_Current);
            serialCommDao.setFileName(fileNameField.getText());
            serialCommDao.setSpeedString_Current(speedString_Current);
            serialCommDao.setTestString_Current(testString_Current);
            serialCommDao.setForceString_Current(forceString_Current);

            //Start test
            serialCommDao.writeCommand(ARDUINO_START_TEST);
        }
        else{
            resultsLabel.setText("No port available!");
        }

    }

//    // TODO: Make this work
//    // TODO: lett test aborted info from arduino
//    public void testConducted(){
//        while(!testFinished){
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        if(!cancelled) {
//            startButton.setText("FINISHED!");
//            resultsLabel.setText("Test Finished Successfully! Application will close automatically.");
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            System.exit(0);
//        }
//        else{
//            resultsLabel.setText("Test was cancelled! Restart the application and try again.");
//        }
//    }

    private void createUIComponents() {
        String[] testStrings = {"Tension", "Compression"};
        String[] forceStrings = {"500Kg", "100Kg"};
        String[] speedStrings = {"100 mm/min", "20 mm/min", "50 mm/min", "10 mm/min"};
        String[] commStrings = {"No port available"};

        serialCommDao = new SerialComManagerDaoImpl();
        if (serialCommDao.getAvailablePorts().length > 0) {
            commStrings = serialCommDao.getAvailablePorts();
        }

        testComboBox = new JComboBox<>(testStrings);
        forceComboBox = new JComboBox<>(forceStrings);
        speedComboBox = new JComboBox<>(speedStrings);
        commComboBox = new JComboBox<>(commStrings);
        testString_Current = testStrings[0];
        forceString_Current = forceStrings[0];
        speedString_Current = speedStrings[0];
        commString_Current = commStrings[0];
        fileNameField = new JTextField(20);
    }
}






