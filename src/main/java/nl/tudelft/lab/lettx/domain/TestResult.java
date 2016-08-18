package nl.tudelft.lab.lettx.domain;

/**
 * Lettx application
 /**
 * Created by Rens Doornbusch on 6-7-2016. *
 * Code inspired by the "LETT" project Visual Basic code of Pieter Welling *
 * - Created to enable cross-platform(X) usage of application for LETT desktop tests *
 *
 * Domain class holds Lett test result data
 */

public class TestResult {

    private float elongation;
    private float force;
    private double time;

    public float getElongation() {
        return elongation;
    }

    public void setElongation(float elongation) {
        this.elongation = elongation;
    }

    public float getForce() {
        return force;
    }

    public void setForce(float force) {
        this.force = force;
    }

    public double getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time/1000.0;
    }
}
