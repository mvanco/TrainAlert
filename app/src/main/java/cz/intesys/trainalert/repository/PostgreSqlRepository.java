package cz.intesys.trainalert.repository;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.api.LocationApi;
import cz.intesys.trainalert.api.PoiApi;
import cz.intesys.trainalert.api.PoisApi;
import cz.intesys.trainalert.api.ResponseApi;
import cz.intesys.trainalert.api.StopApi;
import cz.intesys.trainalert.api.TaServerApi;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.Stop;
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
    public static String LOG_POSTGRE = "postgre";
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
        Call<ResponseApi<LocationApi>> call = mApiService.getLocation();
        call.enqueue(new Callback<ResponseApi<LocationApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<LocationApi>> call, Response<ResponseApi<LocationApi>> response) {
                if (response.body() == null) {
                    return;
                }

                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) {
//                    Log.e("testorder", "onResponse() id:" + response.body().getData().getId());
                    taCallback.onResponse(new Location(response.body().getData()));
                } else {
                    taCallback.onFailure(new Throwable());
                }
                Log.d(LOG_POSTGRE, "getCurrentLocation response");
            }

            @Override
            public void onFailure(Call<ResponseApi<LocationApi>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "getCurrentLocation failure");
            }
        });
        Log.d(LOG_POSTGRE, "getCurrentLocation enqueued");
    }

    @Override
    public void getPois(TaCallback<List<Poi>> taCallback) {
        Call<ResponseApi<PoisApi>> call = mApiService.getPois();
        call.enqueue(new Callback<ResponseApi<PoisApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<PoisApi>> call, Response<ResponseApi<PoisApi>> response) {
                Log.d(LOG_POSTGRE, "getPois response");
                if (response.body() == null || response.body().getData() == null || response.body().getData().getPois() == null) {
                    return;
                }

                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) {
                    List<Poi> pois = new ArrayList<>();
                    for (PoiApi poiApi : response.body().getData().getPois()) {
                        pois.add(new Poi(poiApi));
                    }
                    taCallback.onResponse(pois);
                } else {
                    taCallback.onFailure(new Throwable());
                    Log.d(LOG_POSTGRE, "getPois failure");
                }
            }

            @Override
            public void onFailure(Call<ResponseApi<PoisApi>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
        Log.d(LOG_POSTGRE, "getPois enqueued");
    }

    @Override
    public void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        Call<ResponseApi<PoiApi>> call = mApiService.addPoi(new PoiApi(poi));
        call.enqueue(new Callback<ResponseApi<PoiApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<PoiApi>> call, Response<ResponseApi<PoiApi>> response) {
                Log.d(LOG_POSTGRE, "addPoi response");
                if (response.body() == null) {
                    taCallback.onFailure(new Throwable("body is null"));
                    return;
                }

                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) {
                    taCallback.onResponse(new Poi(response.body().getData()));
                } else {
                    taCallback.onFailure(new Throwable());
                }
            }

            @Override
            public void onFailure(Call<ResponseApi<PoiApi>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "addPoi failure");
            }
        });
        Log.d(LOG_POSTGRE, "addPoi enqueued");
    }

    @Override
    public void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        Call<ResponseApi<PoiApi>> call = mApiService.editPoi(id, new PoiApi(poi));
        call.enqueue(new Callback<ResponseApi<PoiApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<PoiApi>> call, Response<ResponseApi<PoiApi>> response) {
                Log.d(LOG_POSTGRE, "editPoi response");
                if (response.body() == null) {
                    taCallback.onFailure(new Throwable("body is null"));
                    return;
                }

                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) {
                    taCallback.onResponse(new Poi(response.body().getData()));
                } else {
                    taCallback.onFailure(new Throwable());
                }
            }

            @Override public void onFailure(Call<ResponseApi<PoiApi>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "editPoi failure");
            }
        });
        Log.d(LOG_POSTGRE, "editPoi enqueued");
    }

    public void deletePoi(long id, TaCallback<Poi> taCallback) {
        Call<ResponseApi<PoiApi>> call = mApiService.deletePoi(id);
        call.enqueue(new Callback<ResponseApi<PoiApi>>() {

            @Override
            public void onResponse(Call<ResponseApi<PoiApi>> call, Response<ResponseApi<PoiApi>> response) {
                Log.d(LOG_POSTGRE, "deletePoi response");
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    taCallback.onResponse(new Poi(response.body().getData()));
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<PoiApi>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "deletePoi failure");
            }
        });
        Log.d(LOG_POSTGRE, "deletePoi enqueued");
    }

    @Override public void getTrips(String id, TaCallback<List<String>> taCallback) {
        Call<ResponseApi<List<String>>> call = mApiService.getTrips();
        call.enqueue(new Callback<ResponseApi<List<String>>>() {

            @Override
            public void onResponse(Call<ResponseApi<List<String>>> call, Response<ResponseApi<List<String>>> response) {
                Log.d(LOG_POSTGRE, "getTrips response");
                if (response.body() != null && response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    taCallback.onResponse(response.body().getData());
                } else {
                    taCallback.onFailure(new Throwable());
                }
            }

            @Override public void onFailure(Call<ResponseApi<List<String>>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "getTrips failure");
            }
        });
        Log.d(LOG_POSTGRE, "getTrips enqueued");
    }

    @Override public void setTrip(String id, TaCallback<Void> taCallback) {
        Call<ResponseApi<Void>> call = mApiService.setTrip(id);
        call.enqueue(new Callback<ResponseApi<Void>>() {

            @Override
            public void onResponse(Call<ResponseApi<Void>> call, Response<ResponseApi<Void>> response) {
                Log.d(LOG_POSTGRE, "setTrip response");
                if (response.body() != null && response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    taCallback.onResponse(null); // Only call function is enough to inform about completition without error
                } else {
                    taCallback.onFailure(new Throwable());
                }
            }

            @Override public void onFailure(Call<ResponseApi<Void>> call, Throwable t) {
                Log.d(LOG_POSTGRE, "setTrip failure");
                taCallback.onFailure(t);
            }
        });
        Log.d(LOG_POSTGRE, "setTrip enqueued");
    }

    @Override public void getPreviousStops(int count, TaCallback<List<Stop>> taCallback) {
        Call<ResponseApi<List<StopApi>>> call = mApiService.getPreviousStops(count);
        call.enqueue(new Callback<ResponseApi<List<StopApi>>>() {

            @Override
            public void onResponse(Call<ResponseApi<List<StopApi>>> call, Response<ResponseApi<List<StopApi>>> response) {
                Log.d(LOG_POSTGRE, "getPreviousStops response");
                if (response.body() != null
                        && response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    List<Stop> stops = new ArrayList<>();
                    for (StopApi stopApi : response.body().getData()) {
                        stops.add(new Stop(stopApi));
                    }
                    taCallback.onResponse(stops); // Only call function is enough to inform about completition without error
                } else {
                    taCallback.onFailure(new Throwable());
                }
            }

            @Override public void onFailure(Call<ResponseApi<List<StopApi>>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "getPreviousStops failure");
            }
        });
        Log.d(LOG_POSTGRE, "getPreviousStops enqueued");
    }

    @Override public void getNextStops(int count, TaCallback<List<Stop>> taCallback) {
        Call<ResponseApi<List<StopApi>>> call = mApiService.getNextStops(count);
        call.enqueue(new Callback<ResponseApi<List<StopApi>>>() {

            @Override
            public void onResponse(Call<ResponseApi<List<StopApi>>> call, Response<ResponseApi<List<StopApi>>> response) {
                Log.d(LOG_POSTGRE, "getNextStops response");
                if (response.body() != null
                        && response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    List<Stop> stops = new ArrayList<>();
                    for (StopApi stopApi : response.body().getData()) {
                        stops.add(new Stop(stopApi));
                    }
                    taCallback.onResponse(stops); // Only call function is enough to inform about completition without error
                } else {
                    taCallback.onFailure(new Throwable());
                }
            }

            @Override public void onFailure(Call<ResponseApi<List<StopApi>>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "getNextStops failure");
            }
        });
        Log.d(LOG_POSTGRE, "getNextStops enqueued");
    }

    @Override public void getFinalStop(TaCallback<Stop> taCallback) {
        Call<ResponseApi<StopApi>> call = mApiService.getFinalStop();
        call.enqueue(new Callback<ResponseApi<StopApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<StopApi>> call, Response<ResponseApi<StopApi>> response) {
                if (response.body() != null
                        && response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    if (response.body().getData() == null) {
                        taCallback.onResponse(null);
                    } else {
                        taCallback.onResponse(new Stop(response.body().getData())); // Only call function is enough to inform about completition without error
                    }
                } else {
                    taCallback.onFailure(new Throwable());
                }
                Log.d(LOG_POSTGRE, "getFinalStop response");
            }

            @Override public void onFailure(Call<ResponseApi<StopApi>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "getFinalStop failure");
            }
        });
        Log.d(LOG_POSTGRE, "getFinalStop enqueued");
    }

    @Override public void getTrainId(TaCallback<String> taCallback) {
        Call<ResponseApi<String>> call = mApiService.getTrainId();
        call.enqueue(new Callback<ResponseApi<String>>() {
            @Override
            public void onResponse(Call<ResponseApi<String>> call, Response<ResponseApi<String>> response) {
                if (response.body() != null
                        && response.body().getErrorCode() == ResponseApi.ECODE_OK
                        && response.body().getData() != null) {
                    taCallback.onResponse(response.body().getData());
                } else {
                    taCallback.onFailure(new Throwable());
                }
                Log.d(LOG_POSTGRE, "getTrainId response");
            }

            @Override public void onFailure(Call<ResponseApi<String>> call, Throwable t) {
                taCallback.onFailure(t);
                Log.d(LOG_POSTGRE, "getTrainId failure");
            }
        });
        Log.d(LOG_POSTGRE, "getTrainId enqueued");
    }

    @Override public void shouldStop(TaCallback<Boolean> taCallback) {
        Call<ResponseApi<Boolean>> call = mApiService.shouldStop();
        call.enqueue(new Callback<ResponseApi<Boolean>>() {
            @Override
            public void onResponse(Call<ResponseApi<Boolean>> call, Response<ResponseApi<Boolean>> response) {
                if (response.body() != null
                        && response.body().getErrorCode() == ResponseApi.ECODE_OK
                        && response.body().getData() != null) {
                    taCallback.onResponse(response.body().getData());
                    Log.d(LOG_POSTGRE, "shouldStop response");
                } else {
                    taCallback.onFailure(new Throwable());
                    Log.d(LOG_POSTGRE, "shouldStop failure");
                }
            }

            @Override public void onFailure(Call<ResponseApi<Boolean>> call, Throwable t) {
                taCallback.onFailure(new Throwable(t));
                Log.d(LOG_POSTGRE, "shouldStop failure");
            }
        });
        Log.d(LOG_POSTGRE, "shouldStop enqueued");
    }


}

