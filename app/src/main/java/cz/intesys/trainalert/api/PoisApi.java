package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Exposed field names must correspond to server response JSON
 */
public class PoisApi {
    @Expose
    private List<PoiApi> poiList;

    public PoisApi(List<PoiApi> pois) {
        this.poiList = new ArrayList<PoiApi>(pois);
    }

    public List<PoiApi> getPois() {
        return poiList;
    }

    public void setPois(List<PoiApi> pois) {
        this.poiList = pois;
    }


}
