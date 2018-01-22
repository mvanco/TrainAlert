package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;

public class PoiMapFragmentViewModel extends ViewModel {
    public static final int MODE_NONE = 0;
    public static final int MODE_ADD_POI = 1;
    public static final int MODE_EDIT_POI = 2;

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Poi> mRawPois; // TODO: probably redundant field?
    private boolean mInFreeMode = false;
    private @PoiActivityMode int mode;
    private long poiId;

    @Retention (RetentionPolicy.SOURCE)
    @IntDef ( {MODE_NONE, MODE_ADD_POI, MODE_EDIT_POI})
    public @interface PoiActivityMode {
    }

    public PoiMapFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = REPOSITORY;

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> {
            mRawPois = pois;
            mPois.setValue(pois);
        });
    }

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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
            return TaConfig.DEFAULT_LOCATION;
        }
    }

    public void addPoi(Poi poi, TaCallback callback) {
        mRepository.addPoi(poi, callback);
    }

    public LiveData<Location> getLocation() {
        return mLocation;
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

    public MediatorLiveData<List<Poi>> getPois() {
        return mPois;
    }

    private boolean isLoadedLocation() {
        return mLocation.getValue() != null;
    }
}
