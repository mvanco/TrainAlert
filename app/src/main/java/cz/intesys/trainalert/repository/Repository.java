package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.POI;

public interface Repository {
    LiveData<Location> getCurrentLocation();

    List<POI> getPOIs(Context context);
}
