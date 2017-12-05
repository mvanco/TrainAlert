package cz.intesys.trainalert.entity;

import android.content.Context;

import org.osmdroid.api.IGeoPoint;

import java.util.List;

import cz.intesys.trainalert.utility.Utility;

public class POI implements IGeoPoint {
    private POIType POIType;
    private String title;
    private Double latitude;
    private Double longitude;
    private List<Alarm> alarms;
    private int metaIndex;

    public POI(Double latitude, Double longitude, @Utility.POIType int type, Context context) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.POIType = new POIType(type, this, context);
    }

    @Override
    public int getLatitudeE6() {
        return 0; // Deprecated.
    }

    @Override
    public int getLongitudeE6() {
        return 0; // Deprecated.
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public POIType getPOIType() {
        return POIType;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public void addAlarm(Alarm alarm) {
        this.alarms.add(alarm);
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void setAlarms(List<Alarm> alarms) {
        this.alarms = alarms;
    }
}
