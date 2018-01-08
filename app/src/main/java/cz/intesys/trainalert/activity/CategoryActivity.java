package cz.intesys.trainalert.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.annotation.StringDef;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.MenuItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.entity.Category;

import static cz.intesys.trainalert.activity.CategoryActivity.CategoryPreferenceFragment.CATEGORY_KEY;

public class CategoryActivity extends AppCompatPreferenceActivity {

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, CategoryActivity.class);
        return intent;
    }

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return true;
        //return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.pref_headers, target);

        for (Category category : getCategories()) {
            target.add(getHeader(category));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || CategoryPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * TODO: move to better place
     *
     * @return
     */
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(0, "Přechod", R.drawable.poi_crossing));
        categories.add(new Category(1, "Omezení 50", R.drawable.poi_speed_limitation));
        categories.add(new Category(2, "Omezení 70", R.drawable.poi_speed_limitation));
        categories.add(new Category(3, "Stanice", R.drawable.poi_train_station));
        categories.add(new Category(4, "Most", R.drawable.poi_bridge));
        categories.add(new Category(5, "Výhybka", R.drawable.poi_turnout));
        return categories;
    }

    private Header getHeader(Category category) {
        Header header = new Header();
        header.title = category.getTitle();
        header.iconRes = category.getIconRes();
        header.fragment = "cz.intesys.trainalert.activity.CategoryActivity$CategoryPreferenceFragment";
        Bundle bundle = new Bundle();
        bundle.putInt(CATEGORY_KEY, category.getId());
        header.fragmentArguments = bundle;
        return header;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class CategoryPreferenceFragment extends PreferenceFragment {

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

        @Retention (RetentionPolicy.SOURCE)
        @StringDef ( {COLOUR_PREF_KEY, GRAPHICS_PREF_KEY, RINGTONE_PREF_KEY, VIBRATE_PREF_KEY, DISTANCES_PREF_KEY, TEXT_BEFORE_PREF_KEY, INCLUDE_DISTANCE_PREF_KEY, TEXT_AFTER_PREF_KEY})
        public @interface CategoryPrefKey {
        }

        public static String getPrefKey(String title, int categoryId) {
            return String.format("category_%d_%s", categoryId, title);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //addPreferencesFromResource(R.xml.pref_category);
            addPreferences(getArguments().getInt(CATEGORY_KEY));

            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), CategoryActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        private void addPreferences(int categoryId) {

            PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(screen);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Category.CategoryPref pref = Category.CategoryPref.newInstance(categoryId, sharedPref);

            ListPreference colourPref = new ListPreference(getActivity());
            colourPref.setKey(getPrefKey(COLOUR_PREF_KEY, categoryId));
            colourPref.setTitle(R.string.pref_title_colour);
            colourPref.setEntries(R.array.pref_colour_titles);
            colourPref.setEntryValues(R.array.pref_colour_values);
            colourPref.setDefaultValue(pref.getString(COLOUR_PREF_KEY, DEFAULT_VALUE));
            screen.addPreference(colourPref);

            ListPreference graphicsPref = new ListPreference(getActivity());
            graphicsPref.setKey(getPrefKey(GRAPHICS_PREF_KEY, categoryId));
            graphicsPref.setTitle(R.string.pref_title_graphics);
            graphicsPref.setEntries(R.array.pref_graphics_titles);
            graphicsPref.setEntryValues(R.array.pref_graphics_values);
            graphicsPref.setDefaultValue(pref.getString(GRAPHICS_PREF_KEY, DEFAULT_VALUE));
            screen.addPreference(graphicsPref);

            RingtonePreference ringtonePref = new RingtonePreference(getActivity());
            ringtonePref.setKey(getPrefKey(RINGTONE_PREF_KEY, categoryId));
            ringtonePref.setTitle(R.string.pref_title_ringtone);
            ringtonePref.setRingtoneType(RingtoneManager.TYPE_NOTIFICATION);
            ringtonePref.setDefaultValue(pref.getString(RINGTONE_PREF_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString()));
            screen.addPreference(ringtonePref);

            SwitchPreference switchPref = new SwitchPreference(getActivity());
            switchPref.setKey(getPrefKey(VIBRATE_PREF_KEY, categoryId));
            switchPref.setTitle(R.string.pref_title_vibrate);
            switchPref.setDefaultValue(pref.getBoolean(VIBRATE_PREF_KEY, true));
            screen.addPreference(switchPref);

            MultiSelectListPreference distancesPref = new MultiSelectListPreference(getActivity());
            distancesPref.setKey(getPrefKey(DISTANCES_PREF_KEY, categoryId));
            distancesPref.setTitle(R.string.pref_title_distances);
            distancesPref.setEntries(R.array.pref_distances_titles);
            distancesPref.setEntryValues(R.array.pref_distances_values);
            distancesPref.setSummary(R.string.pref_distances_summary);
            distancesPref.setDefaultValue(pref.getStringSet(DISTANCES_PREF_KEY, new HashSet(Arrays.asList(DISTANCE_DEFAULT_VALUE))));
            screen.addPreference(distancesPref);

            EditTextPreference textBeforePref = new EditTextPreference(getActivity());
            textBeforePref.setKey(getPrefKey(TEXT_BEFORE_PREF_KEY, categoryId));
            textBeforePref.setTitle(R.string.pref_title_before_text);
            textBeforePref.setDefaultValue(pref.getString(TEXT_BEFORE_PREF_KEY, "Pozor za "));
            screen.addPreference(textBeforePref);

            SwitchPreference includeDistancePref = new SwitchPreference(getActivity());
            includeDistancePref.setKey(getPrefKey(INCLUDE_DISTANCE_PREF_KEY, categoryId));
            includeDistancePref.setTitle(R.string.pref_title_include_distance);
            includeDistancePref.setDefaultValue(pref.getBoolean(INCLUDE_DISTANCE_PREF_KEY, true));
            screen.addPreference(includeDistancePref);

            EditTextPreference textAfterPref = new EditTextPreference(getActivity());
            textAfterPref.setKey(getPrefKey(TEXT_AFTER_PREF_KEY, categoryId));
            textAfterPref.setTitle(R.string.pref_title_after_text);
            textAfterPref.setDefaultValue(pref.getString(TEXT_AFTER_PREF_KEY, "m"));
            screen.addPreference(textAfterPref);
            textAfterPref.setDependency(getPrefKey(INCLUDE_DISTANCE_PREF_KEY, categoryId));


        }
    }
}
