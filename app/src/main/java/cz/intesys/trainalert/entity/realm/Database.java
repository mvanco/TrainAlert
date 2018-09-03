package cz.intesys.trainalert.entity.realm;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Database extends RealmObject {
    private RealmList<Profile> profiles = new RealmList<>();

    public Database() {
        profiles = new RealmList<>();
    }

    public RealmList<Profile> getProfiles() {
        return profiles;
    }

    public void setProfiles(RealmList<Profile> profiles) {
        this.profiles = profiles;
    }


}
