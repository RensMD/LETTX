import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import java.util.Objects;
import java.util.Vector;

/**
 * Created by Rens Doornbusch on 2-6-2016. *
 */

public class GUI extends JPanel implements Network_iface {

    //String com = null;
    // String filename = null;
    //String elongationPrint;
    //String force = null;
    //Double time = null;
    //Double pwmsignal = null;
    //Float velocity = null;

    private static File fileLocation = null;

    static private final String newline = "\n";
    private static int speed = 19200;
    private static Network network;

    private JPanel LettxJpanel;

    private JButton fileLocationButton;
    private JButton gripUpButton;
    private JButton gripDownButton;
    private JButton startButton;

    private JButton openButton, saveButton;
    private JTextArea log;
    private JFileChooser fc;

    private boolean startButtonStop = false;
    //private static Boolean resend_active = false;
    private static Boolean stopNow = false;
    private static String elongation; //TODO: VBFixedString

    private static String testString_Current;
    private static String forceString_Current;
    private static String speedString_Current;
    JComboBox<String> forceComboBox;
    JComboBox<String> speedComboBox;
    JComboBox<String> testComboBox;
    private JTextField fileName;


    public static void main(String[] args) {

        network = new Network(0, new GUI(), 255);

        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
        });

        // reading the speed if
        if (args.length > 0) {
            try {
                speed = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("the speed must be an integer\n");
                System.exit(1);
            }
        }

        // initializing reader from command line
        int i, inp_num = 0;
        String input;
        BufferedReader in_stream = new BufferedReader(new InputStreamReader(System.in));

        // getting a list of the available serial ports
        Vector<String> ports = network.getPortList();

        // choosing the port to connect to
        System.out.println();
        if (ports.size() > 0) {
            System.out.println("the following serial ports have been detected:");
        } else {
            System.out.println("sorry, no serial ports were found on your computer\n");
            System.exit(0);
        }
        for (i = 0; i < ports.size(); ++i) {
            System.out.println("    " + Integer.toString(i + 1) + ":  " + ports.elementAt(i));
        }

        boolean valid_answer = false;
        if(ports.size()!=1) {
            while (!valid_answer) {
                System.out.println("enter the id (1,2,...) of the connection to connect to: ");
                try {
                    input = in_stream.readLine();
                    inp_num = Integer.parseInt(input);
                    if ((inp_num < 1) || (inp_num >= ports.size() + 1))
                        System.out.println("your input is not valid");
                    else
                        valid_answer = true;
                } catch (NumberFormatException ex) {
                    System.out.println("please enter a correct number");
                } catch (IOException e) {
                    System.out.println("there was an input error\n");
                    System.exit(1);
                }
            }
        }
        else{
            inp_num=1;
        }

        // connecting to the selected port
        if (network.connect(ports.elementAt(inp_num - 1), speed)) {
            System.out.println();
        } else {
            System.out.println("sorry, there was an error connecting\n");
            System.exit(1);
        }

//        // asking whether user wants to mirror traffic
//        System.out.println("do you want this tool to send back all the received messages?");
//        valid_answer = false;
//        while (!valid_answer) {
//            System.out.println("'y' for yes or 'n' for no: ");
//            try {
//                input = in_stream.readLine();
//                switch (input) {
//                    case "y":
//                        resend_active = true;
//                        valid_answer = true;
//                        break;
//                    case "n":
//                        valid_answer = true;
//                        break;
//                    case "q":
//                        System.out.println("example terminated\n");
//                        System.exit(0);
//                }
//            } catch (IOException e) {
//                System.out.println("there was an input error\n");
//                System.exit(1);
//            }
//        }

        // reading in numbers (bytes) to be sent over the serial port

        System.out.println("type 'q' to end the example");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
