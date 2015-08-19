package test.humanity.networkwrappertest.helpers;

/**
 * Created by vkopanja on 19/08/2015.
 */
public class Constants {

    public static String FlickrApiKey = "fc4e753bf36c37e03d0c9fb737fe94b2";
    public static Integer PerPage = 15;
    public static String FlickrPhotoSearch = "https://api.flickr.com/services/rest/?method=flickr.photos.search&text=cats&api_key=%s&per_page=%d&format=json&nojsoncallback=1";
    public static String FlickrImageUrl = "https://farm%d.staticflickr.com/%d/%s_%s.jpg";

}