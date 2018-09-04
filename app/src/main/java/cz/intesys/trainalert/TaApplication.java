package cz.intesys.trainalert;

import android.app.Application;
import android.content.SharedPreferences;

import javax.inject.Inject;

import cz.intesys.trainalert.di.ApplicationComponent;
import cz.intesys.trainalert.di.ApplicationModule;
import cz.intesys.trainalert.di.DaggerApplicationComponent;
import cz.intesys.trainalert.entity.realm.Database;
import cz.intesys.trainalert.entity.realm.Profile;
import io.realm.Realm;

public class TaApplication extends Application {
    private static TaApplication sInstance;
    protected ApplicationComponent mApplicationComponent;

    @Inject
    SharedPreferences mSharedPreferences;

    public TaApplication() {
        sInstance = this;
    }

    public static TaApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
        mApplicationComponent.inject(this);
        Realm.init(this);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Database database = realm.createObject(Database.class);
        Profile profile = Profile.createFromPrefences(getApplicationContext(), "Výchozí profil");
        database.getProfiles().add(profile);
        realm.commitTransaction();
    }

    /**
     * For possible future use
     */
    public SharedPreferences getSharedPreferences() {
        return mSharedPreferences;
    }

    public ApplicationComponent getApplicationComponent() {
        return mApplicationComponent;
    }
}


