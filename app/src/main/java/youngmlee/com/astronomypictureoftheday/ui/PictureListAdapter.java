package youngmlee.com.astronomypictureoftheday.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;

import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.utils.NetworkUtil;

public class PictureListAdapter extends ListAdapter<Picture, PictureListAdapter.ViewHolder> {
    private Context mContext;
    private FragmentChangeListener mFragmentChangeListener;

    protected PictureListAdapter(Context context, @NonNull DiffUtil.ItemCallback<Picture> diffCallback) {
        super(diffCallback);
        mContext = context;
        mFragmentChangeListener = (MainActivity) context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.picture_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Picture picture = getItem(i);
        final int position = viewHolder.getAdapterPosition();

        viewHolder.mPictureCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentChangeListener.attachDetailViewPager(position, viewHolder.mPictureImageView);
            }
        });

        String url = NetworkUtil.validateUrl(picture.getUrl());
        Picasso.get().load(url).fit().centerCrop().into(viewHolder.mPictureImageView);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        final CardView mPictureCardView;
        final ImageView mPictureImageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPictureCardView = itemView.findViewById(R.id.cv_picture);
            mPictureImageView = itemView.findViewById(R.id.iv_picture);
        }
    }
}
