import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Rens on 2-6-2016.
 */

public class GUI {

    private JButton fileLocationButton;
    private JButton gripUpButton;
    private JButton gripDownButton;
    private JButton startButton;

    private boolean startButtonStop = false;

    public static String selectedForce;
    public static String selectedSpeed;

    private String[] forceStrings = {"100Kg", "500Kg"};
    private String[] speedStrings = {"10 mm/min", "20 mm/min", "30 mm/min", "40 mm/min"};

    JComboBox<String> forceComboBox = new JComboBox<>(forceStrings);
    JComboBox<String> speedComboBox = new JComboBox<>(speedStrings);
    private JPanel LettxJpanel;

    public GUI() {
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
                // TODO: Write("A");
            }
        });
        gripUpButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                // TODO: Write("H");
            }
        });

        // Down
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                // TODO: Write("B");
            }
        });
        gripDownButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                super.mouseReleased(mouseEvent);
                // TODO: Write("G");
            }
        });

        // Start
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                if (startButtonStop) {
                    Main.stopNow = true;
                    startButton.setText("Stopped!");
                }
                else{
                    Main.start();
                    startButtonStop = true;
                    startButton.setText("Stop");
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("GUI");
        frame.setContentPane(new GUI().LettxJpanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}





