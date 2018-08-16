package youngmlee.com.astronomypictureoftheday.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.widget.ImageView;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.google.android.gms.ads.MobileAds;

import java.util.concurrent.TimeUnit;

import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.network.NotifyLatestJobService;
import youngmlee.com.astronomypictureoftheday.viewModel.SharedViewModel;
import youngmlee.com.astronomypictureoftheday.viewModel.ViewModelCallbacks;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener{

    private static final String TAG_SPLASH_SCREEN_FRAGMENT = "tag_start_screen_fragment";
    private static final String TAG_PICTURE_LIST_FRAGMENT = "tag_picture_list_fragment";
    private static final String TAG_VIEW_PAGER_FRAGMENT = "tag_view_pager_fragment";
    private static final String TAG_FULL_SCREEN_FRAGMENT = "tag_full_screen_fragment";
    private static final String TAG_NOTIFY_LATEST_JOB= "tag_notify_latest_job";

    private SharedViewModel mSharedViewModel;
    private android.support.v7.widget.Toolbar mToolbar;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSharedPreferences = getSharedPreferences(
                getString(R.string.shared_preferences_key),
                Context.MODE_PRIVATE);


        boolean jobScheduled = mSharedPreferences.getBoolean(getString(R.string.scheduled_job_key), false);
        Log.d("Job TEST", "" + jobScheduled);

        if(!jobScheduled) {
            Log.d("Job TEST", "JobScheduled");
            scheduleNotifyLatestJob();

            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(getString(R.string.scheduled_job_key), true);
            editor.apply();
        }
        //cancelAllJobs();

        MobileAds.initialize(
                this,
                "ca-app-pub-3940256099942544~3347511713");

        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.tb_main_activity);
        setSupportActionBar(mToolbar);

        mSharedViewModel = ViewModelProviders.of(this).get(SharedViewModel.class);

        if (findViewById(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return;
            }
        }

        loadInitialData();
        loadInitialFragment();

    }

    private void scheduleNotifyLatestJob(){
        int windowStart = (int) TimeUnit.HOURS.toSeconds(24);
        int windowEnd = (int) TimeUnit.HOURS.toSeconds(25);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
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
    }

    private void cancelAllJobs(){
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        dispatcher.cancelAll();

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(getString(R.string.scheduled_job_key), false);
        editor.apply();
    }

    private void loadInitialData(){
        mSharedViewModel.loadInitialData(new ViewModelCallbacks() {
            @Override
            public void onResponse(String message) {
                attachPictureListFragment();
                Log.d("FLOW TEST", "ON RESPONSE RECEIVED IN MAINACTIVITY: " + message);
            }

            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                Log.d("FLOW TEST", "ON FAILURE RECEIVED IN MAINACTIVITY: " + throwable.getMessage());
            }
        });
    }

    private void loadInitialFragment(){
        SplashScreenFragment startScreenFragment = new SplashScreenFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, startScreenFragment, TAG_SPLASH_SCREEN_FRAGMENT)
                .commit();
    }

    @Override
    public void attachDetailViewPager(int clickedPosition, ImageView sharedImageView) {
        mSharedViewModel.setCurrentPosition(clickedPosition);
        mSharedViewModel.setHasAccessedViewPager(true);
        PictureDetailViewPager pictureDetailViewPager = new PictureDetailViewPager();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container,
                        pictureDetailViewPager,
                        PictureDetailViewPager.class.getSimpleName())
                .addToBackStack(TAG_VIEW_PAGER_FRAGMENT)
                .commit();
    }

    @Override
    public void attachFullScreenFragment(String url) {
        PictureFullScreenFragment pictureFullScreenFragment = new PictureFullScreenFragment();
        pictureFullScreenFragment.setEnterTransition(new Fade());
        Bundle args = new Bundle();
        args.putString(PictureFullScreenFragment.EXTRA_KEY_IMAGE_URL, url);
        pictureFullScreenFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        pictureFullScreenFragment,
                        PictureFullScreenFragment.class.getSimpleName())
                .addToBackStack(TAG_FULL_SCREEN_FRAGMENT)
                .commit();
    }

    private void attachPictureListFragment() {
        PictureListFragment pictureListFragment = new PictureListFragment();
        pictureListFragment.setEnterTransition(new Fade());
        Bundle args = new Bundle();
        pictureListFragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        pictureListFragment,
                        PictureListFragment.class.getSimpleName())
                .addToBackStack(TAG_PICTURE_LIST_FRAGMENT)
                .commit();
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return false;
    }

    @Override
    public void onBackPressed() {
        Log.d("BACKSTACKTEST", ""+ getSupportFragmentManager().getBackStackEntryCount());
        if(getSupportFragmentManager().getBackStackEntryCount() == 1){
            finish();
        }else{
            super.onBackPressed();
        }
    }

}
