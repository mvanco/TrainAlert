package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

import java.util.Date;

/**
 * Exposed field names must correspond to server response JSON
 */
public class LocationApi {
    @Expose
    private long id;

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    @Expose
    private Date time;

    @Expose
    private boolean interpolated;

    @Expose
    private double speed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isInterpolated() {
        return interpolated;
    }

    public void setInterpolated(boolean interpolated) {
        this.interpolated = interpolated;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }
}
