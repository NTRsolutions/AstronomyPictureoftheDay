package youngmlee.com.astronomypictureoftheday.di;

import javax.inject.Singleton;

import dagger.Component;
import youngmlee.com.astronomypictureoftheday.network.NotifyLatestJobService;
import youngmlee.com.astronomypictureoftheday.widget.ApodAppWidgetProvider;

@Singleton
@Component (modules = {NetworkModule.class})
public interface ServiceComponent {
    void inject(NotifyLatestJobService notifyLatestJobService);
    void inject(ApodAppWidgetProvider apodAppWidgetProvider);
}
