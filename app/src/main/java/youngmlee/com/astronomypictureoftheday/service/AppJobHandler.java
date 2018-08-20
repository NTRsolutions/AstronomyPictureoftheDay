package youngmlee.com.astronomypictureoftheday.service;

import android.content.Context;
import android.util.Log;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

import java.util.concurrent.TimeUnit;

public class AppJobHandler {

    private static final String TAG_NOTIFY_LATEST_JOB= "tag_notify_latest_job";
    private static final String TAG_AUTOMATIC_WALLPAPER_JOB = "tag_automatic_wallpaper_job";

    public static void scheduleNotifyLatestJob(Context context){
        int windowStart = (int) TimeUnit.HOURS.toSeconds(24);
        int windowEnd = (int) TimeUnit.HOURS.toSeconds(25);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job notifyLatestJob = dispatcher.newJobBuilder()
                .setService(NotifyLatestJobService.class)
                .setTag(TAG_NOTIFY_LATEST_JOB)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(windowStart, windowEnd))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        dispatcher.mustSchedule(notifyLatestJob);

        Log.d("SERVICETEST", "notify latest job scheduled");
    }

    public static void cancelNotifyLatestJobs(Context context){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(TAG_NOTIFY_LATEST_JOB);

        Log.d("SERVICETEST", "notify latest job canceled");
    }

    public static void scheduleAutomaticWallpaperJob(Context context){
        int windowStart = (int) TimeUnit.HOURS.toSeconds(24);
        int windowEnd = (int) TimeUnit.HOURS.toSeconds(25);

        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Job notifyLatestJob = dispatcher.newJobBuilder()
                .setService(UpdateWallpaperJobService.class)
                .setTag(TAG_AUTOMATIC_WALLPAPER_JOB)
                .setRecurring(true)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(windowStart, windowEnd))
                .setReplaceCurrent(true)
                .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                .build();
        dispatcher.mustSchedule(notifyLatestJob);

        Log.d("SERVICETEST", "automatic wallpaper job scheduled");
    }

    public static void cancelAutomaticWallpaperJob(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        dispatcher.cancel(TAG_AUTOMATIC_WALLPAPER_JOB);

        Log.d("SERVICETEST", "automatic wallpaper job canceled");
    }
}
