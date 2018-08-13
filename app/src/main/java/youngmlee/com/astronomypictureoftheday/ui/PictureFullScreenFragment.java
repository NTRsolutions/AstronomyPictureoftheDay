package youngmlee.com.astronomypictureoftheday.ui;

import android.Manifest;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.utils.NetworkUtil;
import youngmlee.com.astronomypictureoftheday.utils.SingleMediaScanner;


public class PictureFullScreenFragment extends Fragment {

    public static final String EXTRA_KEY_IMAGE_URL = "extra_key_image_url";
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL = 1;

    private PhotoView mFullScreenPhotoView;
    private String mImageUrl;
    private FloatingActionButton mShareFab;
    private FloatingActionButton mWallpaperFab;
    private FloatingActionButton mSaveFab;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        connectActionbar();
    }

    private void connectActionbar(){
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picture_fullscreen, container, false);
        mFullScreenPhotoView = (PhotoView) view.findViewById(R.id.pv_picture_fullscreen);

        if(getArguments() != null){
            mImageUrl = getArguments().getString(EXTRA_KEY_IMAGE_URL);
            mImageUrl = NetworkUtil.validateUrl(mImageUrl);
        }
        mShareFab = (FloatingActionButton) view.findViewById(R.id.fab_share);
        mWallpaperFab = (FloatingActionButton) view.findViewById(R.id.fab_set_as_wallpaper);
        mSaveFab = (FloatingActionButton) view.findViewById(R.id.fab_save) ;
        connectButtons();
        Picasso.get().load(mImageUrl).fit().centerInside().into(mFullScreenPhotoView);
        //Glide.with(this).load(mImageUrl).into(mFullScreenPhotoView);
        return view;
    }

    private void connectButtons(){
        mShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onShareItem();
            }
        });

        mWallpaperFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSetWallPaper();
            }
        });

        mSaveFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestWritePermission();
            }
        });
    }


    private void onShareItem(){
        Picasso.get().load(mImageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                shareImage(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }



    private void onSetWallPaper(){
        Picasso.get().load(mImageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
                Intent wallPaperIntent = wallpaperManager.getCropAndSetWallpaperIntent(getCacheImageUri(bitmap));
                startActivity(wallPaperIntent);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }

    private void onSaveImage(){
        Picasso.get().load(mImageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                saveBitmapToExternal(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }


    private void shareImage(Bitmap bitmap){
        Uri contentUri = getCacheImageUri(bitmap);
        Log.d("SAVETEST", "uri: " + contentUri.toString());

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setDataAndType(contentUri, getContext().getContentResolver().getType(contentUri));
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("image/png");
        startActivity(Intent.createChooser(shareIntent, "Share via: "));
    }


    private Uri getCacheImageUri(Bitmap bitmap){
        try{
            File cachePath = new File(getContext().getCacheDir(), "images");
            cachePath.mkdirs();
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        File imagePath = new File(getContext().getCacheDir(), "images");
        File newFile = new File(imagePath, "image.png");
        Uri contentUri = FileProvider.getUriForFile(getContext(), "youngmlee.com.astronomypictureoftheday.fileprovider", newFile);
        return contentUri;
    }


    private void saveBitmapToExternal(Bitmap bitmap){
        Toast.makeText(getContext(), "Saving...", Toast.LENGTH_SHORT).show();
        Log.d("AVAILABLE: ",  ""+ isExternalStorageWritable());
        String imageName = System.currentTimeMillis() + ".jpg";
        File imageRoot = getPublicPictureStorageDir();
        imageRoot.mkdirs();
        Log.d("LASTONE", "" + imageRoot.mkdirs());
        File image = new File(imageRoot, imageName);
        try {
            FileOutputStream out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        new SingleMediaScanner(getContext(), image);
    }

    private File getPublicPictureStorageDir(){
        File rootFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imagesFolder = new File(rootFile, "Astronomy");

        if(!imagesFolder.mkdirs()){
            Log.d("LAST", "Directory not created");
        }
        Log.d("LAST", "DIRECTORY: " + imagesFolder.getAbsolutePath());
        return imagesFolder;

    }
    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    private void requestWritePermission(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL);
        } else {
                onSaveImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onSaveImage();
                } else {
                    Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
