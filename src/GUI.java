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
import java.util.Vector;


/**
 * Created by Rens Doornbusch on 2-6-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 * - Extends the functionality of the LETT with compression test *
 */

public class GUI extends JPanel implements Network_iface {

    //String elongationPrint;
    //Double pwmSignal = null;
    //String force = null;
    //Double time = null;
    //Float velocity = null;
    private static String elongation;

    private static Network network;

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

        network = new Network(0, new GUI(), 1);

        // initializing reader from command line
        int i, inp_num = 0;
        String input;
        // BufferedReader in_stream = new BufferedReader(new InputStreamReader(System.in));
        // BufferedReader in_stream = new BufferedReader(new InputStreamReader(Network.inputStream));

        // getting a list of the available serial ports
        Vector<String> ports = network.getPortList();

        boolean valid_answer = false;
        if(ports.size()!=1) {
            frame.setVisible(false);
            createCOMPopUp();
            // choosing the port to connect to
            if (ports.size() > 0) {
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
            for (i = 0; i < ports.size(); ++i) {
                log.append("    " + Integer.toString(i + 1) + ":  " + ports.elementAt(i) + "\n");
            }
            while (!valid_answer) {
                log.append("\n Please, enter the number in front of the port name to choose.");
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

                    log.append("Opening: " + file.getName() + ". \n");
                } else {
                    log.append("Open command cancelled by user.\n");
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
                    int temp[] = {'C'};
                    network.writeSerial(1, temp);
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
                    case "20 mm/min": {
                        int temp[] = {'2'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    case "50 mm/min": {
                        int temp[] = {'3'};
                        network.writeSerial(1, temp);
                        break;
                    }
                    case "100 mm/min": {
                        int temp[] = {'4'};
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
        frame2.dispose();
        frame.setVisible(true);
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


    // Network_iface Methods
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

//    public class SerialReader implements Runnable {
//        InputStream in;
//
//        SerialReader(InputStream in) {
//            this.in = in;
//        }
//
//        public void run() {
//            byte[] buffer = new byte[1024];
//            int len = -1, i, temp;
//            try {
//                while (!end) {
//                    if ((in.available()) > 0) {
//                        if ((len = this.in.read(buffer)) > -1) {
//                            for (i = 0; i < len; i++) {
//                                temp = buffer[i];
//                                // adjust from C-Byte to Java-Byte
//                                if (temp < 0)
//                                    temp += 256;
//                                if (temp == divider) {
//                                    if  (numTempBytes > 0) {
//                                        contact.parseInput(id, numTempBytes, tempBytes);
//                                    }
//                                    numTempBytes = 0;
//                                } else {
//                                    tempBytes[numTempBytes] = temp;
//                                    ++numTempBytes;
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (IOException e) {
//                end = true;
//                try {
//                    outputStream.close();
//                    inputStream.close();
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                serialPort.close();
//                connected = false;
//                contact.networkDisconnected(id);
//                contact.writeLog(id, "connection has been interrupted");
//            }
//        }
//    }

}





