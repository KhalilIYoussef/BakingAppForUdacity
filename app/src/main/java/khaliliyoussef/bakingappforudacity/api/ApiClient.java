package khaliliyoussef.bakingappforudacity.api;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient()
    {
        // add a Facebook StethoInterceptor to the OkHttpClient's list of network interceptors
       OkHttpClient mOkHttpClient= new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(mOkHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
