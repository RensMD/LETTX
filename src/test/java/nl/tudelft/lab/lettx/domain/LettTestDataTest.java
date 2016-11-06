package nl.tudelft.lab.lettx.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Rens on 6-11-2016.
 */
public class LettTestDataTest {


    private LettTestData lettTestData;

    @Before
    public void init() {
        lettTestData = new LettTestData();
    }
   
    @Test
    public void getFileLocationNoInput() throws Exception {
        Assert.assertEquals("\\lettxResults", lettTestData.getFileLocation());
    }

    @Test
    public void getFileLocationWithInput() throws Exception {
        lettTestData.setFileLocation("testFileLocation");
        Assert.assertEquals("testFileLocation\\lettxResults", lettTestData.getFileLocation());
    }

    @Test
    public void getFileNameNoInput() {
        lettTestData.setLettNumber("22");
        Assert.assertTrue(lettTestData.getFileName().matches("test_22_[0-9]{2}/[0-9]{2}/[0-9]{4}_[0-9]{2}:[0-9]{2}:[0-9]{2}.txt"));
    }

    @Test
    public void getFileNameWithInput() {
        lettTestData.setFileName("testFilename");
        Assert.assertEquals("testFilename.txt", lettTestData.getFileName());
    }

    @Test
    public void getDateTime() throws Exception {
        Assert.assertTrue(lettTestData.getDateTime().matches("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}:[0-9]{2}:[0-9]{2}"));
    }

}