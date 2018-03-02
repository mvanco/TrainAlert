package cz.intesys.trainalert.rest;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.repository.PostgreSqlRepository;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static cz.intesys.trainalert.TaConfig.BASIC_DATE_FORMAT_STRING;

public class TaClient {
    private static volatile Retrofit sRetrofit;


    public TaClient() {
    }


    public static <T> T createService(Class<T> service) {
        return getRetrofit().create(service);
    }


    public static Retrofit getRetrofit() {
        if (sRetrofit == null) {
            synchronized (PostgreSqlRepository.class) {
                if (sRetrofit == null) {
                    sRetrofit = buildRetrofit();
                }
            }
        }
        return sRetrofit;
    }


    private static Retrofit buildRetrofit() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(TaConfig.REST_BASE_URL);
        builder.client(buildClient());
        builder.addConverterFactory(createConverterFactory());
        builder.addCallAdapterFactory(createCallAdapterFactory());
        return builder.build();
    }


    private static OkHttpClient buildClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(30, TimeUnit.SECONDS);
        builder.readTimeout(30, TimeUnit.SECONDS);
        builder.writeTimeout(30, TimeUnit.SECONDS);
        builder.addNetworkInterceptor(createLoggingInterceptor());
        return builder.build();
    }

    private static Interceptor createLoggingInterceptor() {
        HttpLoggingInterceptor.Logger logger = new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("intercepter", message);
            }
        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(logger);
        interceptor.setLevel(TaConfig.LOGS ? HttpLoggingInterceptor.Level.BASIC : HttpLoggingInterceptor.Level.NONE);
        return interceptor;
    }

    /**
     * Enable Retrofit to convert data from JSON format to Java object
     *
     * @return
     */
    private static Converter.Factory createConverterFactory() {
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat(BASIC_DATE_FORMAT_STRING);
        Gson gson = builder.create();
        return GsonConverterFactory.create(gson);
    }

    private static CallAdapter.Factory createCallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }
}
