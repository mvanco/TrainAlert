package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

public class DataHelper implements LifecycleObserver {

    public static final int POI_TYPE_CROSSING = 0; // Přejezd
    public static final int POI_TYPE_TRAIN_STATION = 1; // Železniční stanice
    public static final int POI_TYPE_STOP = 2; // Zastávka
    public static final int POI_TYPE_LIGHTS = 3; // Vjezdové návěstidlo
    public static final int POI_TYPE_BEFORE_LIGHTS = 4; // Předvěst
    public static final int POI_TYPE_SPEED_LIMITATION_20 = 5; // Omezení rychlosti 20km/h
    public static final int POI_TYPE_SPEED_LIMITATION_30 = 6; // Omezení rychlosti 30km/h
    public static final int POI_TYPE_SPEED_LIMITATION_40 = 7; // Omezení rychlosti 40km/h
    public static final int POI_TYPE_SPEED_LIMITATION_50 = 8; // Omezení rychlosti 50km/h
    public static final int POI_TYPE_SPEED_LIMITATION_70 = 9; // Omezení rychlosti 70km/h
    public static final int POI_TYPE_DEFUALT = POI_TYPE_CROSSING;

    public static final int GRAPHICS_BLACK_SQUARE = 0;
    public static final int GRAPHICS_BLUE_CIRCLE = 1;
    public static final int GRAPHICS_BLUE_RING = 2;
    public static final int GRAPHICS_BLUE_SQUARE = 3;
    public static final int GRAPHICS_GREY_SQUARE = 4;
    public static final int GRAPHICS_RED_CIRCLE = 5;
    public static final int GRAPHICS_RED_RING = 6;
    public static final int GRAPHICS_RED_SQUARE = 7;
    public static final int GRAPHICS_YELLOW_GREY_SQARE = 8;
    public static final int GRAPHICS_DEFAULT = GRAPHICS_BLACK_SQUARE;


    private static DataHelper sInstance;

    private Repository mRepository;
    private Location mLocation;
    private MutableLiveData<Location> mLocationLiveData;
    private List<Poi> mPois;
    private MutableLiveData<List<Poi>> mPoisLiveData;
    private Utility.LocationPoller mLocationPoller;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POI_TYPE_CROSSING, POI_TYPE_TRAIN_STATION, POI_TYPE_STOP, POI_TYPE_LIGHTS, POI_TYPE_BEFORE_LIGHTS, POI_TYPE_SPEED_LIMITATION_20,
            POI_TYPE_SPEED_LIMITATION_30, POI_TYPE_SPEED_LIMITATION_40, POI_TYPE_SPEED_LIMITATION_50, POI_TYPE_SPEED_LIMITATION_70})
    public @interface CategoryId {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRAPHICS_BLACK_SQUARE, GRAPHICS_BLUE_CIRCLE, GRAPHICS_BLUE_RING, GRAPHICS_BLUE_SQUARE, GRAPHICS_GREY_SQUARE, GRAPHICS_RED_CIRCLE,
            GRAPHICS_RED_RING, GRAPHICS_RED_SQUARE, GRAPHICS_YELLOW_GREY_SQARE})
    public @interface GraphicsId {
    }

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
        categories.add(POI_TYPE_CROSSING, new Category(POI_TYPE_CROSSING, R.string.category_crossing, R.drawable.poi_crossing));
        categories.add(POI_TYPE_TRAIN_STATION, new Category(POI_TYPE_TRAIN_STATION, R.string.category_trainstation, R.drawable.poi_train_station));
        categories.add(POI_TYPE_STOP, new Category(POI_TYPE_STOP, R.string.category_stop, R.drawable.poi_train_station));
        categories.add(POI_TYPE_LIGHTS, new Category(POI_TYPE_LIGHTS, R.string.category_lights, R.drawable.poi_turnout));
        categories.add(POI_TYPE_BEFORE_LIGHTS, new Category(POI_TYPE_BEFORE_LIGHTS, R.string.category_beforelights, R.drawable.poi_turnout));
        categories.add(POI_TYPE_SPEED_LIMITATION_20, new Category(POI_TYPE_SPEED_LIMITATION_20, R.string.category_speed_limitation_20, R.drawable.poi_speed_limitation));
        categories.add(POI_TYPE_SPEED_LIMITATION_30, new Category(POI_TYPE_SPEED_LIMITATION_30, R.string.category_speed_limitation_30, R.drawable.poi_speed_limitation));
        categories.add(POI_TYPE_SPEED_LIMITATION_40, new Category(POI_TYPE_SPEED_LIMITATION_40, R.string.category_speed_limitation_40, R.drawable.poi_speed_limitation));
        categories.add(POI_TYPE_SPEED_LIMITATION_50, new Category(POI_TYPE_SPEED_LIMITATION_50, R.string.category_speed_limitation_50, R.drawable.poi_speed_limitation));
        categories.add(POI_TYPE_SPEED_LIMITATION_70, new Category(POI_TYPE_SPEED_LIMITATION_70, R.string.category_speed_limitation_70, R.drawable.poi_speed_limitation));
        return categories;
    }

    public Category findCategoryById(@CategoryId int categoryId) {
        return getCategories().get(categoryId);
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
