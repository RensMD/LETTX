package nl.tudelft.lab.lettx.domain;

import java.io.File;
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
    private String fileName;
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
        if(this.fileLocation == null){
            this.fileLocation = "";
        }
        if (!this.fileLocation.contains("lettxResults")) {
            this.fileLocation += File.separator + "lettxResults";
        }
        return this.fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileName() {
        if(this.fileName == null || this.fileName.contentEquals("")) {
            this.fileName = "test";
        }
        if (!this.fileName.contains(".txt")) {
            this.fileName += ".txt";
        }
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getLettNumber() {
        return lettNumber;
    }

    public void setLettNumber(String lettNumber) {
        this.lettNumber = lettNumber;
    }

    public String getDateTime() {
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
