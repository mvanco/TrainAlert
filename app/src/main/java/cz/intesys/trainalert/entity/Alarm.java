package cz.intesys.trainalert.entity;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import cz.intesys.trainalert.di.CategoryModule;
import cz.intesys.trainalert.repository.DataHelper;

import static cz.intesys.trainalert.entity.CategorySharedPrefs.GRAPHICS_PREF_KEY;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.RINGTONE_DEFAULT_VALUE;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.RINGTONE_PREF_KEY;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.VIBRATE_DEFAULT_VALUE;
import static cz.intesys.trainalert.entity.CategorySharedPrefs.VIBRATE_PREF_KEY;

public class Alarm implements Parcelable {
    public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
        @Override
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        @Override
        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    @Inject
    public CategorySharedPrefs sharedPrefs;
    private int distance;

    private String message;
    private Poi poi; // Must have exactly one Poi which is related to

    public Alarm(int distance, String message, Poi poi) {
        CategoryModule.getCategoryComponent(poi.getCategory()).inject(this);

        this.distance = distance;
        this.message = message;
        this.poi = poi;
    }

    protected Alarm(Parcel in) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Alarm alarm = (Alarm) o;

        if (distance != alarm.distance) return false;
        if (!message.equals(alarm.message)) return false;
        return poi.equals(alarm.poi);
    }

    @Override
    public int hashCode() {
        int result = distance;
        result = 31 * result + message.hashCode();
        result = 31 * result + poi.hashCode();
        return result;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getMessage() {
        return message;
    }

    public List<Alarm> toArray() {
        return Collections.singletonList(this);
    }

    public Poi getPoi() {
        return poi;
    }

    public @DataHelper.GraphicsId int getGraphics() {
        String graphics = sharedPrefs.getString(GRAPHICS_PREF_KEY, String.valueOf(sharedPrefs.getGraphicsDefaultValue()));
        return Integer.valueOf(graphics);
    }

    public Uri getRingtone() {
        String ringtone = sharedPrefs.getString(RINGTONE_PREF_KEY, RINGTONE_DEFAULT_VALUE.toString());
        return Uri.parse(ringtone);
    }

    public boolean shouldVibrate() {
        return sharedPrefs.getBoolean(VIBRATE_PREF_KEY, VIBRATE_DEFAULT_VALUE);
    }

    public int getDistance() {
        return distance;
    }
}
