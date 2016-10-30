package nl.tudelft.lab.lettx.util;

import nl.tudelft.lab.lettx.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Lettx application
 *
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Convert message from Arduino to usable data.
 * Created by Rens on 25-7-2016.
 */
public class MessageToTestDataConverter {

    private static final String LETT_TEST_END = "a";
    private static final String LETT_TEST_ABORT = "b";
    private static final String LETT_TEST_START = "I";
    private static final String LETT_TEST_CANCEL = "C";
    private static final String NEWLINE = "\n";
    private static final int MESSAGE_START = 0;

    /**
     * Remove arduino commands and split message in single data units.
     * @param message
     * @return separate data
     */
    public String[] split(StringBuilder message) {
        // remove commands
        removeStartCommand(message);
        removeCancelCommand(message);
        removeEndOrAbortCommand(message);

        // split message
        return message.toString().split(NEWLINE);
    }

    /*
     * Check existence of END or ABORT command and remove the found command.
     * Test ends with either END or ABORT command.
     * @param message
    */
    private void removeEndOrAbortCommand(StringBuilder message) {
        int splitPosition = 0;
        splitPosition = getSplitPositionEnd(message, splitPosition);
        splitPosition = getSplitPositionAbort(message, splitPosition);

        int messageEnd = message.length();
        message.delete(splitPosition, messageEnd);
    }

    /*
    * Remove the START command.
    * @param message
    */
    private void removeStartCommand(StringBuilder message) {
        int splitPosStart = message.indexOf(LETT_TEST_START) + 1;
        message.delete(MESSAGE_START, splitPosStart);
    }

    /*
    * Remove the CANCEL command.
    * @param message
    */
    private void removeCancelCommand(StringBuilder message) {
        int splitPosCancel = message.indexOf(LETT_TEST_CANCEL);
        if (splitPosCancel > -1) {
            message.deleteCharAt(splitPosCancel);
        }
    }

    /*
    * Find the position of the ABORT command.
    * @param message
    * @param splitPosition
    * @return position of the command
    */
    private int getSplitPositionAbort(StringBuilder message, int splitPosition) {
        int splitPosAbort = message.indexOf(LETT_TEST_ABORT);
        if(splitPosAbort > -1) {
            splitPosition = splitPosAbort;
        }
        return splitPosition;
    }

    /*
    * Find the position of the END command.
    * @param message
    * @param splitPosition
    * @return position of the command
    */
    private int getSplitPositionEnd(StringBuilder message, int splitPosition) {
        int splitPosEnd = message.indexOf(LETT_TEST_END);
        if(splitPosEnd > -1){
            splitPosition = splitPosEnd;
        }
        return splitPosition;
    }

    /**
     * Extract test results from split data.
     * @param splitMessage
     * @return test result list
     */
    public List<TestResult> convertTestResults(String[] splitMessage) {
        List<TestResult> testResultList = new ArrayList<TestResult>();
        int splitMessageSize = splitMessage.length;
        for (int i = 1; i < splitMessageSize; i = i + 3) {
            TestResult testResult = new TestResult();
            testResult.setElongation(Float.parseFloat(splitMessage[i]));
            testResult.setForce(Float.parseFloat(splitMessage[i + 1]));
            testResult.setTime(Long.parseLong(splitMessage[i + 2]));
            testResultList.add(testResult);
        }
        return testResultList;
    }
}
