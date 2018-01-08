package cz.intesys.trainalert.entity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.annotation.DrawableRes;

import java.util.Collections;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.entity.Category.CategoryPref;
import cz.intesys.trainalert.utility.Utility;

import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.DEFAULT_VALUE;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.GRAPHICS_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.INCLUDE_DISTANCE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.RINGTONE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.TEXT_AFTER_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.TEXT_BEFORE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.VIBRATE_PREF_KEY;

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
    private int distance;
    private String message;
    private Poi poi; // Must have exactly one Poi which is related to

    public Alarm(int distance, String message, Poi poi) {
        this.distance = distance;
        this.message = message;
        this.poi = poi;
    }
    protected Alarm(Parcel in) {

    }

    public static boolean shouldIncludeDistance(@Utility.CategoryId int categoryId, SharedPreferences sharedPref) {
        return CategoryPref.newInstance(categoryId, sharedPref).getBoolean(INCLUDE_DISTANCE_PREF_KEY, true);
    }

    public static String createMessage(int distance, @Utility.CategoryId int categoryId, SharedPreferences sharedPref) {
        CategoryPref categoryPref = CategoryPref.newInstance(categoryId, sharedPref);

        String beforeText = categoryPref.getString(TEXT_BEFORE_PREF_KEY, DEFAULT_VALUE);
        if (shouldIncludeDistance(categoryId, sharedPref)) {
            String afterText = categoryPref.getString(TEXT_AFTER_PREF_KEY, DEFAULT_VALUE);
            return beforeText + distance + afterText;
        } else {
            return beforeText;
        }
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

//    private void disable() {
//        enabled = false;
//    }
//
//    private void enable() {
//        enabled = true;
//    }

//    private boolean isEnabled() {
//        return enabled;
//    }
//
//    private boolean isDisabled() {
//        return !enabled;
//    }

    public List<Alarm> toArray() {
        return Collections.singletonList(this);
    }

    public Poi getPoi() {
        return poi;
    }

    public @DrawableRes
    int getGraphics(SharedPreferences sharedPref) {
        String graphics = CategoryPref.newInstance(getCategoryId(), sharedPref).getString(GRAPHICS_PREF_KEY, DEFAULT_VALUE);
        switch (graphics) {
            case "0":
                return R.drawable.alarm_black_square;
            case "1":
                return R.drawable.alarm_blue_circle;
            case "2":
                return R.drawable.alarm_blue_ring;
            case "3":
                return R.drawable.alarm_blue_square;
            case "4":
                return R.drawable.alarm_grey_square;
            case "5":
                return R.drawable.alarm_red_circle;
            case "6":
                return R.drawable.alarm_red_ring;
            case "7":
                return R.drawable.alarm_red_square;
            case "8":
                return R.drawable.alarm_yellow_grey_square;
            default:
                return R.drawable.alarm_black_square;
        }
    }

    public Uri getRingtone(SharedPreferences sharedPref) {
        String ringtone = CategoryPref.newInstance(getCategoryId(), sharedPref).getString(RINGTONE_PREF_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString());
        return Uri.parse(ringtone);
    }

    public boolean shouldVibrate(SharedPreferences sharedPref) {
        return CategoryPref.newInstance(getCategoryId(), sharedPref).getBoolean(VIBRATE_PREF_KEY, true);
    }

    public boolean shouldIncludeDistance(SharedPreferences sharedPref) {
        String includeDistance = CategoryPref.newInstance(getCategoryId(), sharedPref).getString(INCLUDE_DISTANCE_PREF_KEY, DEFAULT_VALUE);
        return "true".equals(includeDistance);
    }

    public int getDistance() {
        return distance;
    }

    private @Utility.CategoryId
    int getCategoryId() {
        return poi.getCategory();
    }

//    public class FriendMethods {
//        public void disable() {
//            Alarm.this.disable();
//        }
//
//        public void enable() {
//            Alarm.this.enable();
//        }
//
//        public boolean isEnabled() {
//            return Alarm.this.isEnabled();
//        }
//
//        public boolean isDisabled() {
//            return Alarm.this.isDisabled();
//        }
//
//        private FriendMethods() {
//        }
//    }
//
//    /**
//     * @param object in which the friend methods are requested
//     * @return friend methods if object is friend
//     */
//    public FriendMethods getFriendMethods(Object object) {
//        if (object instanceof MainFragmentViewModel) {
//            return new FriendMethods();
//        }
//        else {
//            return null;
//        }
//    }
}
