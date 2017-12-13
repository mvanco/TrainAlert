package cz.intesys.trainalert.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.api.LocationAPI;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.api.PoisApi;
import cz.intesys.trainalert.api.TaServerApi;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.rest.TaClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostgreSqlRepository implements Repository, LifecycleObserver {
    private static PostgreSqlRepository sInstance;
    private TaServerApi mApiService;
    private LocationPoller mLocationPoller;
    private MutableLiveData<Location> mCurrentLocation;
    private MutableLiveData<List<Poi>> mPois;

    public PostgreSqlRepository() {
        mApiService = TaClient.createService(TaServerApi.class);
        mLocationPoller = new LocationPoller(() -> loadCurrentLocation());
        mCurrentLocation = new MutableLiveData<>();
        mPois = new MutableLiveData<>();
    }

    public void loadCurrentLocation() {
        Call<LocationAPI> call = mApiService.getLocation();
        call.enqueue(new Callback<LocationAPI>() {
            @Override
            public void onResponse(Call<LocationAPI> call, Response<LocationAPI> response) {
                mCurrentLocation.setValue(new Location(response.body()));
            }

            @Override
            public void onFailure(Call<LocationAPI> call, Throwable t) {
                // TODO: handle this
            }
        });
    }

    @Override
    public LiveData<Location> getCurrentLocation() {
        return mCurrentLocation;
    }

    public void loadPois() {
        Call<PoisApi> call = mApiService.getPois();
        call.enqueue(new Callback<PoisApi>() {
            @Override
            public void onResponse(Call<PoisApi> call, Response<PoisApi> response) {
                List<Poi> pois = new ArrayList<>();
                for (PoiApi poiApi : response.body().getPois()) {
                    pois.add(new Poi(poiApi));
                }
                mPois.setValue(pois);
            }

            @Override
            public void onFailure(Call<PoisApi> call, Throwable t) {
                // TODO: handle this
            }
        });
    }

    @Override
    public LiveData<List<Poi>> getPois() {
        return mPois;
    }

    public static PostgreSqlRepository getInstance() {
        if (sInstance == null) {
            sInstance = new PostgreSqlRepository();
        }
        return sInstance;
    }

    @OnLifecycleEvent (Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mLocationPoller.startPolling();
    }

    @OnLifecycleEvent (Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mLocationPoller.stopPolling();
    }

    private class LocationPoller {
        private Handler mHandler;
        private Runnable mPeriodicUpdateRunnable;
        private boolean mRunning = false;

        LocationPoller(Runnable locationChangedRunnable) {
            mHandler = new Handler();
            mPeriodicUpdateRunnable = () -> {
                locationChangedRunnable.run();
                mHandler.postDelayed(mPeriodicUpdateRunnable, TaConfig.GPS_TIME_INTERVAL);
            };
        }

        void startPolling() {
            if (!isRunning()) {
                mPeriodicUpdateRunnable.run();
                setRunning(true);
            }
        }

        public boolean isRunning() {
            return mRunning;
        }

        public void setRunning(boolean running) {
            mRunning = running;
        }

        void stopPolling() {
            if (isRunning()) {
                mHandler.removeCallbacks(mPeriodicUpdateRunnable);
                setRunning(false);
            }
        }
    }
}