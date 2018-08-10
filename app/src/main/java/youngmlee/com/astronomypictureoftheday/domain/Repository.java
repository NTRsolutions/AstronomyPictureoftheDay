package youngmlee.com.astronomypictureoftheday.domain;

import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import youngmlee.com.astronomypictureoftheday.domain.database.AppDao;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.network.RetrofitService;
import youngmlee.com.astronomypictureoftheday.utils.DateUtil;

public class Repository {

    private RetrofitService retrofitService;
    private AppDao appDao;


    public Repository(RetrofitService retrofitService, AppDao appDao){
        this.retrofitService = retrofitService;
        this.appDao = appDao;
    }

    public LiveData<List<Picture>> getPictureList(){
        return appDao.getAll();
    }

    //First gets latestDate
    //Second gets List using latestDate
    public void retrieveData(final RepositoryCallbacks repositoryCallbacks){
        Call<Picture> call = retrofitService.getLatestPicture();
        call.enqueue(new Callback<Picture>() {
            @Override
            public void onResponse(Call<Picture> call, Response<Picture> response) {
                Log.d("FLOW TEST", "ON RESPONSE RECEIVED IN REPOSITORY GETTING PICTURE: " + response.message() + " code: " + response.code());
                Log.d("IMAGE TYPE", response.body().getUrl());
                String endDate = response.body().getDate();
                String startDate = DateUtil.subtractDays(endDate, 15);
                retrievePictureList(startDate, endDate, repositoryCallbacks);
            }

            @Override
            public void onFailure(Call<Picture> call, Throwable t) {
                Log.d("FLOW TEST", "ON FAILURE RECEIVED IN REPOSITORY GETTING PICTURE: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void retrievePictureList(String startDate, String endDate, final RepositoryCallbacks repositoryCallbacks){
        Call<List<Picture>> call = retrofitService.getPicturesFromDateRange(startDate, endDate);
        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                Log.d("FLOW TEST", "ON RESPONSE RECEIVED IN REPOSITORY GETTING LIST: " + response.message() + " code: " + response.code());
                final List<Picture> pictureList = response.body();
                processPictures(pictureList);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        appDao.insertAll(pictureList);
                    }
                });
                repositoryCallbacks.onResponse(response.message());
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable t) {
                Log.d("FLOW TEST", "ON FAILURE RECEIVED IN REPOSITORY GETTING LIST: " + t.getMessage());
                repositoryCallbacks.onFailure(t);
            }
        });
    }

    public void clearDatabase(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                appDao.deleteAll();
            }
        });
    }


    private void processPictures(List<Picture> pictures){

        Collections.reverse(pictures);
        for (Iterator<Picture> iterator = pictures.iterator(); iterator.hasNext();) {
            Picture picture = iterator.next();
            if(picture.getMediaType().equals("video")){
                iterator.remove();
            }
        }
    }

    public void loadMoreData(String lastVisibleDate){
        String endDate = lastVisibleDate;
        String startDate = DateUtil.subtractDays(endDate, 15);
        Call<List<Picture>> call = retrofitService.getPicturesFromDateRange(startDate, endDate);
        call.enqueue(new Callback<List<Picture>>() {
            @Override
            public void onResponse(Call<List<Picture>> call, Response<List<Picture>> response) {
                Log.d("SCROLLTEST", "MORE DATA ONRESPONSE" + response.code());
                final List<Picture> pictureList = response.body();
                processPictures(pictureList);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        appDao.insertAll(pictureList);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Picture>> call, Throwable t) {

            }
        });
    }

}
