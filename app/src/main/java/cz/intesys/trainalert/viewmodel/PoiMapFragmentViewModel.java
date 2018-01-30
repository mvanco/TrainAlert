package cz.intesys.trainalert.viewmodel;

import android.databinding.ObservableField;

import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.fragment.PoiMapFragment;

/**
 * BaseViewModel is used only for floatbutton to obtain current location
 */
public class PoiMapFragmentViewModel extends BaseViewModel {

    public final ObservableField<Poi> poi = new ObservableField<>();
    private boolean mInFreeMode = false;
    private @PoiMapFragment.PoiMapFragmentMode
    int mode;

    public PoiMapFragmentViewModel() {
        super();
        poi.set(new Poi());
    }

    public void setPoiId(long poiId) {
        poi.set(new Poi(poiId, poi.get().getTitle(), poi.get().getLatitude(), poi.get().getLongitude(), poi.get().getCategory()));
    }

    public void setPoiCoordinates(double latitude, double longitude) {
        poi.set(new Poi(poi.get().getId(), poi.get().getTitle(), latitude, longitude, poi.get().getCategory()));
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
}
