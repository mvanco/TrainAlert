package cz.intesys.trainalert.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaServerApi {
    @GET ("/TrainAlert/GetLocation")
    Call<LocationAPI> getLocation();

    @GET ("/TrainAlert/GetPois")
    Call<PoisApi> getPois();

    @POST ("/TrainAlert/AddPoi")
    Call<PoiApi> addPoi(@Body PoiApi poiApi);

    @PUT ("/TrainAlert/EditPoi/{id}")
    Call<PoiApi> editPoi(@Path ("id") long id, @Body PoiApi poiApi);
}
