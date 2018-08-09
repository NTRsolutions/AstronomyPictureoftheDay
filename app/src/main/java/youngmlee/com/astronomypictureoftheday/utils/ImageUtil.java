package youngmlee.com.astronomypictureoftheday.utils;

import com.bumptech.glide.request.RequestOptions;

import youngmlee.com.astronomypictureoftheday.R;

public class ImageUtil {

    public static RequestOptions getListImageRequestOptions(){
        return new RequestOptions().centerCrop().error(R.drawable.ic_error);
    }

    public static RequestOptions getDetailImageRequestOptions(){
        return new RequestOptions().centerCrop().error(R.drawable.ic_error);
    }
}
