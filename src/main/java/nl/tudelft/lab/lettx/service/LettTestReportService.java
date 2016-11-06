package nl.tudelft.lab.lettx.service;

import nl.tudelft.lab.lettx.domain.LettTestData;
import nl.tudelft.lab.lettx.domain.TestResult;
import nl.tudelft.lab.lettx.gui.GUI;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Lettx application
 /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Create Lett test report
 */

class LettTestReportService {
    private int lineNumber = 1;

    public void createReport(LettTestData testData, PrintWriter w) {
        // Create new text File
        File dir = new File(testData.getFileLocation());
        if (!dir.isDirectory()) {
            dir.mkdir();
        }
        File textFile = new File(dir + File.separator + testData.getFileName());
        try {
            textFile.createNewFile();
            System.out.println("Test Report saved in: " + textFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Problem saving Test Report in: " + textFile.getAbsolutePath());
            e.printStackTrace();
        }

        // Write Standard information to file
        try {
            w = new PrintWriter(new FileWriter(textFile));
            w.println("Developed by:\t\tPieter Welling & Rens Doornbusch");
            w.println("\t\t\tTU Delft");
            w.println();
            w.println("Test Name:\t\t" + testData.getFileName());
            w.println("Date & Time:\t\t" + testData.getDateTime());
            w.println("Speed:\t\t\t" + testData.getSpeed());
            w.println("Load Cell:\t\t" + testData.getForce());
            w.println("Test Type:\t\t" + testData.getType());
            w.println("LETT #:\t\t\t" + testData.getLettNumber());
            w.println();
            w.println("Nr\tDistance (mm)\tForce (N)\tTime (s)");

            for (TestResult testResult : testData.getTestResults()) {
                w.println(lineNumber++ + ":\t" + testResult.getElongation() + "\t\t" + testResult.getForce() + "\t\t" + testResult.getTime() + "\t\t");
            }
            w.close();

        } catch (IOException e) {
            System.out.println("Problem writing Test Data to file: " + textFile.getAbsolutePath());
            e.printStackTrace();
        }
        GUI.testFinished= true;
    }
}
