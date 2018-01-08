package cz.intesys.trainalert.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.activity.CategoryActivity;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.entity.Category.CategoryPref;
import cz.intesys.trainalert.utility.Utility;

import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.DISTANCES_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.DISTANCE_DEFAULT_VALUE;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.INCLUDE_DISTANCE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.TEXT_AFTER_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.TEXT_BEFORE_PREF_KEY;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_BRIDGE;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_CROSSING;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_DEFUALT;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION_50;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_SPEED_LIMITATION_70;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_TRAIN_STATION;
import static cz.intesys.trainalert.utility.Utility.POI_TYPE_TURNOUT;

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
    private int metaIndex;
    private @Utility.CategoryId int category;

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

    public Poi(Double latitude, Double longitude, @Utility.CategoryId int category) {
        this(null, latitude, longitude, category);
    }

    public Poi(String title, Double latitude, Double longitude, @Utility.CategoryId int category) {
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.category = category;
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

    public int getCategory() {
        return category;
    }

    public int getMetaIndex() {
        return metaIndex;
    }

    public void setMetaIndex(int metaIndex) {
        this.metaIndex = metaIndex;
    }

    public List<Alarm> getAlarmList(SharedPreferences sharedPref) {
        CategoryPref categoryPref = CategoryPref.newInstance(category, sharedPref);

        List<Alarm> alarmList = new ArrayList<>();
        for (String strDistance : new ArrayList<String>(categoryPref.getStringSet(DISTANCES_PREF_KEY, new HashSet(Arrays.asList(DISTANCE_DEFAULT_VALUE))))) {
            int distance = Integer.parseInt(strDistance);
            alarmList.add(new Alarm(distance, createMessage(distance, sharedPref), this));
        }

        return alarmList;
    }

    /**
     * Setting of icon for each {@link Utility.CategoryId}
     *
     * @return drawable resource of mapped icon
     */
    @DrawableRes
    public int getMarkerDrawable() {
        switch (category) {
            case POI_TYPE_CROSSING:
                return R.drawable.poi_crossing;
            case POI_TYPE_SPEED_LIMITATION_50:
            case POI_TYPE_SPEED_LIMITATION_70:
                return R.drawable.poi_speed_limitation;
            case POI_TYPE_BRIDGE:
                return R.drawable.poi_bridge;
            case POI_TYPE_TRAIN_STATION:
                return R.drawable.poi_train_station;
            case POI_TYPE_TURNOUT:
                return R.drawable.poi_turnout;
            default:
                return R.drawable.poi_crossing;
        }
    }

    private String createMessage(int distance, SharedPreferences sharedPref) {
        CategoryPref categoryPref = CategoryPref.newInstance(category, sharedPref);

        String beforeText = categoryPref.getString(TEXT_BEFORE_PREF_KEY, "Pozor za ");
        if (shouldIncludeDistance(category, sharedPref)) {
            String afterText = categoryPref.getString(TEXT_AFTER_PREF_KEY, "m");
            return beforeText + distance + afterText;
        } else {
            return beforeText;
        }
    }

    private boolean shouldIncludeDistance(@Utility.CategoryId int categoryId, SharedPreferences sharedPref) {
        String prefKey = CategoryActivity.CategoryPreferenceFragment.getPrefKey(INCLUDE_DISTANCE_PREF_KEY, categoryId);
        return sharedPref.getBoolean(prefKey, false);
    }
}
