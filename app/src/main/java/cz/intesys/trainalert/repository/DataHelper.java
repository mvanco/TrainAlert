package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;
import android.util.Log;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaApplication;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.api.ResponseApi;
import cz.intesys.trainalert.entity.Category;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.entity.TripStatus;
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
    public static final int POI_TYPE_STOP_AZD = 10; // Železniční zastávka z AŽD
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

    private static final String FIRST_RUN_KEY = "first_run";
    private static final String TRAIN_ID_KEY = "train_id";
    private static final String TRIP_ID_KEY = "trip_id";
    private static final String REGISTERED_KEY = "registered";

    private static DataHelper sInstance;
    private Repository mRepository;
    private SharedPreferences mSharedPrefs;

    private Location mLocation;
    private MutableLiveData<Location> mLocationLiveData;
    private Utility.IntervalPoller mLocationPoller;

    private List<Poi> mPois;
    private MutableLiveData<List<Poi>> mPoisLiveData;
    private Utility.IntervalPoller mPoisPoller;

    private MutableLiveData<Long> mLatestTimeLiveData;

    private MutableLiveData<TripStatus> mTripStatusLiveData;
    private Utility.IntervalPoller mTripStatusPoller;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({POI_TYPE_CROSSING, POI_TYPE_TRAIN_STATION, POI_TYPE_STOP, POI_TYPE_LIGHTS, POI_TYPE_BEFORE_LIGHTS, POI_TYPE_SPEED_LIMITATION_20,
            POI_TYPE_SPEED_LIMITATION_30, POI_TYPE_SPEED_LIMITATION_40, POI_TYPE_SPEED_LIMITATION_50, POI_TYPE_SPEED_LIMITATION_70, POI_TYPE_STOP_AZD})
    public @interface CategoryId {
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GRAPHICS_BLACK_SQUARE, GRAPHICS_BLUE_CIRCLE, GRAPHICS_BLUE_RING, GRAPHICS_BLUE_SQUARE, GRAPHICS_GREY_SQUARE, GRAPHICS_RED_CIRCLE,
            GRAPHICS_RED_RING, GRAPHICS_RED_SQUARE, GRAPHICS_YELLOW_GREY_SQARE})
    public @interface GraphicsId {
    }

    private DataHelper() {
        mRepository = REPOSITORY;
        mSharedPrefs = TaApplication.getInstance().getSharedPreferences();
        mLatestTimeLiveData = new MutableLiveData<>();

        mLocation = TaConfig.DEFAULT_LOCATION;
        mLocationLiveData = new MutableLiveData<Location>();
        mLocationPoller = new Utility.IntervalPoller(TaConfig.GPS_TIME_INTERVAL, () -> {
            if (!mLocation.equals(mLocationLiveData.getValue())) {
                mLocationLiveData.setValue(mLocation);
            }

            mRepository.getCurrentLocation(new TaCallback<Location>() {
                @Override
                public void onResponse(Location response) {
                    mLocation = response;
                    Calendar cal = Calendar.getInstance();
                    Log.d("loader2", "latest time: " + cal.getTime().toString());
                    mLatestTimeLiveData.setValue(cal.getTimeInMillis());
                    Log.d("loader", "new real location");
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        });

        mTripStatusLiveData = new MutableLiveData<>();
        mTripStatusPoller = new Utility.IntervalPoller(TaConfig.TRIP_STATUS_TIME_INTERVAL, () -> {
            DataHelper.getInstance().getTripStatus(new TaCallback<TripStatus>() {
                @Override public void onResponse(TripStatus response) {
                    mTripStatusLiveData.setValue(response);
                }

                @Override public void onFailure(Throwable t) {

                }
            });
        });

        mPois = new ArrayList<>();
        mPoisLiveData = new MutableLiveData<>();
        mPoisPoller = new Utility.IntervalPoller(TaConfig.GET_POIS_INTERVAL, () -> {
            reloadPois();
        });
    }

    public static DataHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DataHelper();
        }
        return sInstance;
    }

    public MutableLiveData<Long> getLatestTimeLiveData() {
        return mLatestTimeLiveData;
    }

    /**
     * Create additional category array from category ids from server with extra information for this application
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
        categories.add(POI_TYPE_STOP_AZD, new Category(POI_TYPE_STOP_AZD, R.string.category_stop_azd, R.drawable.poi_train_station));
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

    public MutableLiveData<TripStatus> getTripStatusLiveData() {
        return mTripStatusLiveData;
    }

    public synchronized void startTripStatusPolling() {
        mTripStatusPoller.startPolling();
    }

    public synchronized void stopTripStatusPolling() {
        mTripStatusPoller.stopPolling();
    }

    public synchronized void startPoisPolling() {
        mPoisPoller.startPolling();
    }

    public synchronized void stopPoisPolling() {
        mPoisPoller.stopPolling();
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

    public synchronized void deletePoi(long id, TaCallback<Poi> taCallback) {
        mRepository.deletePoi(id, new TaCallback<Poi>() {
            @Override public void onResponse(Poi response) {
                taCallback.onResponse(response);
                reloadPois(); //optimalize - only remove one poi
            }

            @Override public void onFailure(Throwable t) {
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

    public boolean isFirstRun() {
        boolean firstRun = mSharedPrefs.getBoolean(FIRST_RUN_KEY, true);
        if (firstRun) {
            mSharedPrefs.edit().putBoolean(FIRST_RUN_KEY, false).commit();
        }

        return firstRun;
    }

    public void getTrips(TaCallback<List<String>> taCallback) {
        getTrainId(new TaCallback<String>() {
            @Override public void onResponse(String response) {
                REPOSITORY.getTrips(response, taCallback);
            }

            @Override public void onFailure(Throwable t) {
                taCallback.onFailure(new Throwable());
            }
        });
    }

    public void setTrip(String id, TaCallback<Void> taCallback) {
        REPOSITORY.setTrip(id, taCallback);
        mSharedPrefs.edit().putString(TRIP_ID_KEY, id).commit();
    }

    public String getTripId() {
        return mSharedPrefs.getString(TRIP_ID_KEY, "");
    }

    public void register(int password, TaCallback<Void> taCallback) {
        if (password == TaConfig.ADMINISTRATOR_PASSWORD) {
            mSharedPrefs.edit().putBoolean(REGISTERED_KEY, true).commit();
            taCallback.onResponse(null);
        } else {
            mSharedPrefs.edit().putBoolean(REGISTERED_KEY, false).commit();
            taCallback.onFailure(new Throwable());
        }
    }

    public void unregister() {
        mSharedPrefs.edit().putBoolean(REGISTERED_KEY, false).commit();
    }

    public boolean isRegistered() {
        return mSharedPrefs.getBoolean(REGISTERED_KEY, false);
    }

    public void getPreviousStops(int count, TaCallback<List<Stop>> taCallback) {
        REPOSITORY.getPreviousStops(count, taCallback);
    }

    public void getNextStops(int count, TaCallback<List<Stop>> taCallback) {
        REPOSITORY.getNextStops(count, taCallback);
    }

    public void getFinalStop(TaCallback<ResponseApi<Stop>> taCallback) {
        REPOSITORY.getFinalStop(taCallback);
    }

    public void getTripStatus(TaCallback<TripStatus> taCallback) {
        REPOSITORY.getTripStatus(taCallback);
    }

    public void getTrainId(TaCallback<String> taCallback) {
        String trainId = mSharedPrefs.getString(TRAIN_ID_KEY, "");
        if (trainId == null || trainId.isEmpty()) {

            REPOSITORY.getTrainId(new TaCallback<String>() {
                @Override public void onResponse(String response) {
                    taCallback.onResponse(response);
                }

                @Override public void onFailure(Throwable t) {
                    taCallback.onFailure(t);
                }
            });
            setTrainId(trainId);
        }
    }

    public boolean isSpeedLimitCategory(@CategoryId int categoryId) {
        return categoryId >= POI_TYPE_SPEED_LIMITATION_20 && categoryId <= POI_TYPE_SPEED_LIMITATION_70;
    }

    private void setTrainId(String id) {
        mSharedPrefs.edit().putString(TRAIN_ID_KEY, id).commit();
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
