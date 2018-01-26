package cz.intesys.trainalert.viewmodel;

import cz.intesys.trainalert.fragment.PoiMapFragment;

/**
 * BaseViewModel is used only for floatbutton to obtain current location
 */
public class PoiMapFragmentViewModel extends BaseViewModel {

    private boolean mInFreeMode = false;
    private @PoiMapFragment.PoiMapFragmentMode int mode;
    private long poiId;

    public long getPoiId() {
        return poiId;
    }

    public void setPoiId(long poiId) {
        this.poiId = poiId;
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
}
