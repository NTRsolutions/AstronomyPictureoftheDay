package youngmlee.com.astronomypictureoftheday.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.utils.NetworkUtil;

public class PictureFullScreenFragment extends Fragment {

    public static final String EXTRA_KEY_IMAGE_URL = "extra_key_image_url";

    private PhotoView mFullScreenPhotoView;
    private String mImageUrl;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_fullscreen, container, false);
        mFullScreenPhotoView = (PhotoView) view.findViewById(R.id.pv_picture_fullscreen);

        if(getArguments() != null){
            mImageUrl = getArguments().getString(EXTRA_KEY_IMAGE_URL);
            mImageUrl = NetworkUtil.validateUrl(mImageUrl);
        }
        Glide.with(this).load(mImageUrl).into(mFullScreenPhotoView);
        return view;
    }
}
