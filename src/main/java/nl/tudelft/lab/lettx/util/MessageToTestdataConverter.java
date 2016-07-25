package nl.tudelft.lab.lettx.util;

import nl.tudelft.lab.lettx.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Convert message from Arduino to usable data.
 * Created by Rens on 25-7-2016.
 */
public class MessageToTestdataConverter {

    /**
     * Split message in single data units.
     * @param message
     * @return seperate data
     */
    public String[] split(StringBuilder message) {
        // remove commands
        int splitPosStart = message.indexOf("I") + 1;
        int start = 0;
        message.delete(0, splitPosStart);

        // split message
        String messageString = message.toString();
        return messageString.split("\n");
    }

    /**
     * Extract test results from split data.
     * @param splitMessage
     * @return test result list
     */
    public List<TestResult> convertTestResults(String[] splitMessage) {
        List<TestResult> testResultList = new ArrayList<TestResult>();
        int splitMessageSize = splitMessage.length;
        for (int i = 1; i < splitMessageSize - 1; i = i + 3) {
            TestResult testResult = new TestResult();
            testResult.setElongation(Float.parseFloat(splitMessage[i]));
            testResult.setForce(Float.parseFloat(splitMessage[i + 1]));
            testResult.setTime(Long.parseLong(splitMessage[i + 2]));
            testResultList.add(testResult);
        }
        return testResultList;
    }

}
