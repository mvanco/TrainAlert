package cz.intesys.trainalert.entity;

import java.util.Date;

import cz.intesys.trainalert.api.StopApi;

public class Stop {
    private int id;
    private String name;
    private Date arrival;
    private int delay;
    private int pressed;

    public Stop(StopApi stopApi) {
        this.id = stopApi.getId();
        this.name = stopApi.getName();
        this.arrival = stopApi.getArrival();
        this.delay = stopApi.getDelay();
        this.pressed = stopApi.getPressed();
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

    public int getPressed() {
        return pressed;
    }

    public void setPressed(int pressed) {
        this.pressed = pressed;
    }
}
