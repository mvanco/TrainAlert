package cz.intesys.trainalert.api;

import retrofit2.Call;
import retrofit2.http.GET;

public interface TAServerAPI {
    @GET ("/TrainAlert/GetLocation")
    Call<LocationAPI> getLocation();
}
