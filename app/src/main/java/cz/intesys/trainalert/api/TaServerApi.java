package cz.intesys.trainalert.api;

import java.util.List;

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

    @GET("/TrainAlert/GetTrips/{trainId}")
    Call<ResponseApi<List<Integer>>> getTrips(@Path("trainId") int id);

    @GET("/TrainAlert/SetTrip/{tripId}")
    Call<ResponseApi<Void>> setTrip(@Path("tripId") int id);

    @GET("/TrainAlert/GetPreviousStops/{count}")
    Call<ResponseApi<List<StopApi>>> getPreviousStops(@Path("count") int id);

    @GET("/TrainAlert/GetNextStops/{count}")
    Call<ResponseApi<List<StopApi>>> getNextStops(@Path("count") int id);

    @GET("/TrainAlert/GetFinalStop")
    Call<ResponseApi<StopApi>> getFinalStop();
}
