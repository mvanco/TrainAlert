package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LiveData;

import cz.intesys.trainalert.api.PoisApi;
import cz.intesys.trainalert.entity.Location;

public interface Repository {
    void loadCurrentLocation();
    LiveData<Location> getCurrentLocation();

    void loadPois();

    LiveData<PoisApi> getPois();
}
