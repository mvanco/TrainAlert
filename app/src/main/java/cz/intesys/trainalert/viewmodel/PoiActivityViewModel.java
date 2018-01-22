package cz.intesys.trainalert.viewmodel;

import android.arch.lifecycle.ViewModel;

import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.repository.Repository;

import static cz.intesys.trainalert.TaConfig.REPOSITORY;


public class PoiActivityViewModel extends ViewModel {

    private Repository mRepository;

    public PoiActivityViewModel() {
        mRepository = REPOSITORY;
    }

    public void addPoi(Poi poi, TaCallback<Poi> taCallback) {
        mRepository.addPoi(poi, taCallback);
    }

    public void editPoi(long id, Poi poi, TaCallback<Poi> taCallback) {
        mRepository.editPoi(id, poi, taCallback);
    }


}
