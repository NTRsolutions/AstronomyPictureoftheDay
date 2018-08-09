package youngmlee.com.astronomypictureoftheday.domain;

public interface RepositoryCallbacks {
    void onResponse(String message);
    void onFailure(Throwable throwable);
}
