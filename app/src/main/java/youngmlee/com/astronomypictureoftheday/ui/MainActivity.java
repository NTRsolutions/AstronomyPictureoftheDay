package youngmlee.com.astronomypictureoftheday.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.service.AppJobHandler;
import youngmlee.com.astronomypictureoftheday.viewModel.SharedViewModel;
import youngmlee.com.astronomypictureoftheday.viewModel.ViewModelCallbacks;

public class MainActivity extends AppCompatActivity implements FragmentChangeListener{

    private static final String TAG_SPLASH_SCREEN_FRAGMENT = "tag_start_screen_fragment";
    private static final String TAG_PICTURE_LIST_FRAGMENT = "tag_picture_list_fragment";
    private static final String TAG_VIEW_PAGER_FRAGMENT = "tag_view_pager_fragment";
    private static final String TAG_FULL_SCREEN_FRAGMENT = "tag_full_screen_fragment";
    private static final String TAG_SETTINGS_FRAGMENT = "tag_settings_fragment";


    private SharedViewModel mSharedViewModel;
    private android.support.v7.widget.Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        boolean firstLaunch = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.first_launch_key), true);
        if(firstLaunch){
            AppJobHandler.scheduleNotifyLatestJob(getApplicationContext());
            AppJobHandler.scheduleAutomaticWallpaperJob(getApplicationContext());

            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(getString(R.string.first_launch_key), false)
                    .apply();

        }


        MobileAds.initialize(
                this,
                "ca-app-pub-3940256099942544~3347511713");

        mToolbar = findViewById(R.id.tb_main_activity);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings: {
                attachSettingsFragment();
            }
        }
        return false;
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
                Toast.makeText(getApplicationContext(), "Failed to load, Please check your internet connection", Toast.LENGTH_LONG)
                        .show();
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

    private void attachSettingsFragment(){
        SettingsFragment settingsFragment = new SettingsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,
                        settingsFragment,
                        SettingsFragment.class.getSimpleName())
                .addToBackStack(TAG_SETTINGS_FRAGMENT)
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
