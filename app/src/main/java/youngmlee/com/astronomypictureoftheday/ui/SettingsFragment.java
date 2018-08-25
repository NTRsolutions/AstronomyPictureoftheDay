package youngmlee.com.astronomypictureoftheday.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.Menu;
import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.service.AppJobHandler;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectActionBar();
    }

    private void connectActionBar(){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayUseLogoEnabled(false);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        Log.d("SHARED PREF", "onSharedPrefreferenceChanged called");
        if(key.equals(getString(R.string.set_automatic_wallpaper_key))){
            boolean on = sharedPreferences.getBoolean(key, true);
            android.support.v7.preference.Preference preference = findPreference(key);

            if(on) {
                preference.setSummary(R.string.set_automatic_wallpaper_summary_on);
                AppJobHandler.scheduleAutomaticWallpaperJob(getContext());
            } else {
                preference.setSummary(R.string.set_automatic_wallpaper_summary_off);
                AppJobHandler.cancelAutomaticWallpaperJob(getContext());
            }
        }
        else if(key.equals(getString(R.string.notifications_key))){
            boolean on = sharedPreferences.getBoolean(key, true);
            android.support.v7.preference.Preference preference = findPreference(key);

            if(on) {
                preference.setSummary(R.string.notifications_summary_on);
                AppJobHandler.scheduleNotifyLatestJob(getContext());
            } else {
                preference.setSummary(R.string.notifications_summary_off);
                AppJobHandler.cancelNotifyLatestJobs(getContext());
            }
        }
    }
}
