package cz.intesys.trainalert.repository;

import java.util.List;

import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.entity.TaCallback;

public interface Repository {
    void getCurrentLocation(TaCallback<Location> taCallback);

    void getPois(TaCallback<List<Poi>> taCallback);

    void addPoi(Poi poi, TaCallback<Poi> taCallback);

    void editPoi(long id, Poi poi, TaCallback<Poi> taCallback);

    void deletePoi(long id, TaCallback<Poi> taCallback);

    void getTrips(String id, TaCallback<List<String>> taCallback);

    void setTrip(String id, TaCallback<Void> taCallback);

    void getPreviousStops(int count, TaCallback<List<Stop>> taCallback);

    void getNextStops(int count, TaCallback<List<Stop>> taCallback);

    void getFinalStop(TaCallback<Stop> taCallback);

    void getTrainId(TaCallback<String> taCallback);
}
