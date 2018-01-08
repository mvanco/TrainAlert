package cz.intesys.trainalert.entity;

import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;

import java.util.Set;

import cz.intesys.trainalert.activity.CategoryActivity;
import cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.CategoryPrefKey;
import cz.intesys.trainalert.utility.Utility;

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

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getIconRes() {
        return iconRes;
    }

    public static class CategoryPref {
        private @Utility.CategoryId int mCategoryId;
        private SharedPreferences mSharedPref;

        public CategoryPref(int categoryId, SharedPreferences sharedPref) {
            mCategoryId = categoryId;
            mSharedPref = sharedPref;
        }

        public static CategoryPref newInstance(@Utility.CategoryId int id, SharedPreferences sharedPref) {
            return new CategoryPref(id, sharedPref);
        }

        /**
         * Simlify basic call on SharedPreferences instance, finds actual preference key according to category
         *
         * @param key
         * @param defaultValue
         * @return
         */
        public String getString(@CategoryPrefKey String key, String defaultValue) {
            String prefkey = CategoryActivity.CategoryPreferenceFragment.getPrefKey(key, mCategoryId);
            return mSharedPref.getString(prefkey, defaultValue);
        }

        /**
         * Simlify basic call on SharedPreferences instance, finds actual preference key according to category
         *
         * @param key
         * @return
         */
        public Set<String> getStringSet(@CategoryPrefKey String key, Set<String> defaultValue) {
            return mSharedPref.getStringSet(CategoryActivity.CategoryPreferenceFragment.getPrefKey(key, mCategoryId), defaultValue);
        }

        /**
         * Simlify basic call on SharedPreferences instance, finds actual preference key according to category
         *
         * @param key
         * @param defaultValue
         * @return
         */
        public boolean getBoolean(@CategoryPrefKey String key, boolean defaultValue) {
            String prefkey = CategoryActivity.CategoryPreferenceFragment.getPrefKey(key, mCategoryId);
            return mSharedPref.getBoolean(prefkey, defaultValue);
        }


    }
}
