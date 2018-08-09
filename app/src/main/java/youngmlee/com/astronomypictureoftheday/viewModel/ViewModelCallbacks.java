package youngmlee.com.astronomypictureoftheday.viewModel;

public interface ViewModelCallbacks {
    void onResponse(String message);
    void onFailure(Throwable throwable);
}
