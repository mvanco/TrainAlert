package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

import org.osmdroid.api.IGeoPoint;

public class PoiApi implements IGeoPoint {
    @Expose
    private long id;

    @Expose
    private String title;

    @Expose
    private double latitude;

    @Expose
    private double longitude;

    @Expose
    private int type;

    @Override
    public int getLatitudeE6() {
        return 0;
    }

    @Override
    public int getLongitudeE6() {
        return 0;
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
