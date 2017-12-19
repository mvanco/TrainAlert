package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.PostgreSqlRepository;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class MainFragmentViewModel extends ViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;
    private boolean mShouldSwitchToFreeMode = false;
    private boolean mFreeMode = false;

    public MainFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = PostgreSqlRepository.getInstance();
        mDisabledAlarms = new ArrayList<Alarm>();

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> mPois.setValue(pois));
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

    public void loadPOIs() {
        mRepository.loadPois();
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

    public boolean areLoadedPois() {
        return mPois.getValue() != null;
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
}
