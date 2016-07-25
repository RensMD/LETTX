package nl.tudelft.lab.lettx.domain;

import nl.tudelft.lab.lettx.gui.GUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Rens on 24-7-2016. *
 */

public class LettTestData {
    private String fileLocation;
    private String name;
    private String lettNumber;
    private String speed;
    private String type;
    private String force;
    private String time;
    private String distance;
    String dateTime;

    private List<TestResult> testResults;

    public List<TestResult> getTestResults() {
        return testResults;
    }

    public void setTestResults(List<TestResult> testResults) {
        this.testResults = testResults;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation() {
        this.fileLocation = GUI.fileLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLettNumber() {
        return lettNumber;
    }

    public void setLettNumber(String lettNumber) {
        this.lettNumber = lettNumber;
    }

    public String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getForce() {
        return force;
    }

    public void setForce(String force) {
        this.force = force;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
