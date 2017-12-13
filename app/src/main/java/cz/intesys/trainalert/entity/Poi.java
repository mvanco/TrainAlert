package cz.intesys.trainalert.entity;

import android.content.Context;

import org.osmdroid.api.IGeoPoint;

import java.util.List;

import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.utility.Utility;

public class Poi implements IGeoPoint {
    private String title;
    private Double latitude;
    private Double longitude;
    private PoiConfiguration POIConfiguration;
    private int metaIndex;

    public Poi(Double latitude, Double longitude, @Utility.POIType int type, Context context) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.POIConfiguration = new PoiConfiguration(type, this, context);
    }

    /**
     * Conversion function
     *
     * @param poiApi
     */
    public Poi(PoiApi poiApi, Context context) {
        this.title = poiApi.getTitle();
        this.latitude = poiApi.getLatitude();
        this.longitude = poiApi.getLongitude();
        this.POIConfiguration = new PoiConfiguration(poiApi.getType(), this, context);
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

    public PoiConfiguration getPOIConfiguration() {
        return POIConfiguration;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public void addAlarm(Alarm alarm) {
        this.POIConfiguration.getAlarmList().add(alarm);
    }

    public List<Alarm> getAlarms() {
        return this.POIConfiguration.getAlarmList();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.POIConfiguration.getAlarmList().addAll(alarms);
    }
}
