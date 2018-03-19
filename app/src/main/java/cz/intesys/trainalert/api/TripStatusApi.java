package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

public class TripStatusApi {
    @Expose
    boolean pressed;

    @Expose
    boolean canPass;

    @Expose
    int speedLimit;

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
}
