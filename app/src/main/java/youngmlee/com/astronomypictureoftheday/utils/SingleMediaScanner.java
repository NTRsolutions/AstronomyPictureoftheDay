package youngmlee.com.astronomypictureoftheday.utils;


import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.util.Log;

import java.io.File;

public class SingleMediaScanner implements MediaScannerConnection.MediaScannerConnectionClient {
    private MediaScannerConnection mMediaScannerConnection;
    private File mFile;

    public SingleMediaScanner(Context context, File file){
        Log.d("PLEASE", "CREATED");
        mFile = file;
        mMediaScannerConnection = new MediaScannerConnection(context, this);
        mMediaScannerConnection.connect();
    }
    @Override
    public void onMediaScannerConnected() {
        mMediaScannerConnection.scanFile(mFile.getAbsolutePath(), null);
        Log.d("PLEASE", "connected");
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        Log.d("PLEASE", "SCANNED FILE:" + mFile.getAbsolutePath());
        mMediaScannerConnection.disconnect();
    }
}
