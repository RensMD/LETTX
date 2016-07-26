package nl.tudelft.lab.lettx.service;

import com.embeddedunveiled.serial.ISerialComDataListener;
import com.embeddedunveiled.serial.SerialComDataEvent;
import nl.tudelft.lab.lettx.domain.LettTestData;
import nl.tudelft.lab.lettx.domain.TestResult;
import nl.tudelft.lab.lettx.util.MessageToTestDataConverter;

import java.util.List;

/**
 * Lettx application
 /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Listen for data on Serial Port
 */

public class DataListenerService implements ISerialComDataListener {
    private static final String LETT_TEST_END = "a";
    private static final String LETT_TEST_ABORTED = "b";
    private boolean isTestEndReceived = false;
    private boolean isTestAborted = false;

    private StringBuilder message = new StringBuilder();
    private LettTestData data = new LettTestData();

    @Override
    public void onNewSerialDataAvailable(SerialComDataEvent dataEvent) {
        String receivedString = extractMessage(dataEvent);
        message.append(receivedString);
        isTestAborted = message.indexOf(LETT_TEST_ABORTED) > -1;
        isTestEndReceived = message.indexOf(LETT_TEST_END) > -1;
        if (isTestEndReceived) {
            MessageToTestDataConverter converter = new MessageToTestDataConverter();

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
