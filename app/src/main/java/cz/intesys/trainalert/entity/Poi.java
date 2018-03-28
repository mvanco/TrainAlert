package cz.intesys.trainalert.entity;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

import org.osmdroid.api.IGeoPoint;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.di.CategoryModule;
import cz.intesys.trainalert.repository.DataHelper;

import static cz.intesys.trainalert.entity.CategorySharedPrefs.DISTANCES_PREF_KEY;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.DISTANCE_DEFAULT_VALUE;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.INCLUDE_DISTANCE_PREF_KEY;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.TEXT_AFTER_PREF_KEY;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.TEXT_BEFORE_PREF_KEY;
import static cz.intesys.trainalert.repository.DataHelper.POI_TYPE_DEFUALT;

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

    @Inject
    public CategorySharedPrefs sharedPrefs;

    @Inject
    public Context context;

    private long id;
    private String title;
    private Double latitude;
    private Double longitude;
    private @DataHelper.CategoryId
    int category;

    /**
     * Conversion function
     *
     * @param poiApi
     */
    public Poi(PoiApi poiApi) {
        this(poiApi.getId(), poiApi.getTitle(), poiApi.getLatitude(), poiApi.getLongitude(), poiApi.getType());
    }

    public Poi() {
        this(null, TaConfig.DEFAULT_LOCATION.getLatitude(), TaConfig.DEFAULT_LOCATION.getLongitude(), POI_TYPE_DEFUALT);
    }

    /**
     * Create new default Poi from location.
     */
    public Poi(Location location) {
        this(null, location.getLatitude(), location.getLongitude(), POI_TYPE_DEFUALT);
    }

    public Poi(String title, String latitude, String longitude, @DataHelper.CategoryId int category) {
        this(title, Double.parseDouble(latitude), Double.parseDouble(longitude), category);
    }

    public Poi(String title, Double latitude, Double longitude, @DataHelper.CategoryId int category) {
        this(0, title, latitude, longitude, category);
    }

    public Poi(long id, String title, Double latitude, Double longitude, @DataHelper.CategoryId int category) {
        CategoryModule.getCategoryComponent(category).inject(this);

        this.id = id;

        if (title == null) {
            this.title = context.getString(R.string.poi_default_name);
        } else {
            this.title = title;
        }

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
        id = in.readLong();
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
        dest.writeLong(id);
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

        if (id != poi.id) return false;
        if (category != poi.category) return false;
        if (!title.equals(poi.title)) return false;
        if (!latitude.equals(poi.latitude)) return false;
        return longitude.equals(poi.longitude);
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + title.hashCode();
        result = 31 * result + latitude.hashCode();
        result = 31 * result + longitude.hashCode();
        result = 31 * result + category;
        return result;
    }

    public String getTitle() {
        return title;
    }

    public int getCategory() {
        return category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Alarm> getAlarmList() {
        List<Alarm> alarmList = new ArrayList<>();
        for (String strDistance : new ArrayList<String>(sharedPrefs.getStringSet(DISTANCES_PREF_KEY, DISTANCE_DEFAULT_VALUE))) {
            int distance = Integer.parseInt(strDistance);
            alarmList.add(new Alarm(distance, createMessage(distance), this));
        }

        return alarmList;
    }

    /**
     * Setting of icon for each {@link DataHelper.CategoryId}
     *
     * @return drawable resource of mapped icon
     */
    @DrawableRes
    public int getMarkerDrawable() {
        return DataHelper.getInstance().findCategoryById(category).getIconRes();
    }

    private String createMessage(int distance) {
        String beforeText = sharedPrefs.getString(TEXT_BEFORE_PREF_KEY, "Pozor za ");
        if (includeDistance()) {
            String afterText = sharedPrefs.getString(TEXT_AFTER_PREF_KEY, "m");
            return beforeText + distance + afterText;
        } else {
            return beforeText;
        }
    }

    private boolean includeDistance() {
        return sharedPrefs.getBoolean(INCLUDE_DISTANCE_PREF_KEY, true);
    }
}
