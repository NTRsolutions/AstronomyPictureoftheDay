package youngmlee.com.astronomypictureoftheday.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.di.DaggerServiceComponent;
import youngmlee.com.astronomypictureoftheday.di.NetworkModule;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.network.RetrofitService;
import youngmlee.com.astronomypictureoftheday.ui.MainActivity;

public class NotifyLatestJobService extends JobService{

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
                        Log.d("Rx", "Notification onSubscribe: ");
                    }

                    @Override
                    public void onSuccess(Picture picture) {
                        Log.d("Rx", "Notification onSuccess: ");
                        if (picture.getMediaType().equals("image")) {
                            showNotification(picture);
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_id = getApplication().getString(R.string.channel_id);
            CharSequence name = getApplication().getString(R.string.channel_name);
            String description = getApplication().getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channel_id, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplication().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification(Picture picture){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Log.d("NOTIFICATION TEST", "NOTIFYING:" + picture.getTitle());

        createNotificationChannel();

        String channel_id = getApplication().getString(R.string.channel_id);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplication(), channel_id)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(picture.getTitle())
                .setContentText(picture.getExplanation())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplication());
        notificationManager.notify(0, mBuilder.build());

    }
}
