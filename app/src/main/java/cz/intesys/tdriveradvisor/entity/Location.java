package cz.intesys.tdriveradvisor.entity;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.sql.Time;

public class Location implements IGeoPoint {

    private Double latitude;
    private Double longitude;
    private int metaIndex; // TODO: remove this when not simulated
    private Time time;

    public Location(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public int getLatitudeE6() {
        return 0; // Deprecated.
    }

    @Override
    public int getLongitudeE6() {
        return 0; // Deprecated.
    }

    @Override
    public double getLatitude() {
        return latitude;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public GeoPoint toGeoPoint() {
        GeoPoint gp = new GeoPoint(latitude, longitude);
        return gp;
    }
}
