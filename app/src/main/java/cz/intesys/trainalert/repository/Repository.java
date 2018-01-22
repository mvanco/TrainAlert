package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TaCallback;

public interface Repository extends LifecycleObserver {
    LiveData<Location> getCurrentLocation();

    LiveData<List<Poi>> getPois(); // Return LiveData and calls initial loading of POIs

    void addPoi(Poi poi, TaCallback<Poi> taCallback);

    void editPoi(long id, Poi poi, TaCallback<Poi> taCallback);
}
