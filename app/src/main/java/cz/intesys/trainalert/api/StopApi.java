package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class StopApi {
    @Expose
    private int id;

    @Expose
    private String name;

    @Expose
    private Date arrival;

    @Expose
    private int delay;

    @Expose
    private int pressed;

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
