package cz.intesys.trainalert.entity;

import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.api.IGeoPoint;

import java.util.List;

import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.utility.Utility;

public class Poi implements IGeoPoint, Parcelable {
    public static final Creator<Poi> CREATOR = new Creator<Poi>() {
        @Override
        public Poi createFromParcel(Parcel in) {
            return new Poi(in);
        }

        @Override
        public Poi[] newArray(int size) {
            return new Poi[size];
        }
    };
    private String title;
    private Double latitude;
    private Double longitude;
    private PoiConfiguration PoiConfiguration;
    private int metaIndex;

    /**
     * Conversion function
     *
     * @param poiApi
     */
    public Poi(PoiApi poiApi) {
        this.title = poiApi.getTitle();
        this.latitude = poiApi.getLatitude();
        this.longitude = poiApi.getLongitude();
        this.PoiConfiguration = new PoiConfiguration(poiApi.getType(), this);
    }

    public Poi(String title, Double latitude, Double longitude, @Utility.POIType int type) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.PoiConfiguration = new PoiConfiguration(type, this);
    }

    public Poi(Double latitude, Double longitude, @Utility.POIType int type) {
        this(null, latitude, longitude, type);
    }

    protected Poi(Parcel in) {
        title = in.readString();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        PoiConfiguration = in.readParcelable(PoiConfiguration.class.getClassLoader());
        metaIndex = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
        dest.writeParcelable(PoiConfiguration, flags);
        dest.writeInt(metaIndex);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getTitle() {
        return title;
    }

    public PoiConfiguration getPoiConfiguration() {
        return PoiConfiguration;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public void addAlarm(Alarm alarm) {
        this.PoiConfiguration.getAlarmList().add(alarm);
    }

    public List<Alarm> getAlarms() {
        return this.PoiConfiguration.getAlarmList();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.PoiConfiguration.getAlarmList().addAll(alarms);
    }
}
