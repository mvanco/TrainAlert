package cz.intesys.trainalert.api;

import com.google.gson.annotations.Expose;

public class ActiveTripApi {
    @Expose
    private String trip;

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }
}
