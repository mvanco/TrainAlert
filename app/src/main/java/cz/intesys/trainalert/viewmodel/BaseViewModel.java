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
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.entity.TripStatus;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Abstraction upon {@link DataHelper} class. Works with data in context of activity/fragment lifecycle. Start/stop polling of services on resume/pause.
 */
public class BaseViewModel extends ViewModel implements LifecycleObserver {
    private Location mCurrentLocation;
    protected DataHelper mDataHelper;
    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private MediatorLiveData<TripStatus> mTripStatusLiveData;
    private long mLastServerResponseTime;

    public BaseViewModel() {
        mDataHelper = DataHelper.getInstance();
        mLocation = new MediatorLiveData<>();
        mPois = new MediatorLiveData<>();

        mCurrentLocation = new Location(TaConfig.DEFAULT_LOCATION.getLatitude(), TaConfig.DEFAULT_LOCATION.getLongitude());
        mTripStatusLiveData = new MediatorLiveData<>();
        mLocation.addSource(mDataHelper.getLocationLiveData(), location -> {
            mLocation.setValue(location);
            mCurrentLocation = location;
        });
        mPois.addSource(mDataHelper.getPoisLiveData(), pois -> mPois.setValue(pois));
        mTripStatusLiveData.addSource(mDataHelper.getTripStatusLiveData(), tripStatus -> mTripStatusLiveData.setValue(tripStatus));
        mDataHelper.getLatestTimeObservable().subscribe(date -> mLastServerResponseTime = date);
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public MutableLiveData<TripStatus> getTripStatusLiveData() {
        return mTripStatusLiveData;
    }

    /**
     * Create gps timeout observable using debounce operator on server responses events.
     * Loader is turned on when interval between responses is greater then determined time.
     *
     * @param owner
     * @return
     */
    public Observable<Boolean> createGpsTimeoutDebounceObservable(LifecycleOwner owner) {
        Observable<Boolean> showLoaderObservable = mDataHelper.getLatestTimeObservable()
                .debounce(TaConfig.GPS_TIMEOUT_DELAY, TimeUnit.MILLISECONDS)
                .map(location -> true);

        Observable<Boolean> hideLoaderObservable = Utility.createObservableFromLiveData(owner, mDataHelper.getLocationLiveData())
                .map(location -> false);

        return Observable.merge(showLoaderObservable, hideLoaderObservable)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Create gps timeout observable using delay operator on server enqueue events.
     * Loader is turned on if there is some response after determined time.
     *
     * @param owner
     * @return
     */
    public Observable<Boolean> createGpsTimeoutDelayObservable(LifecycleOwner owner) {
        Observable<Boolean> showLoaderObservable = Utility.createObservableFromLiveData(owner, mDataHelper.getLocationLiveData())
                .delay(TaConfig.GPS_TIMEOUT_DELAY, TimeUnit.MILLISECONDS)
                .map(location -> {
                    Calendar delayedTime = Calendar.getInstance();
                    Log.d("loader2", "latestTIme" + mLastServerResponseTime + "  delayedTime: " + delayedTime.getTimeInMillis());
                    Log.d("loader2", "difference " + String.valueOf(delayedTime.getTimeInMillis() - mLastServerResponseTime));
                    return (delayedTime.getTimeInMillis() - mLastServerResponseTime) > Long.valueOf(TaConfig.GPS_TIMEOUT_DELAY);
                });

        Observable<Boolean> hideLoaderObservable = Utility.createObservableFromLiveData(owner, mDataHelper.getLocationLiveData())
                .map(location -> false);

        return Observable.merge(showLoaderObservable, hideLoaderObservable)
                .distinctUntilChanged()
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
}