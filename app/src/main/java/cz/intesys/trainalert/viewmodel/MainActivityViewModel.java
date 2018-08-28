package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.os.Handler;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.utility.Utility;

public class MainActivityViewModel extends BaseViewModel {

    // Stream of time events when autoregistration should be performed. True if it is first autoregistration, otherwise false.
    MutableLiveData<Boolean> mAutoRegisterLiveData = new MutableLiveData<>();
    private Utility.IntervalPoller mAutoRegistrationPoller;

    public MainActivityViewModel() {
        mAutoRegistrationPoller = new Utility.IntervalPoller(TaConfig.AUTO_REGISTRATION_INTERVAL, () -> {
             mAutoRegisterLiveData.setValue(false);
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startPolling() {
        mAutoRegisterLiveData.setValue(true);
        new Handler().postDelayed(() -> {
            mAutoRegistrationPoller.startPolling();
        }, TaConfig.AUTO_REGISTRATION_INTERVAL);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopPolling() {
        mAutoRegistrationPoller.stopPolling();
    }

    public void reloadPois() {
        mDataHelper.reloadPois();
    }

    public MutableLiveData<Boolean> getAutoRegisterLiveData() {
        return mAutoRegisterLiveData;
    }
}
