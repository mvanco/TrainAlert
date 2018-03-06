package cz.intesys.trainalert.entity;

import java.util.Date;

import cz.intesys.trainalert.api.StopApi;

public class Stop {
    private int id;
    private String name;
    private Date arrival;
    private int delay;
    private boolean pressed;

    public Stop(StopApi stopApi) {
        this.id = stopApi.getId();
        this.name = stopApi.getName();
        this.arrival = stopApi.getArrival();
        this.delay = stopApi.getDelay();
        this.pressed = stopApi.isPressed();
    }

    public Stop(int id, String name, Date arrival, int delay, boolean pressed) {
        this.id = id;
        this.name = name;
        this.arrival = arrival;
        this.delay = delay;
        this.pressed = pressed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getArrival() {
        return arrival;
    }

    public void setArrival(Date arrival) {
        this.arrival = arrival;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }
}
