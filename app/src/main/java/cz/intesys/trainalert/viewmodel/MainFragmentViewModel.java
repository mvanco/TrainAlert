package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.POI;
import cz.intesys.trainalert.repository.PostgreSQLRepository;
import cz.intesys.trainalert.repository.Repository;
import cz.intesys.trainalert.repository.SimulatedRepository;

public class MainFragmentViewModel extends ViewModel {

    private MediatorLiveData<Location> mLocation;
    private MediatorLiveData<List<POI>> mPOIs;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;

    public MainFragmentViewModel() {
        mLocation = new MediatorLiveData<Location>();
        //mRepository = SimulatedRepository.getInstance();
        mRepository = PostgreSQLRepository.getInstance();
        mLocation.addSource(
                mRepository.getCurrentLocation(),
                currentLocation -> {
                    mLocation.setValue(currentLocation);
                    Log.d("observer", "i am handling location" + currentLocation.getLongitude() + "," + currentLocation.getLongitude());
                }
        );

        mPOIs = new MediatorLiveData<List<POI>>();
        mDisabledAlarms = new ArrayList<Alarm>();
    }

    public LiveData<Location> getLocation() {
        return mLocation;
    }

    public LiveData<List<POI>> getPOIs() {
        return mPOIs;
    }

    public LiveData<List<POI>> loadPOIs(Context context) {
        mPOIs.setValue(mRepository.getPOIs(context));
        return mPOIs;
    }

    public LiveData<Location> getCurrentLocation() {
        if (mLocation.getValue() == null) {
            mLocation.setValue(new Location(50.48365189588503, 14.039404579177328)); // Set default location in order to prevent possible errors.
        }
        return mLocation;
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
