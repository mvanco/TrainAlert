package cz.intesys.trainalert;

import android.app.Application;
import android.content.Context;

public class TrainAlertApplication extends Application {

    private static TrainAlertApplication sInstance;

    public TrainAlertApplication() {
        sInstance = this;
    }

    public static Context getContext() {
        return sInstance;
    }
}
