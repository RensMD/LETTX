package nl.tudelft.lab.lettx.dao;

import com.embeddedunveiled.serial.SerialComManager;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Created by Rens Doornbusch on 7-7-2016.
 */
public class SerialPortCommDaoTest {

    @Mock
    SerialComManager serialPort;

    @InjectMocks
    SerialPortCommDao serialCommDao;


    @Before
    public void before() {
        serialCommDao = new SerialPortCommDao() {
            @Override
            public void writeCommand(String command) {

            }

            @Override
            public boolean startCommunication(String portNumber) {
                return false;
            }

            @Override
            public String[] getAvailablePorts() {
                return new String[0];
            }

            @Override
            public void setFileLocation(String fileLocation) {

            }

            @Override
            public void setFileName(String fileName) {

            }

            @Override
            public void setTestString_Current(String testString_Current) {

            }

            @Override
            public void setForceString_Current(String forceString_Current) {

            }

            @Override
            public void setSpeedString_Current(String speedString_Current) {

            }
        };
    }

    @Test
    public void writeCommand() throws Exception {
//        Mockito.when(new SerialPort("COM3")).thenCallRealMethod();
//        serialCommDao.setSerialPortNumber("COM3");
//        serialCommDao.writeCommand("W");
    }

    @Test
    public void serialEvent() throws Exception {

    }

    @Test
    public void createTestLog()  {

    }

//    @Test
//    public void getAvailablePorts() {
//
//    }

}