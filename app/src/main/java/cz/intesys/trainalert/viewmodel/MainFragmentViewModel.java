package cz.intesys.trainalert.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;

public class MainFragmentViewModel extends AndroidViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;
    private boolean mShouldSwitchToFreeMode = false;
    private boolean mFreeMode = false;
    private boolean animating = true;

    public MainFragmentViewModel(@NonNull Application application) {
        super(application);
        mLocation = new MediatorLiveData<Location>();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = REPOSITORY;
        mDisabledAlarms = new ArrayList<Alarm>();

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> mPois.setValue(pois));
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
            return TaConfig.DEFAULT_LOCATION;
        }
    }

    public LiveData<Location> getLocation() {
        return mLocation;
    }

    public LiveData<List<Poi>> getPois() {
        return mPois;
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

    /**
     * @return current alarms and dis
     */
    public List<Alarm> getCurrentAlarms() {
        List<Alarm> currentAlarms = new ArrayList<>();
        if (!areLoadedPois()) {
            return currentAlarms;
        }
        for (Poi poi : mPois.getValue()) {
            Log.d("distance", "distance to " + poi.getId() + " is " + getLastLocation().toGeoPoint().distanceTo(poi));
            for (Alarm alarm : poi.getAlarmList()) {
                Log.d("alarmDistances", "poi: " + poi.getId() + ", alarmDistance: " + alarm.getDistance());
                if (isDisabled(alarm)) {
                    continue;
                }
                if (getLastLocation().toGeoPoint().distanceTo(poi) < alarm.getDistance()) {
                    Log.d("showNotification", "poi: " + poi.getId() + ", distance: " + alarm.getDistance());
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

    private boolean isLoadedLocation() {
        return mLocation.getValue() != null;
    }

    private boolean isDisabled(Alarm alarm) {
        for (Alarm disabledAlarm : mDisabledAlarms) {
            if (disabledAlarm.equals(alarm)) {
                return true;
            }
        }
        return false;
    }
}
