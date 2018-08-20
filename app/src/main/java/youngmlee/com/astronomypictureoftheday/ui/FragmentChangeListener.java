package youngmlee.com.astronomypictureoftheday.ui;


import android.widget.ImageView;

interface FragmentChangeListener {

    void attachDetailViewPager (int clickedPosition, ImageView sharedImageView);

    void attachFullScreenFragment(String url);

}
