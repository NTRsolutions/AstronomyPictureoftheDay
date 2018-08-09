package youngmlee.com.astronomypictureoftheday.utils;

public class NetworkUtil {
    public static String validateUrl(String url){
        int urlStart = url.lastIndexOf("http");
        return url.substring(urlStart);
    }
}
