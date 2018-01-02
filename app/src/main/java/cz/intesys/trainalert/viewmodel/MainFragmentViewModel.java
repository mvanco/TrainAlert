package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;

public class MainFragmentViewModel extends ViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;
    private boolean mShouldSwitchToFreeMode = false;
    private boolean mFreeMode = false;
    private List<Poi> mRawPois;
    private boolean animating = true;

    public MainFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = REPOSITORY;
        mDisabledAlarms = new ArrayList<Alarm>();

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> {
            mRawPois = pois;
            mPois.setValue(pois);
        });
        loadPOIs();
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

    public LiveData<List<Poi>> getPois() {
        return mPois;
    }

    public List<Poi> getLastPois() {
        return mRawPois;
    }

    public boolean areLoadedPois() {
        return mRawPois != null;
    }

    public void disableAlarm(Alarm alarm) {
        mDisabledAlarms.add(alarm);
        alarm.disable();
    }

    public void enableAlarm(Alarm alarm) {
        alarm.enable();
        mDisabledAlarms.remove(alarm);
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
}
