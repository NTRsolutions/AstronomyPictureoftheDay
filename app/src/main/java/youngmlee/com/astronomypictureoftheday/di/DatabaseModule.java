package youngmlee.com.astronomypictureoftheday.di;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import youngmlee.com.astronomypictureoftheday.domain.database.AppDao;
import youngmlee.com.astronomypictureoftheday.domain.database.AppDatabase;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(Application application){
        AppDatabase appDatabase = Room.databaseBuilder(application,
                AppDatabase.class,
                "app_database")
                .build();
        return appDatabase;
    }

    @Provides
    @Singleton
    AppDao provideAppDao(AppDatabase appDatabase){
        return appDatabase.appDao();
    }
}
