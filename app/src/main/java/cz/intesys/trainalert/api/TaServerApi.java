package cz.intesys.trainalert.api;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TaServerApi {
    @GET("/Location/GetLocation")
    Call<ResponseApi<LocationApi>> getLocation();

    @GET("/Poi/GetPois")
    Call<ResponseApi<PoisApi>> getPois();

    @POST("/Poi/AddPoi")
    Call<ResponseApi<PoiApi>> addPoi(@Body PoiApi poiApi);

    @PUT("/Poi/EditPoi/{id}")
    Call<ResponseApi<PoiApi>> editPoi(@Path("id") long id, @Body PoiApi poiApi);

    @GET("/Poi/DeletePoi/{id}")
    Call<ResponseApi<PoiApi>> deletePoi(@Path("id") long id);

    @GET("/Trip/GetTrips")
    Call<ResponseApi<List<String>>> getTrips();

    @POST("/Trip/SetTrip/{tripId}")
    Call<ResponseApi<Void>> setTrip(@Path("tripId") String id);

    @GET("/Trip/GetPreviousStops/{count}")
    Call<ResponseApi<List<StopApi>>> getPreviousStops(@Path("count") int count);

    @GET("/Trip/GetNextStops/{count}")
    Call<ResponseApi<List<StopApi>>> getNextStops(@Path("count") int count);

    @GET("/Trip/GetFinalStop")
    Call<ResponseApi<StopApi>> getFinalStop();

    @GET("/Vehicle/GetTrainId")
    Call<ResponseApi<String>> getTrainId();

    @GET("/Trip/ShouldStop")
    Call<ResponseApi<Boolean>> shouldStop();
}
