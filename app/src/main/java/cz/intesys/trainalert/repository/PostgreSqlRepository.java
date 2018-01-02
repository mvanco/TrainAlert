package cz.intesys.trainalert.repository;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.api.LocationAPI;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.api.PoisApi;
import cz.intesys.trainalert.api.TaServerApi;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.rest.TaClient;
import cz.intesys.trainalert.utility.Utility.LocationPoller;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Note: If used with location, there must be added getLifecycle().addObserver(PostgreSqlRepository.getInstance())
 * on fragment or activity. For loading POIs, loadPois() method should be used.
 */
public class PostgreSqlRepository implements Repository {
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

    public static PostgreSqlRepository getInstance() {
        if (sInstance == null) {
            sInstance = new PostgreSqlRepository();
        }
        return sInstance;
    }

    @Override
    public LiveData<Location> getCurrentLocation() {
        return mCurrentLocation;
    }

    @Override
    public void loadPois() {
        Call<PoisApi> call = mApiService.getPois();
        call.enqueue(new Callback<PoisApi>() {
            @Override
            public void onResponse(Call<PoisApi> call, Response<PoisApi> response) {
                if (response.body() == null) {
                    return;
                }
                List<Poi> pois = new ArrayList<>();
                for (PoiApi poiApi : response.body().getPois()) {
                    pois.add(new Poi(poiApi));
                }
                if (mPois.getValue() != null && mPois.getValue().equals(pois)) {
                    return; // Prevent reloading of new data (e.g. in RecyclerView) when it is the same as previous - it suppress UI artifacts
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

    @OnLifecycleEvent (Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mLocationPoller.startPolling();
    }

    @OnLifecycleEvent (Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mLocationPoller.stopPolling();
    }


    void loadCurrentLocation() {
        Log.d("testorder", "loadCurrentLocation()");
        Call<LocationAPI> call = mApiService.getLocation();
        call.enqueue(new Callback<LocationAPI>() {
            @Override
            public void onResponse(Call<LocationAPI> call, Response<LocationAPI> response) {
                if (response.body() == null) {
                    return;
                }
                Log.e("testorder", "onResponse() id:" + response.body().getId());
                mCurrentLocation.setValue(new Location(response.body()));
            }

            @Override
            public void onFailure(Call<LocationAPI> call, Throwable t) {
                // TODO: handle this
            }
        });
    }
}