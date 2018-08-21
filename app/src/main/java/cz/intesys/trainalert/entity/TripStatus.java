package cz.intesys.trainalert.entity;

import cz.intesys.trainalert.api.TripStatusApi;

public class TripStatus {
    boolean pressed;  // If "stop button" of next stop is pressed.
    boolean canPass;  // Train can pass the next stop.
    int speedLimit;  // Current speed limit on he railway.
    String atStop;

    public TripStatus(TripStatusApi tripStatusApi) {
        this.pressed = tripStatusApi.isPressed();
        this.canPass = tripStatusApi.isCanPass();
        this.speedLimit = tripStatusApi.getSpeedLimit();
        this.atStop = tripStatusApi.getAtStop();
    }

    public TripStatus(boolean pressed, boolean canPass, int speedLimit, String atStop) {
        this.pressed = pressed;
        this.canPass = canPass;
        this.speedLimit = speedLimit;
        this.atStop = atStop;
    }

    public boolean isPressed() {
        return pressed;
    }

    public boolean isCanPass() {
        return canPass;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public String getAtStop() {
        return atStop;
    }
}
