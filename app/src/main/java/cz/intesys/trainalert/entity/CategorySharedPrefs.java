package cz.intesys.trainalert.entity;

import android.content.SharedPreferences;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Set;

import cz.intesys.trainalert.utility.Utility;

public class CategorySharedPrefs {
    public static final String CATEGORY_KEY = "cz.intesys.trainalert.categorypreferencefragment.category";
    public static final String COLOUR_PREF_KEY = "colour";
    public static final String GRAPHICS_PREF_KEY = "graphics";
    public static final String RINGTONE_PREF_KEY = "ringtone";
    public static final String VIBRATE_PREF_KEY = "vibrate";
    public static final String DISTANCES_PREF_KEY = "distances";
    public static final String TEXT_BEFORE_PREF_KEY = "text_before";
    public static final String INCLUDE_DISTANCE_PREF_KEY = "include_distance";
    public static final String TEXT_AFTER_PREF_KEY = "text_after";
    public static final String DEFAULT_VALUE = "0";
    public static final String DISTANCE_DEFAULT_VALUE = "300";

    private @Utility.CategoryId int mCategoryId;
    private SharedPreferences mSharedPref;

    @Retention (RetentionPolicy.SOURCE)
    @StringDef ( {COLOUR_PREF_KEY, GRAPHICS_PREF_KEY, RINGTONE_PREF_KEY, VIBRATE_PREF_KEY, DISTANCES_PREF_KEY, TEXT_BEFORE_PREF_KEY, INCLUDE_DISTANCE_PREF_KEY, TEXT_AFTER_PREF_KEY})
    public @interface CategoryPrefKey {
    }

    public CategorySharedPrefs(int categoryId, SharedPreferences sharedPref) {
        mCategoryId = categoryId;
        mSharedPref = sharedPref;
    }

    public static CategorySharedPrefs newInstance(@Utility.CategoryId int id, SharedPreferences sharedPref) {
        return new CategorySharedPrefs(id, sharedPref);
    }

    public static String getPrefKey(String title, int categoryId) {
        return String.format("category_%d_%s", categoryId, title);
    }

    /**
     * Simlify basic call on SharedPreferences instance, finds actual preference key according to category
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


}
