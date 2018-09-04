package cz.intesys.trainalert.viewmodel;

import java.util.List;

import cz.intesys.trainalert.entity.realm.Database;
import cz.intesys.trainalert.entity.realm.Profile;
import io.realm.Realm;

/**
 * Not used.
 */
public class ProfileFragmentViewModel extends BaseViewModel {
    Realm realm = Realm.getDefaultInstance();

    public ProfileFragmentViewModel() {
    }

    public List<Profile> getProfiles() {
        Database database = realm.where(Database.class).findFirst();
        return database.getProfiles();
    }
}
