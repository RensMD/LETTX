package nl.tudelft.lab.lettx.domain;

/**
 * Created by Rens on 24-7-2016.
 */
public class TestResult {

    float elongation;
    float force;
    long time;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
