package cz.intesys.trainalert.di;

import android.content.SharedPreferences;

import cz.intesys.trainalert.TaApplication;
import dagger.Component;
import dagger.Module;

@Module
@Component (modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(TaApplication taApplication);

    SharedPreferences getSharedPreferences();
}
