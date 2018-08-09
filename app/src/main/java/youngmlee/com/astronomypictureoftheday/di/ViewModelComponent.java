package youngmlee.com.astronomypictureoftheday.di;

import javax.inject.Singleton;

import dagger.Component;
import youngmlee.com.astronomypictureoftheday.viewModel.SharedViewModel;

@Singleton
@Component(modules = {AppModule.class, RepositoryModule.class, NetworkModule.class, DatabaseModule.class})
public interface ViewModelComponent {

    void inject(SharedViewModel sharedViewModel);

}
