package test.humanity.networkwrappertest;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.app.Application;

/**
 * Created by vkopanja on 19/08/2015.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}