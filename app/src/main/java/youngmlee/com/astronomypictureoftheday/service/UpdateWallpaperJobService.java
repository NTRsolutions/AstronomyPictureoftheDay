package youngmlee.com.astronomypictureoftheday.service;

import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import youngmlee.com.astronomypictureoftheday.di.DaggerServiceComponent;
import youngmlee.com.astronomypictureoftheday.di.NetworkModule;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.network.RetrofitService;

public class UpdateWallpaperJobService extends JobService{

    @Inject
    public RetrofitService retrofitService;

    @Override
    public boolean onStartJob(JobParameters job) {
        DaggerServiceComponent.builder()
                .networkModule(new NetworkModule())
                .build()
                .inject(this);

        Single<Picture> latestPicture = retrofitService.getLatestPicture();
        latestPicture
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .retry(2)
                .subscribe(new SingleObserver<Picture>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d("Rx", "AutomaticWallpaper onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(Picture picture) {
                        Log.d("Rx", "AutomaticWallpaper onSuccess: ");
                        if (picture.getMediaType().equals("image")) {
                            updateWallpaper(picture);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Rx", "Notification onError: ");
                    }
                });
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }

    private void updateWallpaper(Picture picture){
        Picasso.get().load(picture.getUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                try {
                    WallpaperManager.getInstance(getApplicationContext())
                            .setBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }
}
