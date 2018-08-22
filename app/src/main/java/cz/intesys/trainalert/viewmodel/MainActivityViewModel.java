package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.utility.Utility;

public class MainActivityViewModel extends BaseViewModel {

    MutableLiveData<Void> mAutoRegisterLiveData = new MutableLiveData<>();
    private Utility.IntervalPoller mAutoRegistrationPoller;

    public MainActivityViewModel() {
        mAutoRegistrationPoller = new Utility.IntervalPoller(TaConfig.AUTO_REGISTRATION_INTERVAL, () -> {
             mAutoRegisterLiveData.setValue(null);
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startPolling() {
        mAutoRegistrationPoller.startPolling();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopPolling() {
        mAutoRegistrationPoller.stopPolling();
    }

    public void reloadPois() {
        mDataHelper.reloadPois();
    }

    public MutableLiveData<Void> getAutoRegisterLiveData() {
        return mAutoRegisterLiveData;
    }
}
