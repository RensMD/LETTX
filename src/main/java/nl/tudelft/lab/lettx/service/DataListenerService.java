package nl.tudelft.lab.lettx.service;

import com.embeddedunveiled.serial.ISerialComDataListener;
import com.embeddedunveiled.serial.SerialComDataEvent;
import nl.tudelft.lab.lettx.domain.LettTestData;
import nl.tudelft.lab.lettx.domain.TestResult;
import nl.tudelft.lab.lettx.util.MessageToTestDataConverter;

import java.io.PrintWriter;
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
    private static final String LETT_TEST_CANCELED = "C";
    private boolean isTestEndReceived = false;
    private boolean isTestAborted = false;
    private boolean isTestCanceled = false;

    private StringBuilder message = new StringBuilder();
    private LettTestData generatedData = new LettTestData();
    private LettTestData selectedData;

    @Override
    public void onNewSerialDataAvailable(SerialComDataEvent dataEvent) {
        String receivedString = extractMessage(dataEvent);
        message.append(receivedString);
        isTestAborted = message.indexOf(LETT_TEST_ABORTED) > -1;
        isTestEndReceived = message.indexOf(LETT_TEST_END) > -1;
        isTestCanceled = message.indexOf(LETT_TEST_CANCELED) > -1;
        if (isTestEndReceived || isTestAborted) {
            MessageToTestDataConverter converter = new MessageToTestDataConverter();

            String[] splitMessage = converter.split(message);
            generatedData.setLettNumber(splitMessage[0]);
            List<TestResult> testResultList = converter.convertTestResults(splitMessage);

            selectedData.setTestResults(testResultList);
            logCommand(dataEvent, receivedString);
            updateReportData();
            createTestReport();
            logTestEndStatus();
            resetTest();
        } else {
            logCommand(dataEvent, receivedString);
        }
    }

    private void updateReportData() {
        selectedData.setLettNumber(generatedData.getLettNumber());
        selectedData.setFileName(generatedData.getFileName());
    }

    /*
    * Log status at the end of the test to console.
    */
    private void logTestEndStatus() {
        if(isTestEndReceived) {
            System.out.println("Test completed successfully.");
        } else if(isTestCanceled) {
            System.out.println("Test cancelled by user.");
        } else if(isTestAborted) {
            System.out.println("Test aborted by arduino.");
        }
    }

    /*
    * Log the received command to the console.
    */
    private void logCommand(SerialComDataEvent dataEvent, String receivedString) {
        System.out.println("Message from Arduino: " + dataEvent.getDataBytesLength() + " byte(s): " + receivedString);
    }

    /*
    * Reset message and data.
    */
    private void resetTest() {
        message = new StringBuilder();
        generatedData = new LettTestData();
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
        PrintWriter printWriter = null;
        reportService.createReport(selectedData, printWriter);
    }

    /**
     * Set testData values for report.
     *
     * @param testData
     */
    public void setLettTestData(LettTestData testData) {
        selectedData = new LettTestData();
        selectedData.setFileLocation(testData.getFileLocation());
        selectedData.setFileName(testData.getFileName());
        selectedData.setType(testData.getType());
        selectedData.setForce(testData.getForce());
        selectedData.setSpeed(testData.getSpeed());
    }
}
