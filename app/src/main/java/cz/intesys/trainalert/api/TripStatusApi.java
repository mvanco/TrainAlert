package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

public class TripStatusApi {
    @Expose
    boolean pressed; // From original "shouldStop" attribute.

    @Expose
    boolean canPass; // From original "canBeIgnored" attribute.

    @Expose
    int speedLimit; // From original "speedLimit" attribute.

    @Expose
    String atStop;

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isCanPass() {
        return canPass;
    }

    public void setCanPass(boolean canPass) {
        this.canPass = canPass;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public String getAtStop() {
        return atStop;
    }

    public void setAtStop(String atStop) {
        this.atStop = atStop;
    }
}
