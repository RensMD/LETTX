import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Paths;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Vector;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.InputStream;

/**
 * Created by Rens Doornbusch on 2-6-2016. *
 * Inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * Created to enable cross platform (X) usage of application for LETT Desktop tests *
 * Extends the functionality with compression test *
 */

public class GUI extends JPanel implements Network_iface {

    //String elongationPrint;
    //String force = null;
    //Double time = null;
    //Double pwmSignal = null;
    //Float velocity = null;

    static private final String newline = "\n";
    private static Network network;
    private static java.lang.String idinput;
    private static boolean closed = false;

    private JPanel LettxJpanel;

    private JButton gripUpButton;
    private JButton gripDownButton;
    private JButton startButton;

    private boolean startButtonStop = false;
    private static Boolean stopNow = false;
    private static String elongation; //TODO: VBFixedString

    private static String testString_Current;
    private static String forceString_Current;
    private static String speedString_Current;
    JComboBox<String> forceComboBox;
    JComboBox<String> speedComboBox;
    JComboBox<String> testComboBox;

    private JButton fileLocationButton;
    JTextField fileNameField;
    private static String fileLocation = null;

    private JFileChooser fc;
    private static JTextArea log;
    private static JButton COMButton;
    private static JTextField COMField;

    public static void main(String[] args) {

        log = new JTextArea(5,50);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);

        COMButton = new JButton("Choose Port");
        COMField = new JTextField("");
        COMField.setPreferredSize( new Dimension( 200, 24 ) );
        COMButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                closed = true;
            }
        });


        network = new Network(0, new GUI(), 255);

        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        // Schedule a job for the event dispatch thread:
        // creating and showing this application's GUI.
