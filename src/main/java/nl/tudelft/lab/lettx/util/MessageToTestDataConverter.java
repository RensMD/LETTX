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

    // TODO - doorb02 - find out the if LETT_TEST_END must be 'a' or 'C'
/*
    private static final String LETT_TEST_END = "C";
*/
    private static final String LETT_TEST_END = "a";
    private static final String LETT_TEST_START = "I";
    private static final String NEWLINE = "\n";
    private static final int MESSAGE_START = 0;

    /**
     * Split message in single data units.
     * @param message
     * @return separate data
     */
    public String[] split(StringBuilder message) {
        // remove commands
        int splitPosStart = message.indexOf(LETT_TEST_START) + 1;
        message.delete(MESSAGE_START, splitPosStart);
        int splitPosEnd = message.indexOf(LETT_TEST_END);
        int end = message.length();
        message.delete(splitPosEnd, end);

        // split message
        String messageString = message.toString();
        return messageString.split(NEWLINE);
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
