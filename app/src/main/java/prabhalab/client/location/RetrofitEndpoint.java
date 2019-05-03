package prabhalab.client.location;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface RetrofitEndpoint {

    @Headers("Content-Type: application/json")
    @POST("/test-driver-tracking/")
    Call<String> sendLocationToServer(@Body RequestBody jsonObject);
}