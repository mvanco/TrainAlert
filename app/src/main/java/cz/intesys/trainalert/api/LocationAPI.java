package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

public class LocationAPI {
    @Expose
    private long id;

    @Expose
    private double latitude;

    @Expose
    private double longitude;

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
}
