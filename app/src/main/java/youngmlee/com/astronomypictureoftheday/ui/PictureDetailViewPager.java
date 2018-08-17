package youngmlee.com.astronomypictureoftheday.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.viewModel.SharedViewModel;

public class PictureDetailViewPager extends Fragment {

    private ViewPager mViewPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private SharedViewModel mSharedViewModel;
    private String lastVisibleDate;
    private boolean isLoadingMoreData;
    private int initialClickedPosition;


    public static final String EXTRA_KEY_CLICKED_POSITION = "extra_key_clicked_position";

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        connectActionbar();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(getContext())
                .setCurrentScreen(
                        getActivity(),
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(getArguments() != null){
            initialClickedPosition = getArguments().getInt(EXTRA_KEY_CLICKED_POSITION);
        }
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_picture_detail_view_pager, container, false);
        mViewPager = view.findViewById(R.id.vp_picture_detail);
        connectViewModel();
        connectPageChangeListener();
        return view;
    }

    private void connectActionbar(){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    private void connectViewModel(){
        mSharedViewModel.getPictureList().observe(this, new Observer<List<Picture>>() {
            @Override
            public void onChanged(@Nullable List<Picture> pictureList) {
                isLoadingMoreData = false;
                lastVisibleDate = pictureList.get(pictureList.size()-1).getDate();
                mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager(), pictureList);
                mViewPager.setAdapter(mPagerAdapter);
                setCurrentPage();
            }
        });
    }

    private void setCurrentPage(){
        mViewPager.setCurrentItem(mSharedViewModel.getCurrentPosition());
    }


    private void connectPageChangeListener(){
        ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                mSharedViewModel.setCurrentPosition(i);
                if(!isLoadingMoreData &&(i == (mPagerAdapter.getCount()) - 1)){
                    mSharedViewModel.loadMoreData(lastVisibleDate);
                    isLoadingMoreData = true;
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        };
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onPause() {
       // mViewPager.clearOnPageChangeListeners();
        super.onPause();
    }

}