//            catch (InterruptedException e1) {
//            }
            System.out.println("\nenter a number between 0 and 254 to be sent ('q' to exit): ");
            try {
                input = in_stream.readLine();
                if (input.equals("q")) {
                    System.out.println("example terminated\n");
                    network.disconnect();
                    System.exit(0);
                }
                inp_num = Integer.parseInt(input);
                if ((inp_num > 255) || (inp_num < 0)) {
                    System.out.println("the number you entered is not valid");
                } else {
                    int temp[] = { inp_num };
                    network.writeSerial(1, temp);
                    System.out.println("sent " + inp_num + " over the serial port");
                }
            } catch (NumberFormatException ex) {
                System.out.println("please enter a correct number");
            } catch (IOException e) {
                System.out.println("there was an input error");
            }
        }
    }

    private GUI() {

        super(new BorderLayout());

        //Create the log first, because the action listeners
        //need to refer to it.
        log = new JTextArea(5,20);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
//        JScrollPane logScrollPane = new JScrollPane(log);

        //Create a file chooser
        fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

//        //Create the open button.
//        openButton = new JButton("Open a File...");
//        openButton.addActionListener(this);
//
//        //Create the save button.
//        saveButton = new JButton("Save a File...");
//        saveButton.addActionListener(this);

//        //For layout purposes, put the buttons in a separate panel
//        JPanel buttonPanel = new JPanel(); //use FlowLayout
//        buttonPanel.add(openButton);
//        buttonPanel.add(saveButton);
//
//        //Add the buttons and the log to this panel.
//        add(buttonPanel, BorderLayout.PAGE_START);
//        add(logScrollPane, BorderLayout.CENTER);

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
//        forceComboBox.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent actionEvent) {
//                JComboBox forceComboBox = (JComboBox) actionEvent.getSource();
//                forceString_Current = (String) forceComboBox.getSelectedItem();
//            }
//        });

        // Speed
        speedComboBox.addActionListener(actionEvent -> {
            JComboBox speedComboBox = (JComboBox) actionEvent.getSource();
            speedString_Current = (String) speedComboBox.getSelectedItem();
        });

        // File location
        fileLocationButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int returnVal = fc.showOpenDialog(GUI.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    fileLocation =fc.getSelectedFile();

                    log.append("Opening: " + file.getName() + "." + newline);
                } else {
                    log.append("Open command cancelled by user." + newline);
                }
                log.setCaretPosition(log.getDocument().getLength());
                System.out.println("Current Path:");
                System.out.println(fc.getSelectedFile());
            }
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

    private static void start() {

        if(Objects.equals(fileLocation, "")){
            System.out.println("No File location selected\n");
        }
        else {
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

            //TODO: procedure loop!
            // Check for cancel
            if (stopNow || Objects.equals(elongation, "a")) {
                int temp[] = { 'C' };
                network.writeSerial(1, temp);
                // Stop timer
            } else {
                // Start
                int temp[] = { 'I' };
                network.writeSerial(1, temp);
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

//    public void actionPerformed(ActionEvent e) {
//
//        //Handle open button action.
//        if (e.getSource() == openButton) {
//            int returnVal = fc.showOpenDialog(GUI.this);
//
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                File file = fc.getSelectedFile();
//                fileLocation =fc.getSelectedFile();
//
//                log.append("Opening: " + file.getName() + "." + newline);
//            } else {
//                log.append("Open command cancelled by user." + newline);
//            }
//            log.setCaretPosition(log.getDocument().getLength());
//            System.out.println("Current Path:");
//            System.out.println(fc.getSelectedFile());
//
//            //Handle save button action.
//        } else if (e.getSource() == saveButton) {
//            int returnVal = fc.showSaveDialog(GUI.this);
//            if (returnVal == JFileChooser.APPROVE_OPTION) {
//                File file = fc.getSelectedFile();
//                //This is where a real application would save the file.
//                log.append("Saving: " + file.getName() + "." + newline);
//            } else {
//                log.append("Save command cancelled by user." + newline);
//            }
//            log.setCaretPosition(log.getDocument().getLength());
//        }
//    }

//    /** Returns an ImageIcon, or null if the path was invalid. */
//    private static ImageIcon createImageIcon(String path) {
//        java.net.URL imgURL = FileChooser.class.getResource(path);
//        if (imgURL != null) {
//            return new ImageIcon(imgURL);
//        } else {
//            System.err.println("Couldn't find file: " + path);
//            return null;
//        }
//    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("FileChooserDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new GUI());

        //Display the window.
        frame.pack();
        frame.setVisible(true);
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
    }
}





