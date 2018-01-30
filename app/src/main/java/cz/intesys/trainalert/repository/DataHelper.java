package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Category;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.utility.Utility;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;

/**
 * Created by Matus on 26.01.2018.
 */

public class DataHelper implements LifecycleObserver {

    private static DataHelper sInstance;

    private Repository mRepository;
    private Location mLocation;
    private MutableLiveData<Location> mLocationLiveData;
    private List<Poi> mPois;
    private MutableLiveData<List<Poi>> mPoisLiveData;
    private Utility.LocationPoller mLocationPoller;

    private DataHelper() {
        mRepository = REPOSITORY;
        mLocation = TaConfig.DEFAULT_LOCATION;
        mLocationLiveData = new MutableLiveData<Location>();
        mPois = new ArrayList<>();
        mPoisLiveData = new MutableLiveData<>();

        mLocationPoller = new Utility.LocationPoller(() -> {
            if (!mLocation.equals(mLocationLiveData.getValue())) {
                mLocationLiveData.setValue(mLocation);
            }
            mRepository.getCurrentLocation(new TaCallback<Location>() {
                @Override
                public void onResponse(Location response) {
                    mLocation = response;
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        });
    }

    public static DataHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DataHelper();
        }
        return sInstance;
    }

    /**
     * Warning, index in the List must correspond category id!
     *
     * @return list of statically defined categories
     */
    public List<Category> getCategories() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(0, "Přechod", R.drawable.poi_crossing));
        categories.add(new Category(1, "Omezení 50", R.drawable.poi_speed_limitation));
        categories.add(new Category(2, "Omezení 70", R.drawable.poi_speed_limitation));
        categories.add(new Category(3, "Stanice", R.drawable.poi_train_station));
        categories.add(new Category(4, "Most", R.drawable.poi_bridge));
        categories.add(new Category(5, "Výhybka", R.drawable.poi_turnout));
        return categories;
    }


    public synchronized void startLocationPolling() {
        mLocationPoller.startPolling();
    }

    public synchronized void stopLocationPolling() {
        mLocationPoller.stopPolling();
    }

    public synchronized MutableLiveData<Location> getLocationLiveData() {
        return mLocationLiveData;
    }

    public synchronized Location getLocation() {
        return mLocation;
    }

    public synchronized List<Poi> getPois() {
        return mPois;
    }

    public synchronized MutableLiveData<List<Poi>> getPoisLiveData() {
        reloadPois();
        return mPoisLiveData;
    }

    public synchronized void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        mRepository.addPoi(poi, new TaCallback<Poi>() {
            @Override
            public void onResponse(Poi response) {
                taCallback.onResponse(response);
                reloadPois();
            }

            @Override
            public void onFailure(Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    public synchronized void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        mRepository.editPoi(id, poi, new TaCallback<Poi>() {
            @Override
            public void onResponse(Poi response) {
                taCallback.onResponse(response);
                reloadPois();
            }

            @Override
            public void onFailure(Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }


    public boolean areLoadedPois() {
        return mPoisLiveData.getValue() != null;
    }

    public boolean isLoadedLocation() {
        return mLocationLiveData.getValue() != null;
    }

    private void reloadPois() {
        mRepository.getPois(new TaCallback<List<Poi>>() {
            @Override
            public void onResponse(List<Poi> response) {
                if (!mPois.equals(response)) {
                    mPois.clear();
                    mPois.addAll(response);
                    mPoisLiveData.setValue(response);
                }
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
}
