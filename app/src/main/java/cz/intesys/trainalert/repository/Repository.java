package cz.intesys.trainalert.repository;

import android.content.Context;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.POI;

public interface Repository {
    Location getCurrentLocation();

    List<POI> getPOIs(Context context);
}
