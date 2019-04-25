package prabhalab.client.location;

public class ApiUtils {


    public static final String BASE_URL = "http://staging-driver-go-api-lp.lynk.co.in";

    public static RetrofitEndpoint getSOService() {
        return RetrofitClient.getClient(BASE_URL).create(RetrofitEndpoint.class);
    }
}
