package youngmlee.com.astronomypictureoftheday.di;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import youngmlee.com.astronomypictureoftheday.network.RetrofitService;

@Module
public class NetworkModule {

    @Provides
    @Singleton
    public Gson provideGson(){
        Gson gson = new GsonBuilder().create();
        return gson;
    }

    @Provides
    @Singleton
    public Retrofit provideRetrofit(Gson gson){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.nasa.gov/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        return retrofit;
    }

    @Provides
    @Singleton
    public RetrofitService provideRetrofitApi(Retrofit retrofit){
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        return retrofitService;
    }
}
