package nl.tudelft.lab.lettx.domain;

import nl.tudelft.lab.lettx.gui.GUI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Lettx application
 * /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Domain class holds all Lett test data
 */

public class LettTestData {
    private String fileLocation;
    private String name;
    private String lettNumber;
    private String speed;
    private String type;
    private String force;
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
        // TODO: check if date format switch worked
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
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
}
