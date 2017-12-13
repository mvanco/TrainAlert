package cz.intesys.trainalert;

import android.app.Application;
import android.content.Context;

public class TaApplication extends Application {

    private static TaApplication sInstance;

    public TaApplication() {
        sInstance = this;
    }

    public static Context getContext() {
        return sInstance;
    }
}
