package cz.intesys.trainalert.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.os.Handler;

import java.util.List;

import cz.intesys.trainalert.TrainAlertConfig;
import cz.intesys.trainalert.api.LocationAPI;
import cz.intesys.trainalert.api.TAServerAPI;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.POI;
import cz.intesys.trainalert.rest.TrainAlertClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostgreSQLRepository implements Repository, LifecycleObserver {
    private static PostgreSQLRepository sInstance;
    private TAServerAPI mApiService;
    private LocationPoller mLocationPoller;
    private MutableLiveData<Location> liveData;

    public PostgreSQLRepository() {
        mApiService = TrainAlertClient.createService(TAServerAPI.class);
        mLocationPoller = new LocationPoller(() -> getCurrentLocation());
        liveData = new MutableLiveData<>();
    }

    public LiveData<Location> getCurrentLocation() {
        Call<LocationAPI> call = mApiService.getLocation();
        call.enqueue(new Callback<LocationAPI>() {
            @Override
            public void onResponse(Call<LocationAPI> call, Response<LocationAPI> response) {
                liveData.setValue(new Location(response.body()));
            }

            @Override
            public void onFailure(Call<LocationAPI> call, Throwable t) {
                // TODO: handle this
            }
        });
        return liveData;
    }

    @Override
    public List<POI> getPOIs(Context context) {
        return null;
    }

    public static PostgreSQLRepository getInstance() {
        if (sInstance == null) {
            sInstance = new PostgreSQLRepository();
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
                mHandler.postDelayed(mPeriodicUpdateRunnable, TrainAlertConfig.GPS_TIME_INTERVAL);
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