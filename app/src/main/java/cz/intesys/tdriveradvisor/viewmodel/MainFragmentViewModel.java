package cz.intesys.tdriveradvisor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.tdriveradvisor.entity.Alarm;
import cz.intesys.tdriveradvisor.entity.Location;
import cz.intesys.tdriveradvisor.entity.POI;
import cz.intesys.tdriveradvisor.repository.Repository;
import cz.intesys.tdriveradvisor.repository.SimulatedRepository;


public class MainFragmentViewModel extends ViewModel {

    private MediatorLiveData<List<POI>> mPOIs;
    private Repository mRepository;
    private List<Alarm> mDisabledAlarms;

    public MainFragmentViewModel() {
        mPOIs = new MediatorLiveData<List<POI>>();
        mRepository = SimulatedRepository.getInstance();
        mDisabledAlarms = new ArrayList<Alarm>();
    }

    public LiveData<List<POI>> getPOIs() {
        return mPOIs;
    }

    public LiveData<List<POI>> loadPOIs(Context context) {
        mPOIs.setValue(mRepository.getPOIs(context));
        return mPOIs;
    }

    public Location getCurrentLocation() {
        return mRepository.getCurrentLocation();
    }

    public void disableNewAlarm(Alarm alarm) {
        mDisabledAlarms.add(alarm);
        alarm.disable();
    }

    public void enableDisabledAlarm(Alarm alarm) {
        alarm.enable();
        mDisabledAlarms.remove(alarm);
    }

    public List<Alarm> getDisabledAlarms() {
        return mDisabledAlarms;
    }
}
