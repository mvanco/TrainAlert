package cz.intesys.trainalert.viewmodel;

import android.app.AlertDialog;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.realm.Database;
import cz.intesys.trainalert.entity.realm.Profile;
import cz.intesys.trainalert.utility.Utility;
import io.realm.Realm;

public class MainActivityViewModel extends BaseViewModel {

    // Stream of time events when autoregistration should be performed. True if it is first autoregistration, otherwise false.
    MutableLiveData<Boolean> mAutoRegisterLiveData = new MutableLiveData<>();
    private Utility.IntervalPoller mAutoRegistrationPoller;
    Realm realm = Realm.getDefaultInstance();

    public MainActivityViewModel() {
        mAutoRegistrationPoller = new Utility.IntervalPoller(TaConfig.AUTO_REGISTRATION_INTERVAL, () -> {
             mAutoRegisterLiveData.setValue(false);
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startPolling() {
        mAutoRegisterLiveData.setValue(true);
        new Handler().postDelayed(() -> {
            mAutoRegistrationPoller.startPolling();
        }, TaConfig.AUTO_REGISTRATION_INTERVAL);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopPolling() {
        mAutoRegistrationPoller.stopPolling();
    }

    public void reloadPois() {
        mDataHelper.reloadPois();
    }

    public MutableLiveData<Boolean> getAutoRegisterLiveData() {
        return mAutoRegisterLiveData;
    }

    public boolean isVolumeUp(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(TaConfig.SOUND_ENABLED_KEY, TaConfig.COMPASS_ENABLED_DEFAULT);
    }

    public void setVolumeUp(Context context, boolean volumeUp) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(TaConfig.SOUND_ENABLED_KEY, volumeUp).commit();
    }

    public void addProfile(Context context, String title) {
        realm.beginTransaction();
        Profile profile = Profile.createFromPrefences(context, title);
        Database database = realm.where(Database.class).findFirst();  // Should be only one instance here.
        database.getProfiles().add(profile);
        realm.commitTransaction();
    }

    public void deleteProfile(Profile profile) {
        realm.beginTransaction();
        Database database = realm.where(Database.class).findFirst();
        database.getProfiles().remove(profile);
        realm.commitTransaction();
    }

    public void deleteProfile(String profileName) {
        deleteProfile(getProfile(profileName));
    }

    public void loadProfile(Context context, String profileName) {
        Profile profile = realm.where(Profile.class).contains("name", profileName).findFirst();
        profile.saveToPreferences(context);
    }

    public Profile getProfile(String profileName) {
        return realm.where(Profile.class).contains("name", profileName).findFirst();
    }
}
