package youngmlee.com.astronomypictureoftheday.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.viewModel.SharedViewModel;

public class PictureListFragment extends Fragment{

    private SharedViewModel mSharedViewModel;
    private AdView mAdView;
    private RecyclerView mRecyclerView;
    private ListAdapter mListAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private String lastVisibleDate;
    private boolean isLoadingMoreData;

    @Override
    public void onResume() {
        super.onResume();
        FirebaseAnalytics.getInstance(getContext())
                .setCurrentScreen(
                        getActivity(),
                        this.getClass().getSimpleName(),
                        this.getClass().getSimpleName());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        connectActionbar();
        super.onActivityCreated(savedInstanceState);
    }

    private void connectActionbar(){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(R.string.app_name);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mSharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);
        View view = inflater.inflate(R.layout.fragment_picture_list, container, false);
        connectRecyclerView(view);
        connectViewModel();
        initializeAds(view);
        return view;
    }

    private void connectViewModel() {
        mSharedViewModel.getPictureList().observe(this, new Observer<List<Picture>>() {
            @Override
            public void onChanged(@Nullable List<Picture> pictureList) {
                isLoadingMoreData = false;
                mListAdapter.submitList(pictureList);
                if(mSharedViewModel.hasAccessedViewPager()) {
                    mRecyclerView.scrollToPosition(mSharedViewModel.getCurrentPosition());
                    mSharedViewModel.setHasAccessedViewPager(false);
                }
                lastVisibleDate = pictureList.get(pictureList.size()-1).getDate();
            }
        });
    }

    private void connectRecyclerView(View view) {

        mListAdapter = new PictureListAdapter(getActivity(), new DiffUtil.ItemCallback<Picture>() {
            @Override
            public boolean areItemsTheSame(@NonNull Picture picture, @NonNull Picture t1) {
                return picture.getDate().equals(t1.getDate());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Picture picture, @NonNull Picture t1) {
                return picture.getDate().equals(t1.getDate());
            }
        });

        mRecyclerView = view.findViewById(R.id.rv_pictures);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mListAdapter);

        RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisiblePosition = mLinearLayoutManager.findLastVisibleItemPosition();
                if (!isLoadingMoreData && (lastVisiblePosition == (mListAdapter.getItemCount() - 1))) {
                    mSharedViewModel.loadMoreData(lastVisibleDate);
                    isLoadingMoreData = true;
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        };

        mRecyclerView.addOnScrollListener(mScrollListener);
    }

    private void initializeAds(View view){

        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
    }

    @Override
    public void onPause() {
        //mRecyclerView.clearOnScrollListeners();
        super.onPause();
    }
}
