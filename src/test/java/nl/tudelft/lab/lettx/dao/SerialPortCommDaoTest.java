package nl.tudelft.lab.lettx.dao;

import jssc.SerialPort;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * Created by Rens Doornbusch on 7-7-2016.
 */
public class SerialPortCommDaoTest extends SerialPortCommDao {

    @Mock
    SerialPort serialPort;

    @InjectMocks
    SerialPortCommDao serialCommDao;


    @Before
    public void before() {
        serialCommDao = new SerialPortCommDao();
    }

    @Test
    public void writeCommand() throws Exception {
//        Mockito.when(new SerialPort("COM3")).thenCallRealMethod();
//        serialCommDao.setPortNumber("COM3");
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