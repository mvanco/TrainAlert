package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.api.PoisApi;
import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.repository.PostgreSqlRepository;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class MainFragmentViewModel extends ViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<PoisApi> mPOIs;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;

    public MainFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();
        //mRepository = SimulatedRepository.getInstance();
        mRepository = PostgreSqlRepository.getInstance();
        mLocation.addSource(
                mRepository.getCurrentLocation(),
                currentLocation -> {
                    mLocation.setValue(currentLocation);
                    Log.d("observer", "i am handling location" + currentLocation.getLongitude() + "," + currentLocation.getLongitude());
                }
        );

        mPOIs = new MediatorLiveData<>();
        mPOIs.addSource(
                mRepository.getPois(),
                POIs -> {
                    mPOIs.setValue(POIs);
                }
        );

        mPOIs = new MediatorLiveData<PoisApi>();
        mDisabledAlarms = new ArrayList<Alarm>();
    }

    public LiveData<PoisApi> getPOIs() {
        return mPOIs;
    }

    public LiveData<Location> getCurrentLocation() {
        return mLocation;
    }

    public Location getStarterLocation() {
        return new Location(50.48365189588503, 14.039404579177328);
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

    public void loadPOIs() {
        mRepository.loadPois();
    }
}
