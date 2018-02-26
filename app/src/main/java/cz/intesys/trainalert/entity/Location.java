package cz.intesys.trainalert.entity;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;

import java.util.Date;

import cz.intesys.trainalert.api.LocationApi;

public class Location implements IGeoPoint {

    private Double latitude;
    private Double longitude;
    private Date time;
    private int metaIndex; // TODO: Remove this when not simulated.

    public Location(LocationApi locationApi) {
        this(locationApi.getLatitude(), locationApi.getLongitude(), locationApi.getTime());
    }

    public Location(Double latitude, Double longitude) {
        this(latitude, longitude, new Date());
    }

    public Location(Double latitude, Double longitude, Date time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (metaIndex != location.metaIndex) return false;
        if (!latitude.equals(location.latitude)) return false;
        return longitude.equals(location.longitude);
    }

    @Override
    public int hashCode() {
        int result = latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + metaIndex;
        return result;
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
