package nl.tudelft.lab.lettx.gui;

import nl.tudelft.lab.lettx.dao.SerialPortCommDao;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
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
    private JComboBox<String> commComboBox;
    private String testString_Current;
    private String forceString_Current;
    private String speedString_Current;
    private String commString_Current;
    public JButton startButton;
    private boolean stopButton = false;
    private JTextArea log;
    private boolean closed = false;

    private boolean stopNow = false;
    private boolean cancelled = false;
    private String selectedPort;
    private String LETTNumber;

    SerialPortCommDao serialCommDao;

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

        log = new JTextArea(5,31);
        log.setMargin(new Insets(5,5,5,5));
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
        // serial Port Select
        commComboBox.addActionListener(actionEvent -> {
            JComboBox commComboBox = (JComboBox) actionEvent.getSource();
            commString_Current = (String) commComboBox.getSelectedItem();
        });

        /* Control */
        // Up
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                serialCommDao.writeCommand("A");
            }
        });
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                serialCommDao.writeCommand("H");
            }
        });
        // Down
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                serialCommDao.writeCommand("B");
            }
        });
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                serialCommDao.writeCommand("G");
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
                    if (!commString_Current.equalsIgnoreCase("no comport available")) {
                        serialCommDao.writeCommand("C");
                    }
                }
                else{
                    stopButton = true;
                    start();
                }
            }
        });
    }

    private void start() {
        // TODO - replace setting filename and filelocation with default choice on textfields
        if (!commString_Current.equalsIgnoreCase("no comport available")) {
            startButton.setText("STOP");
            //Check for empty fields
            if (Objects.equals(fileNameField.getText(), "")) {
                fileNameField.setText("test");
            }
            serialCommDao.setFileName(fileNameField.getText());
            if (Objects.equals(fileLocation, "")) {
                fileLocation = "C:";
            }
            serialCommDao.setFileLocation(fileLocation);
            serialCommDao.setPortNumber(commString_Current);

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

//            // Write Standard information to file
            serialCommDao.setSpeedString_Current(speedString_Current);
            serialCommDao.setTestString_Current(testString_Current);
            serialCommDao.setForceString_Current(forceString_Current);
            serialCommDao.createTestLog();

            //Start test
            if (!stopNow) {
                serialCommDao.writeCommand("I");
            }
        }
    }

    private void createUIComponents() {
        serialCommDao = new SerialPortCommDao();
        String[] testStrings = {"Tension", "Compression"};
        String[] forceStrings = {"500Kg", "100Kg"};
        String[] speedStrings = {"100 mm/min", "20 mm/min", "50 mm/min", "10 mm/min"};
        String[] commStrings = {"no comport available"};
        if(serialCommDao.getAvailablePorts().length > 0) {
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





