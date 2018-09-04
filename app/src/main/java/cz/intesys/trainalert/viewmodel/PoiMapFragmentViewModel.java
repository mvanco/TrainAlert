package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.OnLifecycleEvent;
import android.databinding.ObservableField;

import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.fragment.PoiMapFragment;
import cz.intesys.trainalert.repository.DataHelper;
import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.BehaviorSubject;

/**
 * BaseViewModel is used only for floatbutton to obtain current location
 */
public class PoiMapFragmentViewModel extends BaseViewModel {

    public final ObservableField<Poi> poi = new ObservableField<>();
    private boolean mInFreeMode = false;
    private @PoiMapFragment.PoiMapFragmentMode
    int mode;
    BehaviorSubject<Boolean> mOnCoordinatesChangedObservable;
    private boolean mActiveSavings = true;

    public PoiMapFragmentViewModel() {
        poi.set(new Poi());
        mOnCoordinatesChangedObservable = BehaviorSubject.create();
    }

    public void setPoiId(long poiId) {
        poi.set(new Poi(poiId, poi.get().getTitle(), poi.get().getLatitude(), poi.get().getLongitude(), poi.get().getCategory()));
    }

    public void setPoiTitle(String title) {
        poi.set(new Poi(poi.get().getId(), title, poi.get().getLatitude(), poi.get().getLongitude(), poi.get().getCategory()));
    }

    public void setPoiCoordinates(double latitude, double longitude) {
        poi.set(new Poi(poi.get().getId(), poi.get().getTitle(), latitude, longitude, poi.get().getCategory()));
        if (mActiveSavings) {
            mOnCoordinatesChangedObservable.onNext(true);
        }
    }

    public void setPoiCategory(@DataHelper.CategoryId int category) {
        poi.set(new Poi(poi.get().getId(), poi.get().getTitle(), poi.get().getLatitude(), poi.get().getLongitude(), category));
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResumePoiMapFragment() {
        mActiveSavings = true;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPausePoiMapFragment() {
        mActiveSavings = false;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(@PoiMapFragment.PoiMapFragmentMode int mode) {
        this.mode = mode;
    }

    public boolean isInFreeMode() {
        return mInFreeMode;
    }

    public void setInFreeMode(boolean inFreeMode) {
        mInFreeMode = inFreeMode;
    }

    public Poi getWorkingPoi() {
        return poi.get();
    }

    public void setWorkingPoi(Poi workingPoi) {
        poi.set(workingPoi);
    }

    public Observable<Boolean> getOnShouldSaveObservable() {
        return mOnCoordinatesChangedObservable.debounce(TaConfig.SAVE_POI_TIMOUT, TimeUnit.MILLISECONDS);
    }

    /**
     * Inactivate savings for a while
     */
    public void inactivateSavings() {
        mActiveSavings = false;
        new android.os.Handler().postDelayed(() -> {
            mActiveSavings = true;
        }, 2000);
    }

    public boolean isActiveSavings() {
        return mActiveSavings;
    }
}
