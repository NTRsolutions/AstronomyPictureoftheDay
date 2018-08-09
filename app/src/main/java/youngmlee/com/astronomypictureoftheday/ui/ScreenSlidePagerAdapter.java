package youngmlee.com.astronomypictureoftheday.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import youngmlee.com.astronomypictureoftheday.domain.model.Picture;

public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

    private List<Picture> mPictureList;

    public ScreenSlidePagerAdapter(FragmentManager fm, List<Picture> pictureList) {
        super(fm);
        mPictureList = pictureList;
    }

    @Override
    public Fragment getItem(int i) {
        PictureDetailFragment pictureDetailFragment = new PictureDetailFragment();
        Bundle args = new Bundle();
        args.putInt(PictureDetailFragment.EXTRA_KEY_PICTURE_POSITION, i);
        pictureDetailFragment.setArguments(args);
        return pictureDetailFragment;
    }

    @Override
    public int getCount() {
        return mPictureList.size();
    }
}
