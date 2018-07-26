package cz.intesys.trainalert.entity;

import cz.intesys.trainalert.api.TripStatusApi;

public class TripStatus {
    boolean pressed;  // If "stop button" of next stop is pressed.
    boolean canPass;  // Train can pass the next stop.
    int speedLimit;  // Current speed limit on he railway.

    public TripStatus(TripStatusApi tripStatusApi) {
        this.pressed = tripStatusApi.isPressed();
        this.canPass = tripStatusApi.isCanPass();
        this.speedLimit = tripStatusApi.getSpeedLimit();
    }

    public TripStatus(boolean pressed, boolean canPass, int speedLimit) {
        this.pressed = pressed;
        this.canPass = canPass;
        this.speedLimit = speedLimit;
    }

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
