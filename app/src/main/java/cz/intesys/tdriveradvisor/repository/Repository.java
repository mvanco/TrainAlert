package cz.intesys.tdriveradvisor.repository;

import android.content.Context;

import java.util.List;

import cz.intesys.tdriveradvisor.entity.Location;
import cz.intesys.tdriveradvisor.entity.POI;

public interface Repository {
    Location getCurrentLocation();

    List<POI> getPOIs(Context context);
}
