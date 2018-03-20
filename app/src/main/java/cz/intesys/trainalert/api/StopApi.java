package cz.intesys.trainalert.api;

import android.support.annotation.StringDef;

import com.google.gson.annotations.Expose;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;

public class StopApi {
    public static final String TYPE_STARTING = "starting_stop";
    public static final String TYPE_REGULAR = "stop";
    public static final String TYPE_ON_DEMAND = "on_request";
    public static final String TYPE_FINAL = "final_stop";
    @Expose
    private String id;
    @Expose
    private String name;
    @Expose
    private Date arrival;
    @Expose
    private int delay;
    @Expose
    private String type;
    @Expose
    private boolean pressed;

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({TYPE_STARTING, TYPE_REGULAR, TYPE_ON_DEMAND, TYPE_FINAL})
    public @interface StopType {
    }

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


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
