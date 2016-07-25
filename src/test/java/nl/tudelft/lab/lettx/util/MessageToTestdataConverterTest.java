package nl.tudelft.lab.lettx.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Rens Doornbusch on 25-7-2016.
 */
public class MessageToTestdataConverterTest {
    // test messages
    public static final String NORMAL_LETT_TEST_MESSAGE = "TF4I19\n0.00\n0\n0\n3.10\n10.20\n1\n6.20\n20.40\n2\na\n";
    private static final String EXTRA_END_CHARS_LETT_TEST_MESSAGE = "TF4I19\n0.00\n0\n0\n3.10\n10.20\n1\n6.20\n20.40\n2\na\n24.00\n12.34\n3\n";

    MessageToTestdataConverter converter;

    @Before
    public void before() {
        converter = new MessageToTestdataConverter();
    }

    @Test
    public void splitTestExtractLettNumber() {
        String[] splitMessage = converter.split(createNormalMessage());
        Assert.assertEquals("19", splitMessage[0]);
    }

    @Test
    public void splitTestExtractFirstData() {
        String[] splitMessage = converter.split(createNormalMessage());
        Assert.assertEquals("0.00", splitMessage[1]);
    }

    @Test
    public void splitTestExtraCharactersAfterEnd() {
        String[] splitMessage = converter.split(createExtraEndCharactersMessage());
        int lastDataPos = splitMessage.length - 1;
        Assert.assertEquals("2", splitMessage[lastDataPos]);
    }

    /**
     * Message with characters after test end character "a"
     *
     * @return message
     */
    private StringBuilder createExtraEndCharactersMessage() {
        return createMessage(EXTRA_END_CHARS_LETT_TEST_MESSAGE);
    }

    /**
     * Normal message from Arduino Lett Test
     *
     * @return message
     */
    private StringBuilder createNormalMessage() {
        return createMessage(NORMAL_LETT_TEST_MESSAGE);
    }

    /**
     * Create test message.
     *
     * @param arduinoData
     * @return message
     */
    private StringBuilder createMessage(String arduinoData) {
        StringBuilder message = new StringBuilder();
        return message.append(arduinoData);
    }

}
