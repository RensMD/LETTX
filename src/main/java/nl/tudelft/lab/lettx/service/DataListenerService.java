package nl.tudelft.lab.lettx.service;

import com.embeddedunveiled.serial.ISerialComDataListener;
import com.embeddedunveiled.serial.SerialComDataEvent;
import nl.tudelft.lab.lettx.domain.LettTestData;
import nl.tudelft.lab.lettx.domain.TestResult;
import nl.tudelft.lab.lettx.util.MessageToTestdataConverter;

import java.util.List;

/**
 * Created by Rens on 22-7-2016.
 */
public class DataListenerService implements ISerialComDataListener {
    public static final String LETT_TEST_END = "a";
    boolean isDataReceived = false;
    boolean isTestEndReceived = false;
    boolean isTestAborted = false;

    StringBuilder message = new StringBuilder();
    LettTestData data = new LettTestData();

    @Override
    public void onNewSerialDataAvailable(SerialComDataEvent dataEvent) {
        String receivedString = extractMessage(dataEvent);
        message.append(receivedString);
        isTestAborted = message.indexOf("C") > -1;
        isTestEndReceived = message.indexOf(LETT_TEST_END) > -1;
        if (isTestEndReceived) {
            MessageToTestdataConverter converter = new MessageToTestdataConverter();

            String[] splitMessage = converter.split(message);
            data.setLettNumber(splitMessage[0]);
            List<TestResult> testResultList = converter.convertTestResults(splitMessage);

            data.setTestResults(testResultList);
            createTestReport();
            message = new StringBuilder();
        }
        if(isTestAborted) {
            message = new StringBuilder();
            data = new LettTestData();
        }
        System.out.println("Message from Arduino: " + dataEvent.getDataBytesLength() + " byte(s): " + receivedString);
    }

    @Override
    public void onDataListenerError(int errorNum) {
        System.out.println("Error: " + errorNum);
    }

    /**
     * Extract message from data.
     * @param dataEvent
     * @return
     */
    private String extractMessage(SerialComDataEvent dataEvent) {
        int byteCount = dataEvent.getDataBytesLength();
        isDataReceived = byteCount > 0;
        byte[] receivedData = dataEvent.getDataBytes();
        return new String(receivedData);
    }

    /**
     * Create report from test data.
      */
    private void createTestReport() {
        LettTestReportService reportService = new LettTestReportService();
        reportService.createReport(data);
    }

    /**
     * Set testData values for report.
     *
     * @param testData
     */
    public void setLettTestData(LettTestData testData) {
        data = new LettTestData();
        data.setFileLocation();
        data.setName(testData.getName());
        data.setType(testData.getType());
        data.setForce(testData.getForce());
        data.setSpeed(testData.getSpeed());
    }
}
