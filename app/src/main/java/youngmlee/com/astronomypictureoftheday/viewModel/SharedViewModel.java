package youngmlee.com.astronomypictureoftheday.viewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import java.util.List;
import javax.inject.Inject;
import youngmlee.com.astronomypictureoftheday.di.AppModule;

import youngmlee.com.astronomypictureoftheday.di.DaggerAppComponent;
import youngmlee.com.astronomypictureoftheday.di.DatabaseModule;
import youngmlee.com.astronomypictureoftheday.di.NetworkModule;
import youngmlee.com.astronomypictureoftheday.di.RepositoryModule;
import youngmlee.com.astronomypictureoftheday.domain.Repository;
import youngmlee.com.astronomypictureoftheday.domain.RepositoryCallbacks;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;


public class SharedViewModel extends AndroidViewModel{

    @Inject
    public Repository repository;
    private LiveData<List<Picture>> mPictureList;
    private int mCurrentPosition;
    private boolean mHasAccessedViewPager;

    public SharedViewModel(Application application){
        super(application);
        DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .databaseModule(new DatabaseModule())
                .networkModule(new NetworkModule())
                .repositoryModule(new RepositoryModule())
                .build()
                .inject(this);

        repository.clearDatabase();
    }

    public LiveData<List<Picture>> getPictureList() {
        if(mPictureList == null){
            mPictureList = new MutableLiveData<>();
            mPictureList = repository.getPictureList();
        }
        return mPictureList;
    }


    public int getCurrentPosition(){
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition){
        mCurrentPosition = currentPosition;
    }

    public boolean hasAccessedViewPager() {
        return mHasAccessedViewPager;
    }

    public void setHasAccessedViewPager(boolean mHasAccessedViewPager) {
        this.mHasAccessedViewPager = mHasAccessedViewPager;
    }

    public void loadInitialData(final ViewModelCallbacks viewModelCallbacks){
        Log.d("FLOW TEST", " LOAD DATA IN VM CALLED");
        repository.retrieveLatestData(new RepositoryCallbacks() {
            @Override
            public void onResponse(String message) {
                Log.d("FLOW TEST", "ON RESPONSE RECEIVED IN VIEWMODEL: " + message);
                viewModelCallbacks.onResponse(message);

            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("FLOW TEST", "ON FAILURE RECEIVED IN VIEWMODEL: " + throwable.getMessage());
                throwable.printStackTrace();
                viewModelCallbacks.onFailure(throwable);

            }
        });
    }

    public void loadMoreData(String lastVisibleDate){
        repository.loadMoreData(lastVisibleDate);
    }

}
