package cz.intesys.trainalert.entity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.StringDef;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.repository.DataHelper;

public class CategorySharedPrefs {
    public static final String CATEGORY_KEY = "cz.intesys.trainalert.categorypreferencefragment.category";
    public static final String COLOUR_PREF_KEY = "colour";
    public static final String GRAPHICS_PREF_KEY = "graphics";
    public static final String RINGTONE_PREF_KEY = "ringtone";
    public static final String VOICE_NAVIGATION_PREF_KEY = "voice_navigation";
    public static final String VIBRATE_PREF_KEY = "vibrate";
    public static final String DISTANCES_PREF_KEY = "distances";
    public static final String TEXT_BEFORE_PREF_KEY = "text_before";
    public static final String INCLUDE_DISTANCE_PREF_KEY = "include_distance";
    public static final String TEXT_AFTER_PREF_KEY = "text_after";

    public static final String GRAPHICS_DEFAULT_VALUE = String.valueOf(DataHelper.GRAPHICS_BLUE_CIRCLE);
    public static final Uri RINGTONE_DEFAULT_VALUE = Settings.System.DEFAULT_NOTIFICATION_URI;
    public static final boolean VIBRATE_DEFAULT_VALUE = false;
    public static final HashSet DISTANCE_DEFAULT_VALUE = new HashSet(Arrays.asList("300"));
    public static final boolean INCLUDE_DISTANCE_DEFAULT_VALUE = true;
    public static final boolean VOICE_NAVIGATION_DEFAULT_VALUE = false;
    public static final String TEXT_AFTER_DEFAULT_VALUE = "m";

    public static final String DEFAULT_VALUE = "0";

    private @DataHelper.CategoryId
    int mCategoryId;
    private SharedPreferences mSharedPref;

    @Retention (RetentionPolicy.SOURCE)
    @StringDef({COLOUR_PREF_KEY, GRAPHICS_PREF_KEY, RINGTONE_PREF_KEY, VOICE_NAVIGATION_PREF_KEY, VIBRATE_PREF_KEY, DISTANCES_PREF_KEY, TEXT_BEFORE_PREF_KEY, INCLUDE_DISTANCE_PREF_KEY, TEXT_AFTER_PREF_KEY})
    public @interface CategoryPrefKey {
    }

    public CategorySharedPrefs(int categoryId, SharedPreferences sharedPref) {
        mCategoryId = categoryId;
        mSharedPref = sharedPref;
    }

    public static CategorySharedPrefs newInstance(@DataHelper.CategoryId int id, SharedPreferences sharedPref) {
        return new CategorySharedPrefs(id, sharedPref);
    }

    public static String getPrefKey(String title, int categoryId) {
        return String.format("category_%d_%s", categoryId, title);
    }

    /**
     * Simplify basic call on SharedPreferences instance, finds actual preference key according to category
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public String getString(@CategoryPrefKey String key, String defaultValue) {
        String prefkey = getPrefKey(key, mCategoryId);
        return mSharedPref.getString(prefkey, defaultValue);
    }

    /**
     * Simlify basic call on SharedPreferences instance, finds actual preference key according to category
     *
     * @param key
     * @return
     */
    public Set<String> getStringSet(@CategoryPrefKey String key, Set<String> defaultValue) {
        return mSharedPref.getStringSet(getPrefKey(key, mCategoryId), defaultValue);
    }

    /**
     * Simlify basic call on SharedPreferences instance, finds actual preference key according to category
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean getBoolean(@CategoryPrefKey String key, boolean defaultValue) {
        String prefkey = getPrefKey(key, mCategoryId);
        return mSharedPref.getBoolean(prefkey, defaultValue);
    }

    public @StringRes int getTextBeforeDefaultValue() {
        switch (mCategoryId) {
            case DataHelper.POI_TYPE_CROSSING:
                return R.string.text_before_default_crossing;
            case DataHelper.POI_TYPE_TRAIN_STATION:
                return R.string.text_before_default_trainstation;
            case DataHelper.POI_TYPE_STOP:
                return R.string.text_before_default_stop;
            case DataHelper.POI_TYPE_LIGHTS:
                return R.string.text_before_default_lights;
            case DataHelper.POI_TYPE_BEFORE_LIGHTS:
                return R.string.text_before_default_beforelights;
            case DataHelper.POI_TYPE_SPEED_LIMITATION_20:
                return R.string.text_before_default_speed_limitation_20;
            case DataHelper.POI_TYPE_SPEED_LIMITATION_30:
                return R.string.text_before_default_speed_limitation_30;
            case DataHelper.POI_TYPE_SPEED_LIMITATION_40:
                return R.string.text_before_default_speed_limitation_40;
            case DataHelper.POI_TYPE_SPEED_LIMITATION_50:
                return R.string.text_before_default_speed_limitation_50;
            case DataHelper.POI_TYPE_SPEED_LIMITATION_70:
                return R.string.text_before_default_speed_limitation_70;
            case DataHelper.POI_TYPE_STOP_AZD:
                return R.string.text_before_default_stop;
            default:
                return R.string.text_before_default;
        }
    }

    public int getGraphicsDefaultValue() {
        switch (mCategoryId) {
            case DataHelper.POI_TYPE_CROSSING:
                return DataHelper.GRAPHICS_YELLOW_GREY_SQARE;

            case DataHelper.POI_TYPE_TRAIN_STATION:
            case DataHelper.POI_TYPE_STOP:
                return DataHelper.GRAPHICS_BLUE_CIRCLE;

            case DataHelper.POI_TYPE_LIGHTS:
            case DataHelper.POI_TYPE_BEFORE_LIGHTS:
                return DataHelper.GRAPHICS_BLUE_RING;

            case DataHelper.POI_TYPE_SPEED_LIMITATION_20:
            case DataHelper.POI_TYPE_SPEED_LIMITATION_30:
            case DataHelper.POI_TYPE_SPEED_LIMITATION_40:
            case DataHelper.POI_TYPE_SPEED_LIMITATION_50:
            case DataHelper.POI_TYPE_SPEED_LIMITATION_70:
                return DataHelper.GRAPHICS_RED_RING;

            case DataHelper.POI_TYPE_STOP_AZD:
                return DataHelper.GRAPHICS_BLUE_CIRCLE;

            default:
                return R.string.text_before_default;
        }
    }
}
