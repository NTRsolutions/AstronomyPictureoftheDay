package youngmlee.com.astronomypictureoftheday.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.List;
import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.utils.ImageUtil;
import youngmlee.com.astronomypictureoftheday.utils.NetworkUtil;
import youngmlee.com.astronomypictureoftheday.viewModel.SharedViewModel;

public class PictureDetailFragment extends Fragment{

    public static final String EXTRA_KEY_PICTURE_POSITION = "extra_key_picture_position";

    private ImageView mDetailPictureImageView;
    private TextView mDescriptionTextView;
    private TextView mTitleTextView;
    private TextView mCopyrightTextView;

    private SharedViewModel mSharedViewModel;
    private FragmentChangeListener fragmentChangeListener;
    private int mPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_detail, container, false);
        mDescriptionTextView = (TextView) view.findViewById(R.id.tv_detail_description);
        mDetailPictureImageView = (ImageView) view.findViewById(R.id.iv_detail_picture);
        mTitleTextView = (TextView) view.findViewById(R.id.tv_title);
        mCopyrightTextView = (TextView) view.findViewById(R.id.tv_copyright);

        if(getArguments() != null) {
            mPosition = getArguments().getInt(EXTRA_KEY_PICTURE_POSITION);
        }
        fragmentChangeListener = (MainActivity) getActivity();
        connectViewModel();
        return view;
    }

    private void connectViewModel(){
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        mSharedViewModel.getPictureList().observe(this, new Observer<List<Picture>>() {
            @Override
            public void onChanged(@Nullable List<Picture> pictureList) {
                connectImageViewOnClickListener(pictureList);
                updateUi(pictureList);
            }
        });
    }

    private void updateUi(List<Picture> pictureList){
        String url = NetworkUtil.validateUrl(pictureList.get(mPosition).getUrl());
        Picasso.get().load(url).fit().centerCrop().into(mDetailPictureImageView);
        mDescriptionTextView.setText(pictureList.get(mPosition).getExplanation());
        mTitleTextView.setText(pictureList.get(mPosition).getTitle());
        if(pictureList.get(mPosition).getCopyright() != null) {
            mCopyrightTextView.setText(pictureList.get(mPosition).getCopyright());
        }
        else{
            mCopyrightTextView.setVisibility(View.GONE);
        }
        //Glide.with(getContext()).load(url).apply(ImageUtil.getDetailImageRequestOptions()).into(mDetailPictureImageView);
    }

    private void connectImageViewOnClickListener(final List<Picture> pictureList){
        mDetailPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = pictureList.get(mPosition).getUrl();
                fragmentChangeListener.attachFullScreenFragment(url);
            }
        });
    }
}
