package youngmlee.com.astronomypictureoftheday.domain;

import android.arch.lifecycle.LiveData;
import android.util.Log;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
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

    public void retrieveLatestData(final RepositoryCallbacks repositoryCallbacks){
        Log.d("RX", "retrieveDataWithRX called");
         Single<List<Picture>> observablePictureList =
                 retrofitService.getLatestPicture()
                 .flatMap(new Function<Picture, SingleSource<List<Picture>>>() {
                     @Override
                     public SingleSource<List<Picture>> apply(Picture picture) {
                         String endDate = picture.getDate();
                         String startDate = DateUtil.subtractDays(endDate, 25);
                         return retrofitService.getPicturesFromDateRange(startDate, endDate);
                     }
                 })
                 .flatMap(new Function<List<Picture>, SingleSource<List<Picture>>>() {
                     @Override
                     public SingleSource<List<Picture>> apply(List<Picture> pictureList) {
                         List<Picture> processedPictureList = processPictures(pictureList);
                         return Single.just(processedPictureList);
                     }
                 });

         observablePictureList
                 .subscribeOn(Schedulers.io())
                 .observeOn(Schedulers.io())
                 .retry(2)
                 .subscribe(new SingleObserver<List<Picture>>() {
                     @Override
                     public void onSubscribe(Disposable d) {
                         Log.d("RX", "onSubscribe: " + d.toString());
                     }

                     @Override
                     public void onSuccess(List<Picture> pictureList) {
                         Log.d("RX", "onSuccess: " + pictureList.get(0).getDate() + " " + pictureList.get(pictureList.size()-1).getDate());
                         appDao.insertAll(pictureList);
                         repositoryCallbacks.onResponse("success");
                     }

                     @Override
                     public void onError(Throwable e) {
                         Log.d("RX", "onSubscribe: " + e.getMessage());
                         repositoryCallbacks.onFailure(e);
                     }
                 });

    }

    public void loadMoreData(String lastVisibleDate){
        String endDate = lastVisibleDate;
        String startDate = DateUtil.subtractDays(endDate, 15);

        Single<List<Picture>> pictureListSingle =
                retrofitService.getPicturesFromDateRange(startDate, endDate)
                .flatMap(new Function<List<Picture>, SingleSource<? extends List<Picture>>>() {
                    @Override
                    public SingleSource<? extends List<Picture>> apply(List<Picture> pictureList) {
                        List<Picture> processedList = processPictures(pictureList);
                        return Single.just(processedList);
                    }
                });

        pictureListSingle
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .retry(2)
                .subscribe(new SingleObserver<List<Picture>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("RX", "onSubscribe: " + d.toString());
                    }

                    @Override
                    public void onSuccess(List<Picture> pictureList) {
                        Log.d("RX", "onSuccess: " + pictureList.get(0).getDate() + " " + pictureList.get(pictureList.size()-1).getDate());
                        appDao.insertAll(pictureList);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("RX", "onSubscribe: " + e.getMessage());
                    }
                });
    }

    private List<Picture> processPictures(List<Picture> pictures){
        Collections.reverse(pictures);
        for (Iterator<Picture> iterator = pictures.iterator(); iterator.hasNext();) {
            Picture picture = iterator.next();
            if(picture.getMediaType().equals("video")){
                iterator.remove();
            }
        }
        return pictures;
    }

    public void clearDatabase() {
        Completable.fromAction(new Action() {
            @Override
            public void run() {
                appDao.deleteAll();
            }
        })
                .subscribeOn(Schedulers.io())
                .subscribe();
    }

}
