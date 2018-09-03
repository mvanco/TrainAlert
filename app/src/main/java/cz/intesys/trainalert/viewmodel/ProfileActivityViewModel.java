package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.content.Context;

import cz.intesys.trainalert.entity.realm.Database;
import cz.intesys.trainalert.entity.realm.Profile;
import io.realm.Realm;

public class ProfileActivityViewModel extends ViewModel {

    Realm realm = Realm.getDefaultInstance();


    public ProfileActivityViewModel() {
    }

    public void addProfile(Context context, String title) {
        realm.beginTransaction();
        Profile unmanagedProfile = Profile.createFromPrefences(context);
        unmanagedProfile.setName(title);
//        Profile managedProfile = realm.copyToRealm(unmanagedProfile);
        Database database = realm.where(Database.class).findFirst();  // Should be only one instance here.
        database.getProfiles().add(unmanagedProfile);
        realm.commitTransaction();
    }

    public void deleteProfile(Profile profile) {
        realm.beginTransaction();
        Database database = realm.where(Database.class).findFirst();
        database.getProfiles().remove(profile);
        realm.commitTransaction();
    }

    public void loadProfile(Context context, String profileName) {
        Profile profile = realm.where(Profile.class).contains("name", profileName).findFirst();
        profile.saveToPreferences(context);
    }
}
