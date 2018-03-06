package cz.intesys.trainalert.repository;

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
            }

            @Override
            public void onFailure(Call<ResponseApi<LocationApi>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override
    public void getPois(TaCallback<List<Poi>> taCallback) {
        Call<ResponseApi<PoisApi>> call = mApiService.getPois();
        call.enqueue(new Callback<ResponseApi<PoisApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<PoisApi>> call, Response<ResponseApi<PoisApi>> response) {
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
                }
            }

            @Override
            public void onFailure(Call<ResponseApi<PoisApi>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override
    public void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        Call<ResponseApi<PoiApi>> call = mApiService.addPoi(new PoiApi(poi));
        call.enqueue(new Callback<ResponseApi<PoiApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<PoiApi>> call, Response<ResponseApi<PoiApi>> response) {
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
            }
        });
    }

    @Override
    public void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        Call<ResponseApi<PoiApi>> call = mApiService.editPoi(id, new PoiApi(poi));
        call.enqueue(new Callback<ResponseApi<PoiApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<PoiApi>> call, Response<ResponseApi<PoiApi>> response) {
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
            }
        });
    }

    public void deletePoi(long id, TaCallback<Poi> taCallback) {
        Call<ResponseApi<PoiApi>> call = mApiService.deletePoi(id);
        call.enqueue(new Callback<ResponseApi<PoiApi>>() {

            @Override
            public void onResponse(Call<ResponseApi<PoiApi>> call, Response<ResponseApi<PoiApi>> response) {
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    taCallback.onResponse(new Poi(response.body().getData()));
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<PoiApi>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override public void getTrips(String id, TaCallback<List<String>> taCallback) {
        Call<ResponseApi<List<String>>> call = mApiService.getTrips();
        call.enqueue(new Callback<ResponseApi<List<String>>>() {

            @Override
            public void onResponse(Call<ResponseApi<List<String>>> call, Response<ResponseApi<List<String>>> response) {
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    taCallback.onResponse(response.body().getData());
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<List<String>>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override public void setTrip(String id, TaCallback<Void> taCallback) {
        Call<ResponseApi<Void>> call = mApiService.setTrip(id);
        call.enqueue(new Callback<ResponseApi<Void>>() {

            @Override
            public void onResponse(Call<ResponseApi<Void>> call, Response<ResponseApi<Void>> response) {
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    taCallback.onResponse(null); // Only call function is enough to inform about completition without error
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<Void>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override public void getPreviousStops(int count, TaCallback<List<Stop>> taCallback) {
        Call<ResponseApi<List<StopApi>>> call = mApiService.getPreviousStops(count);
        call.enqueue(new Callback<ResponseApi<List<StopApi>>>() {

            @Override
            public void onResponse(Call<ResponseApi<List<StopApi>>> call, Response<ResponseApi<List<StopApi>>> response) {
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    List<Stop> stops = new ArrayList<>();
                    for (StopApi stopApi : response.body().getData()) {
                        stops.add(new Stop(stopApi));
                    }
                    taCallback.onResponse(stops); // Only call function is enough to inform about completition without error
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<List<StopApi>>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override public void getNextStops(int count, TaCallback<List<Stop>> taCallback) {
        Call<ResponseApi<List<StopApi>>> call = mApiService.getNextStops(count);
        call.enqueue(new Callback<ResponseApi<List<StopApi>>>() {

            @Override
            public void onResponse(Call<ResponseApi<List<StopApi>>> call, Response<ResponseApi<List<StopApi>>> response) {
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    List<Stop> stops = new ArrayList<>();
                    for (StopApi stopApi : response.body().getData()) {
                        stops.add(new Stop(stopApi));
                    }
                    taCallback.onResponse(stops); // Only call function is enough to inform about completition without error
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<List<StopApi>>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override public void getFinalStop(TaCallback<Stop> taCallback) {
        Call<ResponseApi<StopApi>> call = mApiService.getFinalStop();
        call.enqueue(new Callback<ResponseApi<StopApi>>() {
            @Override
            public void onResponse(Call<ResponseApi<StopApi>> call, Response<ResponseApi<StopApi>> response) {
                if (response.body().getErrorCode() == ResponseApi.ECODE_OK) { //TODO: make with enum or annotated int
                    if (response.body().getData() == null) {
                        taCallback.onResponse(null);
                    } else {
                        taCallback.onResponse(new Stop(response.body().getData())); // Only call function is enough to inform about completition without error
                    }
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<StopApi>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }

    @Override public void getTrainId(TaCallback<String> taCallback) {
        Call<ResponseApi<String>> call = mApiService.getTrainId();
        call.enqueue(new Callback<ResponseApi<String>>() {
            @Override
            public void onResponse(Call<ResponseApi<String>> call, Response<ResponseApi<String>> response) {
                if (response.body() != null && response.body().getErrorCode() == ResponseApi.ECODE_OK) {
                    if (response.body().getData() == null) {
                        String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                        taCallback.onFailure(new Throwable(throwableMessage));
                    } else {
                        taCallback.onResponse(response.body().getData());
                    }
                } else {
                    String throwableMessage = "nastala chyba s kodom " + response.body().getErrorCode();
                    taCallback.onFailure(new Throwable(throwableMessage));
                }
            }

            @Override public void onFailure(Call<ResponseApi<String>> call, Throwable t) {
                taCallback.onFailure(t);
            }
        });
    }
}

