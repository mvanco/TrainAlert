package cz.intesys.trainalert.viewmodel;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Poi;

public class MainFragmentViewModel extends BaseViewModel {

    private List<Alarm> mDisabledAlarms;
    private boolean mShouldSwitchToFreeMode = false;
    private boolean mFreeMode = false;
    private boolean animating = true;

    public MainFragmentViewModel() {
        mDisabledAlarms = new ArrayList<Alarm>();
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

    private boolean isDisabled(Alarm alarm) {
        for (Alarm disabledAlarm : mDisabledAlarms) {
            if (disabledAlarm.equals(alarm)) {
                return true;
            }
        }
        return false;
    }
}
