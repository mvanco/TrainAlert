package cz.intesys.trainalert.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TaServerApi {
    @GET ("/TrainAlert/GetLocation")
    Call<LocationAPI> getLocation();

    @GET ("/TrainAlert/GetPois")
    Call<PoisApi> getPois();
}
