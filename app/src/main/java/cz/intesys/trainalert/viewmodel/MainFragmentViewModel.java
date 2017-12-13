package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.PostgreSqlRepository;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class MainFragmentViewModel extends ViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<Poi>> mPois;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;

    public MainFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();
        //mRepository = SimulatedRepository.getInstance();

        mPois = new MediatorLiveData<List<Poi>>();
        mRepository = PostgreSqlRepository.getInstance();
        mDisabledAlarms = new ArrayList<Alarm>();

        mLocation.addSource(mRepository.getCurrentLocation(), currentLocation -> mLocation.setValue(currentLocation));
        mPois.addSource(mRepository.getPois(), pois -> mPois.setValue(pois));
    }

    public void loadPOIs() {
        mRepository.loadPois();
    }

    public Location getStarterLocation() {
        return new Location(50.48365189588503, 14.039404579177328);
    }

    public LiveData<Location> getCurrentLocation() {
        return mLocation;
    }

    public LiveData<List<Poi>> getPois() {
        return mPois;
    }

    public boolean areLoadedPois() {
        return mPois.getValue() != null;
    }

    public void disableAlarm(Alarm alarm) {
        mDisabledAlarms.add(alarm);
        alarm.disable();
    }

    public void enableAlarm(Alarm alarm) {
        alarm.enable();
        mDisabledAlarms.remove(alarm);
    }

    public List<Alarm> getDisabledAlarms() {
        return mDisabledAlarms;
    }

    public void restartRepository() {
        if (mRepository instanceof SimulatedRepository) {
            ((SimulatedRepository) mRepository).restartRepository();
        }
    }
}
