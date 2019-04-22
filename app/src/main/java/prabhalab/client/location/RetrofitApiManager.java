package prabhalab.client.location;

import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


/**
 * Created by PrabhagaranR on 20-09-17.
 */

public class RetrofitApiManager {
    private ApiUtils retrofitEndPoint;
    private static RetrofitApiManager instance;
    private static  final String HMAC_SHA1_ALGORITHM = "HMACSHA1";
    public static final String method = "GET";
    Retrofit retrofitEngine;
    public static RetrofitApiManager getInstance() {
        if (instance == null) {
            instance = new RetrofitApiManager();
        }
        return instance;
    }
    private RetrofitApiManager() {
        try {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(interceptor);
            httpClient.connectTimeout(180, TimeUnit.SECONDS);
            httpClient.readTimeout(180, TimeUnit.SECONDS);
            httpClient.writeTimeout(180, TimeUnit.SECONDS);
            httpClient.followRedirects(false);
            ////httpClient.addNetworkInterceptor(new AddHeaderInterceptor());
            retrofitEngine = new Retrofit.Builder().client(httpClient.build())
                    .baseUrl("https://staging-driver-go-api-lp.lynk.co.in/test-driver-tracking")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
            retrofitEndPoint = retrofitEngine.create(ApiUtils.class);

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public ApiUtils getEndPoint() {
        return retrofitEndPoint;
    }



}
