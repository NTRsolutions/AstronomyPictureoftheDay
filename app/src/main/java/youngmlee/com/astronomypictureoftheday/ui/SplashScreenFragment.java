package youngmlee.com.astronomypictureoftheday.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import youngmlee.com.astronomypictureoftheday.R;

public class SplashScreenFragment extends Fragment {

    private ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash_screen, container, false);

        mProgressBar = (ProgressBar) view.findViewById(R.id.pb_start_screen);

        return view;
    }


}
