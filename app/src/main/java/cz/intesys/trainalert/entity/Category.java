package cz.intesys.trainalert.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.activity.CategoryActivity;
import cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.CategoryPrefKey;
import cz.intesys.trainalert.utility.Utility;

import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.DISTANCES_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.GRAPHICS_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.INCLUDE_DISTANCE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.RINGTONE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.TEXT_AFTER_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.TEXT_BEFORE_PREF_KEY;
import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.VIBRATE_PREF_KEY;

public class Category {
    private static int sCount = 0;
    //TODO: add getter from shared preferences
    private @Utility.CategoryId int id;
    private String title;
    private @DrawableRes int iconRes;

    public Category(int id, String title, int iconRes) {
        this.id = id;
        this.title = title;
        this.iconRes = iconRes;
    }

    public static Category createCategory(String title, int iconRes) {
        Category category = new Category(sCount++, title, iconRes);
        return category;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }


    /**
     * Properties obtained from SharedPreferences
     */

    public @DrawableRes
    int getGraphics(Context context) {
        String graphics = CategoryPref.newInstance(id, context).getPref(GRAPHICS_PREF_KEY);
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

    public Uri getRingtone(Context context) {
        String ringtone = CategoryPref.newInstance(id, context).getPref(RINGTONE_PREF_KEY);
        return Uri.parse(ringtone);
    }

    public boolean shouldVibrate(Context context) {
        String vibrate = CategoryPref.newInstance(id, context).getPref(VIBRATE_PREF_KEY);
        return "true".equals(vibrate);
    }

    public boolean shouldIncludeDistance(Context context) {
        String includeDistance = CategoryPref.newInstance(id, context).getPref(INCLUDE_DISTANCE_PREF_KEY);
        return "true".equals(includeDistance);
    }

    public String getAlarmString(Context context) {
        String beforeText = CategoryPref.newInstance(id, context).getPref(TEXT_BEFORE_PREF_KEY);
        if (shouldIncludeDistance(context)) {
            String afterText = CategoryPref.newInstance(id, context).getPref(TEXT_AFTER_PREF_KEY);
            //TODO: create alarm strigng
            return null;
        } else {
            return beforeText;
        }
    }

    public List<Integer> getDistances(Context context) {
        Set<String> distances = CategoryPref.newInstance(id, context).getMultiSelectListPreference(DISTANCES_PREF_KEY);
        List<Integer> result = new ArrayList<>();
        for (String distance : distances) {
            result.add(Integer.parseInt(distance));
        }
        return result;
    }


    public static class CategoryPref {
        private @Utility.CategoryId int mCategoryId;
        private Context mContext;
        private SharedPreferences sharedPref;

        public CategoryPref(int categoryId, Context context) {
            mCategoryId = categoryId;
            mContext = context;
            sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        }

        public static CategoryPref newInstance(@Utility.CategoryId int id, Context context) {
            return new CategoryPref(id, context);
        }

        public String getPref(@CategoryPrefKey String key) {
            return sharedPref.getString(CategoryActivity.CategoryPreferenceFragment.getPrefKey(key, mCategoryId), null);
        }

        public Set<String> getMultiSelectListPreference(@CategoryPrefKey String key) {
            return sharedPref.getStringSet(CategoryActivity.CategoryPreferenceFragment.getPrefKey(key, mCategoryId), null);
        }
    }
}
