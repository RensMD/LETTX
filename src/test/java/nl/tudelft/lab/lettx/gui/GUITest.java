package nl.tudelft.lab.lettx.gui;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Rens Doornbusch on 7-7-2016.
 */
public class GUITest {

    /**
     * Test that GUI class can be started.
     */
    @Test
    public void startGUI() {
        GUI gui = new GUI();
        Assert.assertNotNull(gui);
    }
}
