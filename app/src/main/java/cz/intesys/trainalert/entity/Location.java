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
    private boolean interpolated = false;  // If there are real coordinates or interpolated.
    private int speed;  // Current speed.

    public Location(LocationApi locationApi) {
        this(locationApi.getLatitude(), locationApi.getLongitude(), locationApi.getTime(), locationApi.isInterpolated(), locationApi.getSpeed());
    }

    public Location(Double latitude, Double longitude) {
        this(latitude, longitude, new Date(), false, 0.0);
    }

    public Location(Double latitude, Double longitude, Date time, boolean interpolated, double speed) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
        this.interpolated = interpolated;
        this.speed = (int) Math.round(speed);
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
        if (interpolated != location.interpolated) return false;
        if (speed != location.speed) return false;
        if (latitude != null ? !latitude.equals(location.latitude) : location.latitude != null)
            return false;
        if (longitude != null ? !longitude.equals(location.longitude) : location.longitude != null)
            return false;
        return time != null ? time.equals(location.time) : location.time == null;
    }

    @Override
    public int hashCode() {
        int result = latitude != null ? latitude.hashCode() : 0;
        result = 31 * result + (longitude != null ? longitude.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + metaIndex;
        result = 31 * result + (interpolated ? 1 : 0);
        result = 31 * result + speed;
        return result;
    }

    @Override
    public double getLongitude() {
        return longitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
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

    public boolean isInterpolated() {
        return interpolated;
    }

    public void setInterpolated(boolean interpolated) {
        this.interpolated = interpolated;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
