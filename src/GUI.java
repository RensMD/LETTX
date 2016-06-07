import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.Objects;
import java.util.Vector;

/**
 * Created by Rens on 2-6-2016.
 */

public class GUI implements Network_iface {

    //String com = null;
    //String filelocation = null;
    //String filename = null;
    //String elongationPrint;
    //String force = null;
    //Double time = null;
    //Double pwmsignal = null;
    //Float velocity = null;

    // set the speed of the serial port
    public static int speed = 9600;
    private static Network network;

    private static boolean resend_active = false;

    private static String elongation; //TODO: VBFixedString
    private static Boolean stopNow = false;

    private JButton fileLocationButton;
    private JButton gripUpButton;
    private JButton gripDownButton;
    private JButton startButton;

    private boolean startButtonStop = false;

    static String selectedForce;
    static String selectedSpeed;

    private String[] forceStrings = {"100Kg", "500Kg"};
    private String[] speedStrings = {"10 mm/min", "50 mm/min", "100 mm/min"};

    JComboBox<String> forceComboBox = new JComboBox<>(forceStrings);
    JComboBox<String> speedComboBox = new JComboBox<>(speedStrings);
    private JPanel LettxJpanel;

    public static void main(String[] args) {
        network = new Network(0, new GUI(), 255);

        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

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
        BufferedReader in_stream = new BufferedReader(new InputStreamReader(
                System.in));

        // getting a list of the available serial ports
        Vector<String> ports = network.getPortList();

        // choosing the port to connect to
        System.out.println();
        if (ports.size() > 0) {
            System.out
                    .println("the following serial ports have been detected:");
        } else {
            System.out
                    .println("sorry, no serial ports were found on your computer\n");
            System.exit(0);
        }
        for (i = 0; i < ports.size(); ++i) {
            System.out.println("    " + Integer.toString(i + 1) + ":  "
                    + ports.elementAt(i));
        }
        boolean valid_answer = false;
        while (!valid_answer) {
            System.out
                    .println("enter the id (1,2,...) of the connection to connect to: ");
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

        // connecting to the selected port
        if (network.connect(ports.elementAt(inp_num - 1), speed)) {
            System.out.println();
        } else {
            System.out.println("sorry, there was an error connecting\n");
            System.exit(1);
        }

        // asking whether user wants to mirror traffic
        System.out
                .println("do you want this tool to send back all the received messages?");
        valid_answer = false;
        while (!valid_answer) {
            System.out.println("'y' for yes or 'n' for no: ");
            try {
                input = in_stream.readLine();
                if (input.equals("y")) {
                    resend_active = true;
                    valid_answer = true;
                } else if (input.equals("n")) {
                    valid_answer = true;
                } else if (input.equals("q")) {
                    System.out.println("example terminated\n");
                    System.exit(0);
                }
            } catch (IOException e) {
                System.out.println("there was an input error\n");
                System.exit(1);
            }
        }

        // reading in numbers (bytes) to be sent over the serial port
        System.out.println("type 'q' to end the example");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
            }
            System.out
                    .println("\nenter a number between 0 and 254 to be sent ('q' to exit): ");
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
        // File location
        fileLocationButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                // TODO: fileLocationSelect();
            }
        });

        // Force
        // TODO: action listener right for Combo box?
        forceComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox<String> forceComboBox = (JComboBox<String>) actionEvent.getSource();
                String selectedForce = (String) forceComboBox.getSelectedItem();
            }
        });

        // Speed
        speedComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JComboBox<String> speedComboBox = (JComboBox<String>) actionEvent.getSource();
                String selectedSpeed = (String) speedComboBox.getSelectedItem();
            }
        });

        // Control
        // Up
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                int temp[] = { 5 };
                network.writeSerial(1, temp);
            }
        });
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                int temp[] = { 6 };
                network.writeSerial(1, temp);
            }
        });

        // Down
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                int temp[] = { '5' };
                network.writeSerial(1, temp);
            }
        });
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                int temp[] = { '6' };
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

        //if(filelocation == ""){
        // select file location }


        if (selectedForce.equals("100Kg")){
            //TODO: Write("E");
            return;
        }
        else{
            //TODO: Write("F");
        }

        if (selectedSpeed.equals("10 mm/min")){
            //TODO: Write("1");
            return;
        }
        else if (selectedSpeed.equals("50 mm/min")){
            //TODO: Write("2");
            return;
        }
        else if (selectedSpeed.equals("100 mm/min")){
            //TODO: Write("3");
            return;
        }

        //TODO: Write("I"); to start

        //TODO: procedure loop

        // Check for cancel
        if(stopNow || Objects.equals(elongation, "a")){
            //Write("I");
            // Stop timer
        }
        else{
            //Write File
        }
    }

    public void writeLog(int id, String text) {
        System.out.println("   log:  |" + text + "|");
    }


    public void parseInput(int id, int numBytes, int[] message) {
        if (resend_active) {
            network.writeSerial(numBytes, message);
            System.out.print("received and sent back the following message: ");
        } else {
            System.out.print("received the following message: ");
        }
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
}





