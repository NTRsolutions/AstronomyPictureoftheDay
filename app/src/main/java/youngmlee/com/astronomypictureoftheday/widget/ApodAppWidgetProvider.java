package youngmlee.com.astronomypictureoftheday.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.RemoteViews;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import youngmlee.com.astronomypictureoftheday.R;
import youngmlee.com.astronomypictureoftheday.di.DaggerServiceComponent;
import youngmlee.com.astronomypictureoftheday.di.NetworkModule;
import youngmlee.com.astronomypictureoftheday.domain.model.Picture;
import youngmlee.com.astronomypictureoftheday.network.RetrofitService;
import youngmlee.com.astronomypictureoftheday.ui.MainActivity;

public class ApodAppWidgetProvider extends AppWidgetProvider{

    @Inject
    public RetrofitService mRetrofitService;

    @Override
    public void onUpdate(final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds) {

        DaggerServiceComponent.builder()
                .networkModule(new NetworkModule())
                .build()
                .inject(this);

        Call<Picture> call = mRetrofitService.getLatestPicture();
        call.enqueue(new Callback<Picture>() {
            @Override
            public void onResponse(Call<Picture> call, Response<Picture> response) {
                Picture picture = response.body();

                for (int i = 0; i < appWidgetIds.length; i++) {
                    int appWidgetId = appWidgetIds[i];
                    Intent intent = new Intent(context, MainActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                    RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
                    views.setOnClickPendingIntent(R.id.iv_widget, pendingIntent);
                    Picasso.get().load(picture.getUrl()).into(views, R.id.iv_widget, new int[] {appWidgetId});
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                }

            }

            @Override
            public void onFailure(Call<Picture> call, Throwable t) {
                t.printStackTrace();
            }
        });

        }

}
