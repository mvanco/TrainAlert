package cz.intesys.tdriveradvisor.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.ViewModel;

import java.util.List;

import cz.intesys.tdriveradvisor.entity.POI;
import cz.intesys.tdriveradvisor.repository.Repository;
import cz.intesys.tdriveradvisor.repository.SimulatedRepository;


public class MainFragmentViewModel extends ViewModel {

    MediatorLiveData<List<POI>> POIs;
    private Repository mRepository;

    public MainFragmentViewModel() {
        mRepository = SimulatedRepository.getInstance();
    }

    public LiveData<List<POI>> getPOIs() {
        return POIs;
    }

    public LiveData<List<POI>> loadPOIs() {
//        POIs.addSource(
//                mRepository.getPOIs(),
//                returnedPOIs -> POIs.setValue(returnedPOIs)
//        );
        return POIs;
    }

    //    public ListIssuesViewModel() {
//        mApiResponse = new MediatorLiveData<>();
//        mIssueRepository = new IssueRepositoryImpl();
//    }
//
//    @NonNull
//    public LiveData<ApiResponse> getApiResponse() {
//        return mApiResponse;
//    }
//
//    public LiveData<ApiResponse> loadIssues(@NonNull String user, String repo) {
//        mApiResponse.addSource(
//                mIssueRepository.getIssues(user, repo),
//                apiResponse -> mApiResponse.setValue(apiResponse)
//        );
//        return mApiResponse;
//    }

}
