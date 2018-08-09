package youngmlee.com.astronomypictureoftheday.di;

import android.arch.persistence.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import youngmlee.com.astronomypictureoftheday.domain.Repository;
import youngmlee.com.astronomypictureoftheday.domain.database.AppDao;
import youngmlee.com.astronomypictureoftheday.domain.database.AppDatabase;
import youngmlee.com.astronomypictureoftheday.network.RetrofitApi;


@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public Repository provideRepository(RetrofitApi retrofitApi, AppDao appDao){
        Repository repository = new Repository(retrofitApi, appDao);
        return repository;
    }

}
