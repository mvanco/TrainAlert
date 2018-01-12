package cz.intesys.trainalert.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.HashSet;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.activity.CategoryActivity;
import cz.intesys.trainalert.entity.CategorySharedPrefs;

/**
 * This fragment shows data and sync preferences only. It is used when the
 * activity is showing a two-pane settings UI.
 */
public class CategoryDetailFragment extends PreferenceFragment {

    public CategoryDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //addPreferencesFromResource(R.xml.pref_category);
        addPreferences(getArguments().getInt(CategorySharedPrefs.CATEGORY_KEY));

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
        CategorySharedPrefs pref = CategorySharedPrefs.newInstance(categoryId, sharedPref);

        ListPreference colourPref = new ListPreference(getActivity());
        colourPref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.COLOUR_PREF_KEY, categoryId));
        colourPref.setTitle(R.string.pref_title_colour);
        colourPref.setEntries(R.array.pref_colour_titles);
        colourPref.setEntryValues(R.array.pref_colour_values);
        colourPref.setDefaultValue(pref.getString(CategorySharedPrefs.COLOUR_PREF_KEY, CategorySharedPrefs.DEFAULT_VALUE));
        screen.addPreference(colourPref);

        ListPreference graphicsPref = new ListPreference(getActivity());
        graphicsPref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.GRAPHICS_PREF_KEY, categoryId));
        graphicsPref.setTitle(R.string.pref_title_graphics);
        graphicsPref.setEntries(R.array.pref_graphics_titles);
        graphicsPref.setEntryValues(R.array.pref_graphics_values);
        graphicsPref.setDefaultValue(pref.getString(CategorySharedPrefs.GRAPHICS_PREF_KEY, CategorySharedPrefs.DEFAULT_VALUE));
        screen.addPreference(graphicsPref);

        RingtonePreference ringtonePref = new RingtonePreference(getActivity());
        ringtonePref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.RINGTONE_PREF_KEY, categoryId));
        ringtonePref.setTitle(R.string.pref_title_ringtone);
        ringtonePref.setRingtoneType(RingtoneManager.TYPE_NOTIFICATION);
        ringtonePref.setDefaultValue(pref.getString(CategorySharedPrefs.RINGTONE_PREF_KEY, Settings.System.DEFAULT_NOTIFICATION_URI.toString()));
        screen.addPreference(ringtonePref);

        SwitchPreference switchPref = new SwitchPreference(getActivity());
        switchPref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.VIBRATE_PREF_KEY, categoryId));
        switchPref.setTitle(R.string.pref_title_vibrate);
        switchPref.setDefaultValue(pref.getBoolean(CategorySharedPrefs.VIBRATE_PREF_KEY, true));
        screen.addPreference(switchPref);

        MultiSelectListPreference distancesPref = new MultiSelectListPreference(getActivity());
        distancesPref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.DISTANCES_PREF_KEY, categoryId));
        distancesPref.setTitle(R.string.pref_title_distances);
        distancesPref.setEntries(R.array.pref_distances_titles);
        distancesPref.setEntryValues(R.array.pref_distances_values);
        distancesPref.setSummary(R.string.pref_distances_summary);
        distancesPref.setDefaultValue(pref.getStringSet(CategorySharedPrefs.DISTANCES_PREF_KEY, new HashSet(Arrays.asList(CategorySharedPrefs.DISTANCE_DEFAULT_VALUE))));
        screen.addPreference(distancesPref);

        EditTextPreference textBeforePref = new EditTextPreference(getActivity());
        textBeforePref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.TEXT_BEFORE_PREF_KEY, categoryId));
        textBeforePref.setTitle(R.string.pref_title_before_text);
        textBeforePref.setDefaultValue(pref.getString(CategorySharedPrefs.TEXT_BEFORE_PREF_KEY, "Pozor za "));
        screen.addPreference(textBeforePref);

        SwitchPreference includeDistancePref = new SwitchPreference(getActivity());
        includeDistancePref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.INCLUDE_DISTANCE_PREF_KEY, categoryId));
        includeDistancePref.setTitle(R.string.pref_title_include_distance);
        includeDistancePref.setDefaultValue(pref.getBoolean(CategorySharedPrefs.INCLUDE_DISTANCE_PREF_KEY, true));
        screen.addPreference(includeDistancePref);

        EditTextPreference textAfterPref = new EditTextPreference(getActivity());
        textAfterPref.setKey(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.TEXT_AFTER_PREF_KEY, categoryId));
        textAfterPref.setTitle(R.string.pref_title_after_text);
        textAfterPref.setDefaultValue(pref.getString(CategorySharedPrefs.TEXT_AFTER_PREF_KEY, "m"));
        screen.addPreference(textAfterPref);
        textAfterPref.setDependency(CategorySharedPrefs.getPrefKey(CategorySharedPrefs.INCLUDE_DISTANCE_PREF_KEY, categoryId));


    }
}
