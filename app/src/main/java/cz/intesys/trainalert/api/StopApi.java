package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

import java.util.Date;

public class StopApi {
    @Expose
    private String id;

    @Expose
    private String name;

    @Expose
    private Date arrival;

    @Expose
    private int delay;

    @Expose
    private boolean pressed;

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
