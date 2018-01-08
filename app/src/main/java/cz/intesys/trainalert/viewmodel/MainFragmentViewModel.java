package cz.intesys.trainalert.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;
import cz.intesys.trainalert.utility.Utility;
import io.reactivex.Observable;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;

public class MainFragmentViewModel extends AndroidViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;
    private boolean mShouldSwitchToFreeMode = false;
    private boolean mFreeMode = false;
    private boolean animating = true;
    private SharedPreferences mSharedPreferences;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
        mLocation = new MediatorLiveData<Location>();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = REPOSITORY;
        mDisabledAlarms = new ArrayList<Alarm>();

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> {
            mPois.setValue(pois);
        });
        loadPOIs();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(application.getApplicationContext());
    }

    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public boolean isAnimating() {
        return animating;
    }

    public void setAnimating(boolean animating) {
        this.animating = animating;
    }

    public boolean isFreeMode() {
        return mFreeMode;
    }

    public void setFreeMode(boolean freeMode) {
        mFreeMode = freeMode;
    }

    public boolean isShouldSwitchToFreeMode() {
        return mShouldSwitchToFreeMode;
    }

    public void setShouldSwitchToFreeMode(boolean shouldSwitchToFreeMode) {
        mShouldSwitchToFreeMode = shouldSwitchToFreeMode;
    }

    public Location getLastLocation() {
        if (isLoadedLocation()) {
            return mLocation.getValue();
        } else {
            return getStarterLocation();
        }
    }

    public Location getStarterLocation() {
        return new Location(50.48365189588503, 14.039404579177328);
    }

    public boolean isLoadedLocation() {
        return mLocation.getValue() != null;
    }

    public LiveData<Location> getLocation() {
        return mLocation;
    }

    public Observable<List<Poi>> getPoisObservable(LifecycleOwner owner) {
        return Utility.createLongTermUpdateObservable(owner, mPois);
    }

    public List<Poi> getLastPois() {
        return mPois.getValue();
    }

    public boolean areLoadedPois() {
        return mPois.getValue() != null;
    }

    public List<Alarm> getDisabledAlarms() {
        return mDisabledAlarms;
    }

    public void restartRepository() {
        if (mRepository instanceof SimulatedRepository) {
            ((SimulatedRepository) mRepository).restartRepository();
        }
    }

    public void loadPOIs() {
        mRepository.loadPois();
    }

    /**
     * Enables getLastPois() funcionality without handling all location updates
     *
     * @param owner
     */
    public void enableLastLocation(LifecycleOwner owner) {
        mLocation.observe(owner, (location) -> {
        });
    }

    /**
     * Enables getLastPois() funcionality without handling all pois updates
     *
     * @param owner
     */
    public void enableLastPois(LifecycleOwner owner) {
        mPois.observe(owner, (pois) -> {
        });
    }

    /**
     * @return current alarms and dis
     */
    public List<Alarm> getCurrentAlarms() {
        List<Alarm> currentAlarms = new ArrayList<>();
        if (!areLoadedPois()) {
            return currentAlarms;
        }
        for (Poi poi : mPois.getValue()) {
            Log.d("distance", "distance to " + poi.getMetaIndex() + " is " + getLastLocation().toGeoPoint().distanceTo(poi));
            for (Alarm alarm : poi.getAlarmList(mSharedPreferences)) {
                Log.d("alarmDistances", "poi: " + poi.getMetaIndex() + ", alarmDistance: " + alarm.getDistance());
                if (isDisabled(alarm)) {
                    continue;
                }
                if (getLastLocation().toGeoPoint().distanceTo(poi) < alarm.getDistance()) {
                    Log.d("showNotification", "poi: " + poi.getMetaIndex() + ", distance: " + alarm.getDistance());
                    currentAlarms.add(alarm);
                    mDisabledAlarms.add(alarm);
                }
            }

        }

        // Enable alarm of Poi with sufficient distance again
        List<Alarm> alarmsToEnable = new ArrayList<Alarm>();

        for (Alarm alarm : getDisabledAlarms()) {
            if (getLastLocation().toGeoPoint().distanceTo(alarm.getPoi()) > alarm.getDistance()) {
                alarmsToEnable.add(alarm);
            }
        }

        for (Alarm alarm : alarmsToEnable) {
            mDisabledAlarms.remove(alarm);
        }

        return currentAlarms;
    }

    private boolean isDisabled(Alarm alarm) {
        for (Alarm disabledAlarm : mDisabledAlarms) {
            if (disabledAlarm.equals(alarm)) {
                return true;
            }
        }
        return false;
    }


//    private void handleNotification(GeoPoint currentLocation) {
//        if (getActivity() == null || !mViewModel.areLoadedPois()) { // If not attached or not loaded POIs yet.
//            return;
//        }
//        for (Poi poi : mViewModel.getLastPois()) {
//            Log.d("distance", "distance to " + poi.getMetaIndex() + " is " + currentLocation.distanceTo(poi));
//            for (Alarm alarm : poi.getAlarmList(PreferenceManager.getDefaultSharedPreferences(getActivity()))) {
//                Log.d("alarmDistances", "poi: " + poi.getMetaIndex() + ", alarmDistance: " + alarm.getDistance());
//                if (alarm.isDisabled()) {
//                    continue;
//                }
//                if (currentLocation.distanceTo(poi) < alarm.getDistance()) {
//                    Log.d("showNotification", "poi: " + poi.getMetaIndex() + ", distance: " + alarm.getDistance());
//                    showTravelNotification(alarm);
//                    mViewModel.disableAlarm(alarm);
//                }
//            }
//        }
//
//        // Enable alarm of Poi with sufficient distance again
//        List<Alarm> alarmsToRemove = new ArrayList<Alarm>();
//
//        for (Alarm alarm : mViewModel.getDisabledAlarms()) {
//            if (currentLocation.distanceTo(alarm.getPoi()) > alarm.getDistance()) {
//                alarmsToRemove.add(alarm);
//            }
//        }
//
//        for (Alarm alarm : alarmsToRemove) {
//            mViewModel.enableAlarm(alarm);
//        }
//    }
}
