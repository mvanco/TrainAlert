package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;

public interface Repository {
    void loadCurrentLocation();

    LiveData<Location> getCurrentLocation();

    void loadPois();

    LiveData<List<Poi>> getPois();
}
