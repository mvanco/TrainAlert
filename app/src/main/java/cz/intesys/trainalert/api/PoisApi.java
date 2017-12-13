package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class PoisApi {
    @Expose
    private List<PoiApi> pois;

    public PoisApi(List<PoiApi> pois) {
        this.pois = new ArrayList<PoiApi>(pois);
    }

    public List<PoiApi> getPois() {
        return pois;
    }

    public void setPois(List<PoiApi> pois) {
        this.pois = pois;
    }


}
