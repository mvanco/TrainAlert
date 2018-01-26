package cz.intesys.trainalert.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.api.LocationAPI;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.api.PoisApi;
import cz.intesys.trainalert.api.TaServerApi;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.rest.TaClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Note: If used with location, there must be added getLifecycle().addObserver(PostgreSqlRepository.getInstance())
 * on fragment or activity. For loading POIs, reloadPois() method should be used.
 */
public class PostgreSqlRepository implements Repository {
    private static PostgreSqlRepository sInstance;
    private TaServerApi mApiService;

    public PostgreSqlRepository() {
        mApiService = TaClient.createService(TaServerApi.class);
    }

    public static PostgreSqlRepository getInstance() {
        if (sInstance == null) {
            sInstance = new PostgreSqlRepository();
        }
        return sInstance;
    }

    @Override
    public void getCurrentLocation(TaCallback<Location> taCallback) {
        Call<LocationAPI> call = mApiService.getLocation();
        call.enqueue(new Callback<LocationAPI>() {
            @Override
            public void onResponse(Call<LocationAPI> call, Response<LocationAPI> response) {
                if (response.body() == null) {
                    return;
                }
                Log.e("testorder", "onResponse() id:" + response.body().getId());
                taCallback.onResponse(new Location(response.body()));
            }

            @Override
            public void onFailure(Call<LocationAPI> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override
    public void getPois(TaCallback<List<Poi>> taCallback) {
        Call<PoisApi> call = mApiService.getPois();
        call.enqueue(new Callback<PoisApi>() {
            @Override
            public void onResponse(Call<PoisApi> call, Response<PoisApi> response) {
                if (response.body() == null || response.body().getPois() == null) {
                    return;
                }
                List<Poi> pois = new ArrayList<>();
                for (PoiApi poiApi : response.body().getPois()) {
                    pois.add(new Poi(poiApi));
                }

                taCallback.onResponse(pois);
            }

            @Override
            public void onFailure(Call<PoisApi> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override
    public void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        Call<PoiApi> call = mApiService.addPoi(new PoiApi(poi));
        call.enqueue(new Callback<PoiApi>() {
            @Override
            public void onResponse(Call<PoiApi> call, Response<PoiApi> response) {
                if (response.body() == null) {
                    taCallback.onFailure(new Throwable("body is null"));
                    return;
                }

                taCallback.onResponse(new Poi(response.body()));
            }

            @Override
            public void onFailure(Call<PoiApi> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override
    public void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        Call<PoiApi> call = mApiService.editPoi(id, new PoiApi(poi));
        call.enqueue(new Callback<PoiApi>() {
            @Override
            public void onResponse(Call<PoiApi> call, Response<PoiApi> response) {
                if (response.body() == null) {
                    taCallback.onFailure(new Throwable("body is null"));
                    return;
                }

                taCallback.onResponse(new Poi(response.body()));
            }

            @Override
            public void onFailure(Call<PoiApi> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }
}

