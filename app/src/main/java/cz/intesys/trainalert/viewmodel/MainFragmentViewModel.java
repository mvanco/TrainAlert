package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TripStatus;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;
import io.reactivex.Observable;
import io.reactivex.Observer;

public class MainFragmentViewModel extends BaseViewModel {

    MutableLiveData<Void> mMapMovementLiveData;
    private List<Alarm> mDisabledAlarms;
    private boolean mFreeMode = false;
    private boolean animating = true;
    private Utility.IntervalPoller mMapMovementPoller;
    private boolean mBlockedSwitchingToFreeMode;
    private int mSpeedLimit = DataHelper.SPEED_LIMIT_NO_LIMIT;

    public MainFragmentViewModel() {
        mDisabledAlarms = new ArrayList<Alarm>();
        mMapMovementPoller = new Utility.IntervalPoller(TaConfig.MAP_MOVEMENT_INTERVAL, () -> {
            mMapMovementLiveData.setValue(null);
        });
        mMapMovementLiveData = new MutableLiveData<>();
    }

    public Observable<String> createAtStopNotificationObservable(LifecycleOwner owner) {
        Observable<String> atStopNotificationObservable = Utility.createObservableFromLiveData(owner, mDataHelper.getTripStatusLiveData())
                .map(tripStatus -> (tripStatus != null && tripStatus.getAtStop() != null) ? tripStatus.getAtStop() : "")
                .distinctUntilChanged();
        return atStopNotificationObservable;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mMapMovementPoller.startPolling();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mMapMovementPoller.stopPolling();
    }

    public boolean isBlockedSwitchingToFreeMode() {
        return mBlockedSwitchingToFreeMode;
    }

    public void setBlockedSwitchingToFreeMode(boolean blockedSwitchingToFreeMode) {
        mBlockedSwitchingToFreeMode = blockedSwitchingToFreeMode;
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

    public List<Alarm> getDisabledAlarms() {
        return mDisabledAlarms;
    }

    /**
     * @return current alarms and dis
     */
    public List<Alarm> getCurrentAlarms() {
        List<Alarm> currentAlarms = new ArrayList<>();
        if (!areLoadedPois() || !isLoadedLocation()) {
            return currentAlarms;
        }
        for (Poi poi : getPois()) {
            Log.d("distance", "distance to " + poi.getId() + " is " + getLocation().toGeoPoint().distanceTo(poi));
            for (Alarm alarm : poi.getAlarmList()) {
                Log.d("alarmDistances", "poi: " + poi.getId() + ", alarmDistance: " + alarm.getDistance());
                if (isDisabled(alarm)) {
                    continue;
                }
                if (getLocation().toGeoPoint().distanceTo(poi) < alarm.getDistance()) {
                    Log.d("showNotification", "poi: " + poi.getId() + ", distance: " + alarm.getDistance());
                    currentAlarms.add(alarm);
                    mDisabledAlarms.add(alarm);
                }
            }

        }

        // Enable alarm of Poi with sufficient distance again
        List<Alarm> alarmsToEnable = new ArrayList<Alarm>();

        for (Alarm alarm : getDisabledAlarms()) {
            if (getLocation().toGeoPoint().distanceTo(alarm.getPoi()) > alarm.getDistance()) {
                alarmsToEnable.add(alarm);
            }
        }

        for (Alarm alarm : alarmsToEnable) {
            mDisabledAlarms.remove(alarm);
        }

        return currentAlarms;
    }

    public Poi getPassingPoi() {
        for (Poi poi : getPois()) {
            if (getLocation().toGeoPoint().distanceTo(poi) < 50) {
                return poi;
            }
        }

        return null;
    }

    public MutableLiveData<Void> getMapMovementLiveData() {
        return mMapMovementLiveData;
    }

    private boolean isDisabled(Alarm alarm) {
        for (Alarm disabledAlarm : mDisabledAlarms) {
            if (disabledAlarm.equals(alarm)) {
                return true;
            }
        }
        return false;
    }

    public void setSpeedLimit(@DataHelper.CategoryId int categoryId) {
        mSpeedLimit = mDataHelper.getSpeedFromCategory(categoryId);
    }

    public int getSpeedLimit() {
        return mSpeedLimit;
    }
}
