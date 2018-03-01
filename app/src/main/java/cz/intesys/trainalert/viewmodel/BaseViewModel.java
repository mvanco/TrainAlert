package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.repository.DataHelper;

/**
 * Works with repository
 */
public class BaseViewModel extends ViewModel implements LifecycleObserver {
    private DataHelper mDataHelper;
    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;

    public BaseViewModel() {
        mDataHelper = DataHelper.getInstance();
        mLocation = new MediatorLiveData<>();
        mPois = new MediatorLiveData<>();
        mLocation.addSource(mDataHelper.getLocationLiveData(), location -> mLocation.setValue(location));
        mPois.addSource(mDataHelper.getPoisLiveData(), pois -> mPois.setValue(pois));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mDataHelper.startLocationPolling();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mDataHelper.stopLocationPolling();
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
