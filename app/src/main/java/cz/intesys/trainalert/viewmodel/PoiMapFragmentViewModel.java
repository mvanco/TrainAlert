package cz.intesys.trainalert.viewmodel;

import android.databinding.ObservableField;

import java.util.concurrent.TimeUnit;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.fragment.PoiMapFragment;
import cz.intesys.trainalert.repository.DataHelper;
import io.reactivex.Observable;
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
        mOnCoordinatesChangedObservable.onNext(true);
    }

    public void setPoiCategory(@DataHelper.CategoryId int category) {
        poi.set(new Poi(poi.get().getId(), poi.get().getTitle(), poi.get().getLatitude(), poi.get().getLongitude(), category));
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
}
