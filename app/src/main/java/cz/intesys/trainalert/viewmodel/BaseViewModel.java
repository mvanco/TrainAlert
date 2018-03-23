package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.entity.TripStatus;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Works with repository
 */
public class BaseViewModel extends ViewModel implements LifecycleObserver {
    private DataHelper mDataHelper;
    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private MediatorLiveData<TripStatus> mTripStatusLiveData;
    private long mLastServerResponse;

    public BaseViewModel() {
        mDataHelper = DataHelper.getInstance();
        mLocation = new MediatorLiveData<>();
        mPois = new MediatorLiveData<>();
        mTripStatusLiveData = new MediatorLiveData<>();
        mLocation.addSource(mDataHelper.getLocationLiveData(), location -> mLocation.setValue(location));
        mPois.addSource(mDataHelper.getPoisLiveData(), pois -> mPois.setValue(pois));
        mTripStatusLiveData.addSource(mDataHelper.getTripStatusLiveData(), tripStatus -> mTripStatusLiveData.setValue(tripStatus));
    }

    public MutableLiveData<TripStatus> getTripStatusLiveData() {
        return mTripStatusLiveData;
    }

    public Observable<Boolean> getGpsTimeoutLiveData(LifecycleOwner owner) {
        mDataHelper.getLatestTimeLiveData().observe(owner, date -> {
            mLastServerResponse = date;
        });

        return Utility.createObservableFromLiveData(owner, mDataHelper.getLocationLiveData())
                .delay(TaConfig.GPS_TIMEOUT_DELAY, TimeUnit.MILLISECONDS)
                .map(location -> {
                    Calendar delayedTime = Calendar.getInstance();
                    Log.d("loader2", "latestTIme" + mLastServerResponse + "  delayedTime: " + delayedTime.getTimeInMillis());
                    Log.d("loader2", "difference " + String.valueOf(delayedTime.getTimeInMillis() - mLastServerResponse));
                    return (delayedTime.getTimeInMillis() - mLastServerResponse) > Long.valueOf(TaConfig.GPS_TIMEOUT_DELAY);
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mDataHelper.startLocationPolling();
        mDataHelper.startTripStatusPolling();
        mDataHelper.startPoisPolling();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mDataHelper.stopLocationPolling();
        mDataHelper.stopTripStatusPolling();
        mDataHelper.stopPoisPolling();
    }

    public LiveData<Location> getLocationLiveData() {
        return mLocation;
    }

    public LiveData<List<Poi>> getPoisLiveData() {
        return mPois;
    }

    public Location getLocation() {
        return mDataHelper.getLocation();
    }

    public List<Poi> getPois() {
        return mDataHelper.getPois();
    }


    public boolean areLoadedPois() {
        return mDataHelper.getPoisLiveData().getValue() != null;
    }

    public boolean isLoadedLocation() {
        return mDataHelper.getLocationLiveData().getValue() != null;
    }

    public void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        mDataHelper.addPoi(poi, taCallback);
    }

    public void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        mDataHelper.editPoi(id, poi, taCallback);
    }

    public void deletePoi(long id, TaCallback<Poi> taCallback) {
        mDataHelper.deletePoi(id, taCallback);
    }

    public void getPreviousStops(int count, TaCallback<List<Stop>> taCallback) {
        mDataHelper.getPreviousStops(count, taCallback);
    }

    public void getNextStops(int count, TaCallback<List<Stop>> taCallback) {
        mDataHelper.getNextStops(count, taCallback);
    }

    public void getFinalStop(TaCallback<Stop> taCallback) {
        mDataHelper.getFinalStop(taCallback);
    }
}