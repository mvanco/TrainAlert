package cz.intesys.trainalert.repository;

import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.MutableLiveData;
import android.content.SharedPreferences;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cz.intesys.trainalert.BuildConfig;
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
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;

/**
 * Abstraction upon repository mainly. Ensures reloading (sending additional repository service when needed).
 */
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

    public static final int SPEED_LIMIT_NO_LIMIT = 999;

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
    public static final String TRIP_NO_TRIP = "";
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

    private PublishSubject<Long> mLatestTimeObservable;

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

    public static final String TRIP_ID_BUSINESS_TRIP_0 = "Sluzebni_jizda_0";
    public static final String TRIP_ID_BUSINESS_TRIP_1 = "Sluzebni_jizda_1";

    private DataHelper() {
        mRepository = REPOSITORY;
        mSharedPrefs = TaApplication.getInstance().getSharedPreferences();
        mLatestTimeObservable = PublishSubject.create();

        mLocation = TaConfig.DEFAULT_LOCATION;
        mLocationLiveData = new MutableLiveData<Location>();
        mLocationPoller = new Utility.IntervalPoller(BuildConfig.GPS_TIME_INTERVAL, () -> {
            if (!mLocation.equals(mLocationLiveData.getValue())) {
                mLocationLiveData.setValue(mLocation);
            }

            mRepository.getCurrentLocation(new TaCallback<Location>() {
                @Override
                public void onResponse(Location response) {
                    mLocation = response;
                    Calendar cal = Calendar.getInstance();
                    mLatestTimeObservable.onNext(cal.getTimeInMillis());
                }

                @Override
                public void onFailure(Throwable t) {

                }
            });
        });

        mTripStatusLiveData = new MutableLiveData<>();
        mTripStatusPoller = new Utility.IntervalPoller(TaConfig.TRIP_STATUS_TIME_INTERVAL, () -> {
            mRepository.getTripStatus(new TaCallback<TripStatus>() {
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

    public Observable<Long> getLatestTimeObservable() {
        return mLatestTimeObservable;
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

    /**
     * Live data are changed in specified time points with exactly same interval.
     *
     * @return
     */
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
        mRepository.setTrip(id, new TaCallback<Void>() {
            @Override public void onResponse(Void response) {
                mSharedPrefs.edit().putString(TRIP_ID_KEY, id).commit();
                taCallback.onResponse(null);
            }

            @Override public void onFailure(Throwable t) {
                mSharedPrefs.edit().remove(TRIP_ID_KEY).commit();
//                mSharedPrefs.edit().putString(TRIP_ID_KEY, TRIP_NO_TRIP).commit();
                taCallback.onFailure(new Throwable());
            }
        });

    }

    public void unregisterTrip() {
        mSharedPrefs.edit().remove(TRIP_ID_KEY).commit();
//        mSharedPrefs.edit().putString(TRIP_ID_KEY, TRIP_NO_TRIP).commit();
    }

    public String getTripId() {
        return mSharedPrefs.getString(TRIP_ID_KEY, TRIP_NO_TRIP);
    }

    /**
     * Register to gain access to side menu
     *
     * @param password
     * @param taCallback
     */
    public void register(int password, TaCallback<Void> taCallback) {
        if (password == TaConfig.ADMINISTRATOR_PASSWORD) {
            mSharedPrefs.edit().putBoolean(REGISTERED_KEY, true).commit();
            taCallback.onResponse(null);
        } else {
            mSharedPrefs.edit().putBoolean(REGISTERED_KEY, false).commit();
            taCallback.onFailure(new Throwable());
        }
    }

    /**
     * Unregister to prohibit access to side menu.
     */
    public void unregister() {
        mSharedPrefs.edit().putBoolean(REGISTERED_KEY, false).commit();
    }

    public boolean isRegistered() {
        return !BuildConfig.ACTION_BAR_BLOCKED || mSharedPrefs.getBoolean(REGISTERED_KEY, false);
    }

    public void getPreviousStops(int count, TaCallback<List<Stop>> taCallback) {
        mRepository.getPreviousStops(count, taCallback);
    }

    public void getNextStops(int count, TaCallback<List<Stop>> taCallback) {
        mRepository.getNextStops(count, taCallback);
    }

    public void getFinalStop(TaCallback<ResponseApi<Stop>> taCallback) {
        mRepository.getFinalStop(taCallback);
    }

    public void getTrainId(TaCallback<String> taCallback) {
        String trainId = mSharedPrefs.getString(TRAIN_ID_KEY, "");
        if (trainId == null || trainId.isEmpty()) {

            mRepository.getTrainId(new TaCallback<String>() {
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

    public void reloadPois() {
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

    public void getActiveTrip(TaCallback<String> taCallback) {
        mRepository.getActiveTrip(taCallback);
    }

    public int getSpeedFromCategory(@CategoryId int categoryId) {
        switch (categoryId) {
            case POI_TYPE_SPEED_LIMITATION_20:
                return 20;
            case POI_TYPE_SPEED_LIMITATION_30:
                return 30;
            case POI_TYPE_SPEED_LIMITATION_40:
                return 40;
            case POI_TYPE_SPEED_LIMITATION_50:
                return 50;
            case POI_TYPE_SPEED_LIMITATION_70:
                return 70;
            default:
                return -1;
        }
    }
}