//        SwingUtilities.invokeLater(() -> {
//            // Turn off metal's use of bold fonts
//            UIManager.put("swing.boldMetal", Boolean.FALSE);
//        });

        // initializing reader from command line
        int i, inp_num = 0;
        String input;
        BufferedReader in_stream = new BufferedReader(new InputStreamReader(System.in));

        // getting a list of the available serial ports
        Vector<String> ports = network.getPortList();


        //TODO: Create arduino choice window
        boolean valid_answer = false;
        if(ports.size()!=1) {
            createCOMPopUp();

            // choosing the port to connect to
            if (ports.size() > 0) {
                System.out.println("Multiple serial ports have been detected:");
                log.append("Multiple serial ports have been detected:\n");
            } else {
                System.out.println("sorry, no serial ports were found on your computer\n");
                log.append("Sorry, no serial ports were found on your computer.\n");
                log.append("Program will exit soon...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
            for (i = 0; i < ports.size(); ++i) {
                log.append("    " + Integer.toString(i + 1) + ":  " + ports.elementAt(i) + "\n");
            }

            // TODO: make dependent on button press
            while (!valid_answer) {
                log.append("Enter the number (1,2,...) of the port to connect to: \n");
                try {
                    input = textFieldInput();
                    inp_num = Integer.parseInt(input);
                    if ((inp_num < 1) || (inp_num >= ports.size() + 1))
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

        // connecting to the selected port
        int speed = 19200;
        if (network.connect(ports.elementAt(inp_num - 1), speed)) {
            System.out.println();
        } else {
            System.out.println("sorry, there was an error connecting\n");
            System.exit(1);
        }

//        //TODO: Get rid of 0-254
//        // reading in numbers (bytes) to be sent over the serial port
//        System.out.println("type 'q' to end the example");
//        while (true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException ignored) {
//            }
//
//            System.out.println("\nenter a number between 0 and 254 to be sent ('q' to exit): ");
//            try {
//                input = in_stream.readLine();
//                if (input.equals("q")) {
//                    System.out.println("example terminated\n");
//                    network.disconnect();
//                    System.exit(0);
//                }
//                inp_num = Integer.parseInt(input);
//                if ((inp_num > 255) || (inp_num < 0)) {
//                    System.out.println("the number you entered is not valid");
//                } else {
//                    int temp[] = { inp_num };
//                    network.writeSerial(1, temp);
//                    System.out.println("sent " + inp_num + " over the serial port");
//                }
//            } catch (NumberFormatException ex) {
//                System.out.println("please enter a correct number");
//            } catch (IOException e) {
//                System.out.println("there was an input error");
//            }
//        }
    }

    private static String textFieldInput() {
        while(!closed){
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        idinput = COMField.getText();
        //TODO: close frame
        return idinput;
    }

    private GUI() {

        super(new BorderLayout());

        JScrollPane logScrollPane = new JScrollPane(log);
        //For layout purposes, put the buttons in a separate panel
        JPanel buttonPanel = new JPanel(); //use FlowLayout
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

                    log.append("Opening: " + file.getName() + "." + newline);
                } else {
                    log.append("Open command cancelled by user." + newline);
                }
                log.setCaretPosition(log.getDocument().getLength());
                System.out.println("Current Path:");
                System.out.println(fileLocation);
            }
        });

        // Selection Boxes
        //Test
        testComboBox.addActionListener(actionEvent -> {
            JComboBox testComboBox = (JComboBox) actionEvent.getSource();
            testString_Current = (String) testComboBox.getSelectedItem();
        });
        // Force
        forceComboBox.addActionListener(actionEvent -> {
            JComboBox forceComboBox = (JComboBox) actionEvent.getSource();
            forceString_Current = (String) forceComboBox.getSelectedItem();
        });
        // Speed
        speedComboBox.addActionListener(actionEvent -> {
            JComboBox speedComboBox = (JComboBox) actionEvent.getSource();
            speedString_Current = (String) speedComboBox.getSelectedItem();
        });

        // Control
        // Up
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                int temp[] = { 'A' };
                network.writeSerial(1, temp);
            }
        });
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                int temp[] = { 'H' };
                network.writeSerial(1, temp);
            }
        });
        // Down
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                int temp[] = { 'B' };
                network.writeSerial(1, temp);
            }
        });
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                int temp[] = { 'G' };
                network.writeSerial(1, temp);
            }
        });
        // Start
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (startButtonStop) {
                    stopNow = true;
                    startButton.setText("Stopped!");
                }
                else{

                    startButtonStop = true;
                    startButton.setText("Stop");
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
                    case "Tensile": {
                        int temp[] = {'T'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    case "Compression": {
                        int temp[] = {'R'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    default:
                        System.out.println("Wrong Test input\n");
                        break;
                }
                switch (forceString_Current) {
                    case "100Kg": {
                        int temp[] = {'E'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    case "500Kg": {
                        int temp[] = {'F'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    default:
                        System.out.println("Wrong Force input\n");
                        break;
                }
                switch (speedString_Current) {
                    case "10 mm/min": {
                        int temp[] = {'1'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    case "50 mm/min": {
                        int temp[] = {'2'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    case "100 mm/min": {
                        int temp[] = {'3'};
                        network.writeSerial(1, temp);
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
                    // TODO: check current LETT number
                    //w.write("LETT #:\t\t" + LETTnumber  + System.getProperty("line.separator"));
                    w.write(System.getProperty("line.separator"));
                    w.write("Time (s)\tDistance (mm)\tForce (N)" + System.getProperty("line.separator"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //Start test
                if (!stopNow || !Objects.equals(elongation, "a")) {
                    int temp[] = {'I'};
                    network.writeSerial(1, temp);
                }

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

    public void writeLog(int id, String text) {
        System.out.println("   log:  |" + text + "|");
    }

    public void parseInput(int id, int numBytes, int[] message) {
        System.out.print("received the following message: ");
        System.out.print(message[0]);
        for (int i = 1; i < numBytes; ++i) {
            System.out.print(", ");
            System.out.print(message[i]);
        }
        System.out.println();
    }

    public void networkDisconnected(int id) {
        System.exit(0);
    }

    private void createUIComponents() {
        String[] testStrings = {"Tensile", "Compression"};
        String[] forceStrings = {"100Kg", "500Kg"};
        String[] speedStrings = {"10 mm/min", "50 mm/min", "100 mm/min"};
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
        JFrame frame = new JFrame("FileChooserDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new GUI());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
}





