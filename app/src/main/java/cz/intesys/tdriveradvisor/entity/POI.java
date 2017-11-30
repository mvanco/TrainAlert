package cz.intesys.tdriveradvisor.entity;

import cz.intesys.tdriveradvisor.utility.Utility.POIType;

public class POI {
    private @POIType int type;
    private String title;
    private Double latitude;
    private Double longitude;

    public POI(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
