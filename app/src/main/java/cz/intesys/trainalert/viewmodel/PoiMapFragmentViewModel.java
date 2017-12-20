package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class PoiMapFragmentViewModel extends ViewModel {
    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Poi> mRawPois;
    private boolean mInFreeMode = false;

    public PoiMapFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = SimulatedRepository.getInstance(); // TODO: change to real PostgreSqlRepository

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> {
            mRawPois = pois;
            mPois.setValue(pois);
        });
        loadPOIs();
    }

    public boolean isInFreeMode() {
        return mInFreeMode;
    }

    public void setInFreeMode(boolean inFreeMode) {
        mInFreeMode = inFreeMode;
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
