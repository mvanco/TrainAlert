package cz.intesys.trainalert.entity.realm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

import cz.intesys.trainalert.entity.CategorySharedPrefs;
import cz.intesys.trainalert.repository.DataHelper;
import io.realm.RealmList;
import io.realm.RealmObject;


/**
 * Represent profile
 */
public class Profile extends RealmObject {
    private String name;
    private RealmList<CategorySetting> data = new RealmList<>();

    public Profile() {
    }

    public Profile(String name) {
        this.name = name;
    }

    public static Profile createFromPrefences(Context context) {
        Profile profile = new Profile();
        profile.fill(context);
        return profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<CategorySetting> getData() {
        return data;
    }

    public void setData(RealmList<CategorySetting> data) {
        this.data = data;
    }

    public void saveToPreferences(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();

        for (int categoryId = DataHelper.POI_TYPE_CROSSING; categoryId <= DataHelper.POI_TYPE_SPEED_LIMITATION_70; categoryId++) {
            editor.putString(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.GRAPHICS_PREF_KEY, categoryId), String.valueOf(data.get(categoryId).getGraphics()));
            editor.putBoolean(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.VOICE_NAVIGATION_PREF_KEY, categoryId), data.get(categoryId).isSoundNotification());
            editor.putString(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.RINGTONE_PREF_KEY, categoryId), data.get(categoryId).getRingtone());
            editor.putString(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.TEXT_BEFORE_PREF_KEY, categoryId), data.get(categoryId).getTextBefore());
            editor.putBoolean(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.INCLUDE_DISTANCE_PREF_KEY, categoryId), data.get(categoryId).isIncludeDistance());
            editor.putString(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.TEXT_AFTER_PREF_KEY, categoryId), data.get(categoryId).getTextAfter());
        }

        editor.commit();
    }

    private void fill(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        for (int categoryId = DataHelper.POI_TYPE_CROSSING; categoryId <= DataHelper.POI_TYPE_SPEED_LIMITATION_70; categoryId++) {
            CategorySharedPrefs pref = CategorySharedPrefs.newInstance(categoryId, sharedPref);
            CategorySetting cSett = new CategorySetting();
            cSett.setCategoryId(categoryId);

            cSett.setGraphics(Integer.valueOf(pref.getString(CategorySharedPrefs.GRAPHICS_PREF_KEY, String.valueOf(pref.getGraphicsDefaultValue()))));
            cSett.setSoundNotification(pref.getBoolean(CategorySharedPrefs.VOICE_NAVIGATION_PREF_KEY, CategorySharedPrefs.VOICE_NAVIGATION_DEFAULT_VALUE));
            cSett.setRingtone(pref.getString(CategorySharedPrefs.RINGTONE_PREF_KEY, CategorySharedPrefs.RINGTONE_DEFAULT_VALUE.toString()));
//            cSett.setDistances(new ArrayList<String>());
//            pref.getStringSet(CategorySharedPrefs.DISTANCES_PREF_KEY, CategorySharedPrefs.DISTANCE_DEFAULT_VALUE);
            cSett.setTextBefore(pref.getString(CategorySharedPrefs.TEXT_BEFORE_PREF_KEY, context.getString(pref.getTextBeforeDefaultValue()) + " "));
            cSett.setIncludeDistance(pref.getBoolean(CategorySharedPrefs.INCLUDE_DISTANCE_PREF_KEY, CategorySharedPrefs.INCLUDE_DISTANCE_DEFAULT_VALUE));
            cSett.setTextAfter(pref.getString(CategorySharedPrefs.TEXT_AFTER_PREF_KEY, CategorySharedPrefs.TEXT_AFTER_DEFAULT_VALUE));
            data.add(cSett);
        }
    }
}
