package cz.intesys.trainalert.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaServerApi {
    @GET ("/TrainAlert/GetLocation")
    Call<ResponseApi<LocationApi>> getLocation();

    @GET ("/TrainAlert/GetPois")
    Call<ResponseApi<PoisApi>> getPois();

    @POST ("/TrainAlert/AddPoi")
    Call<ResponseApi<PoiApi>> addPoi(@Body PoiApi poiApi);

    @PUT ("/TrainAlert/EditPoi/{id}")
    Call<ResponseApi<PoiApi>> editPoi(@Path("id") long id, @Body PoiApi poiApi);

    @GET("/TrainAlert/DeletePoi/{id}")
    Call<ResponseApi<PoiApi>> deletePoi(@Path("id") long id);
}
