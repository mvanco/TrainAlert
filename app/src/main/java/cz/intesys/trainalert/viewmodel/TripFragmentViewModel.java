package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import java.util.List;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;

public class TripFragmentViewModel extends BaseViewModel {

    private Utility.IntervalPoller mTripPoller;
    private MediatorLiveData<List<Stop>> previousStops;
    private MediatorLiveData<List<Stop>> nextStops;
    private MediatorLiveData<Stop> finalStop;
    private MediatorLiveData<Boolean> shouldStop;
    private int mPreviousStopCount;
    private int mNextStopCount;

    public TripFragmentViewModel(int previousStopCount, int nextStopCount) {
        super();
        previousStops = new MediatorLiveData<>();
        nextStops = new MediatorLiveData<>();
        finalStop = new MediatorLiveData<>();
        shouldStop = new MediatorLiveData<>();
        mPreviousStopCount = previousStopCount;
        mNextStopCount = nextStopCount;
        mTripPoller = new Utility.IntervalPoller(TaConfig.TRIP_TIME_INTERVAL, () -> {
            DataHelper.getInstance().getPreviousStops(mPreviousStopCount, new TaCallback<List<Stop>>() {
                @Override public void onResponse(List<Stop> response) {
                    previousStops.setValue(response);
                }

                @Override public void onFailure(Throwable t) {
                }
            });
            DataHelper.getInstance().getNextStops(mNextStopCount, new TaCallback<List<Stop>>() {
                @Override public void onResponse(List<Stop> response) {
                    nextStops.setValue(response);
                }

                @Override public void onFailure(Throwable t) {
                }
            });
            DataHelper.getInstance().getFinalStop(new TaCallback<Stop>() {
                @Override public void onResponse(Stop response) {
                    finalStop.setValue(response);
                }

                @Override public void onFailure(Throwable t) {
                }
            });
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void startLocationPolling() {
        mTripPoller.startPolling();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stopLocationPolling() {
        mTripPoller.startPolling();
    }

    public MediatorLiveData<List<Stop>> getPreviousStopsLiveData() {
        return previousStops;
    }

    public MediatorLiveData<List<Stop>> getNextStopsLiveData() {
        return nextStops;
    }

    public MediatorLiveData<Stop> getFinalStopLiveData() {
        return finalStop;
    }

    public MediatorLiveData<Boolean> getShouldStopLiveData() {
        return shouldStop;
    }

    public static class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {
        private int mPreviousStopCount;
        private int mNextStopCount;

        public ViewModelFactory(int previousStopCount, int nextStopCount) {
            mPreviousStopCount = previousStopCount;
            mNextStopCount = nextStopCount;
        }

        @NonNull @Override public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new TripFragmentViewModel(mPreviousStopCount, mNextStopCount);
        }
    }
}
