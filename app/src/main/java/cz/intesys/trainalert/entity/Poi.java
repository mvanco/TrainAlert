package cz.intesys.trainalert.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.osmdroid.api.IGeoPoint;

import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.utility.Utility;

import static cz.intesys.trainalert.utility.Utility.POI_TYPE_DEFUALT;

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
    private PoiConfiguration poiConfiguration;
    private int metaIndex;

    /**
     * Conversion function
     *
     * @param poiApi
     */
    public Poi(PoiApi poiApi) {
        this(poiApi.getTitle(), poiApi.getLatitude(), poiApi.getLongitude(), poiApi.getType());
    }

    /**
     * Create new default Poi from location.
     */
    public Poi(Location location, Context context) {
        this(context.getString(R.string.poi_default_name), location.getLatitude(), location.getLongitude(), POI_TYPE_DEFUALT);
    }

    public Poi(Double latitude, Double longitude, @Utility.CategoryId int type) {
        this(null, latitude, longitude, type);
    }

    public Poi(String title, Double latitude, Double longitude, @Utility.CategoryId int type) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.poiConfiguration = new PoiConfiguration(type, this);
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
        poiConfiguration = in.readParcelable(PoiConfiguration.class.getClassLoader());
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
        dest.writeParcelable(poiConfiguration, flags);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Poi poi = (Poi) o;

        if (!title.equals(poi.title)) return false;
        if (!latitude.equals(poi.latitude)) return false;
        return longitude.equals(poi.longitude);
    }

    @Override
    public int hashCode() {
        int result = title.hashCode();
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        return result;
    }

    public String getTitle() {
        return title;
    }

    public PoiConfiguration getPoiConfiguration() {
        return poiConfiguration;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public void addAlarm(Alarm alarm) {
        this.poiConfiguration.getAlarmList().add(alarm);
    }

    public List<Alarm> getAlarms() {
        return this.poiConfiguration.getAlarmList();
    }

    public void setAlarms(List<Alarm> alarms) {
        this.poiConfiguration.getAlarmList().addAll(alarms);
    }
}
