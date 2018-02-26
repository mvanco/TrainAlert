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
}
