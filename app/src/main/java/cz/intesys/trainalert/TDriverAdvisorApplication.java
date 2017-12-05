package cz.intesys.trainalert;

import android.app.Application;
import android.content.Context;

public class TDriverAdvisorApplication extends Application {

    private static TDriverAdvisorApplication sInstance;

    public TDriverAdvisorApplication() {
        sInstance = this;
    }

    public static Context getContext() {
        return sInstance;
    }
}
