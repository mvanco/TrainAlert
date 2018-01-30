package cz.intesys.trainalert.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ActivityPoiBinding;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.fragment.PoiListFragment;
import cz.intesys.trainalert.fragment.PoiMapFragment;
import cz.intesys.trainalert.viewmodel.PoiActivityViewModel;

public class PoiActivity extends AppCompatActivity implements PoiListFragment.OnFragmentInteractionListener, PoiMapFragment.OnFragmentInteractionListener {

    private static final String POI_MAP_FRAGMENT_TAG = "cz.intesys.trainAlert.poiActivity.poiMapTag";
    private static final String POI_LIST_FRAGMENT_TAG = "cz.intesys.trainAlert.poiActivity.poiListTag";

    private ActivityPoiBinding mBinding;
    private PoiActivityViewModel mViewModel;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PoiActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_poi);
        mViewModel = ViewModelProviders.of(this).get(PoiActivityViewModel.class);

        setupActionBar();

        if (savedInstanceState == null) { // Not restoring from previous state, otherwise performance issue
            setupLayout();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (inSingleFragmentMode()) { // In portrait mode with single fragment.
                    if (getSupportFragmentManager().findFragmentByTag(POI_MAP_FRAGMENT_TAG) != null) {
                        switchToPoiListFragment();
                    } else {
                        onBackPressed();
                    }
                } else { // In landscape mode with combined fragments.
                    onBackPressed();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoiAdded(Poi poi) {
        mViewModel.addPoi(poi, new TaCallback() { // TODO: probably use viewmodel on this activity
            @Override
            public void onResponse(Object response) {
                getPoiMapFragment().showNewPoiAddedNotification();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onPoiEdited(long id, Poi poi) {
        mViewModel.editPoi(id, poi, new TaCallback<Poi>() {
            @Override
            public void onResponse(Poi response) {
                getPoiMapFragment().showNewPoiEditedNotification();
            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }

    @Override
    public void onCategoryIconClick() {

    }

    @Override
    public void onPoiAdd() {
        if (inSingleFragmentMode()) {
            switchToPoiMapFragment(PoiMapFragment.MODE_ADD_POI);
        } else {
            PoiMapFragment fragment = (PoiMapFragment) getSupportFragmentManager().findFragmentById(R.id.activityPoi_mapFragment);
            fragment.addPoi();
        }
    }

    @Override
    public void onPoiSelect(Poi poi) {
        if (inSingleFragmentMode()) {
            if (inPoiListFragment()) {
                switchToPoiMapFragment(poi);
            } else {
                getPoiMapFragment().editPoi(poi);
            }
        } else {
            getPoiMapFragment().editPoi(poi);
        }

        Log.d("fraginteraction", "was clicked on poi " + poi.getTitle());
    }

    private PoiMapFragment getPoiMapFragment() {
        if (inSingleFragmentMode()) {
            return (PoiMapFragment) getSupportFragmentManager().findFragmentByTag(POI_MAP_FRAGMENT_TAG);
        } else {
            return (PoiMapFragment) getSupportFragmentManager().findFragmentById(R.id.activityPoi_mapFragment);
        }
    }

    private PoiListFragment getPoiListFragment() {
        if (inSingleFragmentMode()) {
            return (PoiListFragment) getSupportFragmentManager().findFragmentByTag(POI_LIST_FRAGMENT_TAG);
        } else {
            return (PoiListFragment) getSupportFragmentManager().findFragmentById(R.id.activityPoi_listFragment);
        }
    }

    private void setupLayout() {
        if (mBinding.activityPoiFragmentContainer != null) {
            switchToPoiListFragment();
        }
    }

    private void setupActionBar() {
        setSupportActionBar(mBinding.activityPoiToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_poi);
    }

    private void switchToPoiListFragment() {
        PoiListFragment fragment = PoiListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.activityPoi_fragmentContainer, fragment, POI_LIST_FRAGMENT_TAG).commit();
    }

    private void switchToPoiMapFragment(Poi poi) {
        PoiMapFragment fragment = PoiMapFragment.newInstance(poi);
        getSupportFragmentManager().beginTransaction().replace(R.id.activityPoi_fragmentContainer, fragment, POI_MAP_FRAGMENT_TAG).commit();
    }

    private void switchToPoiMapFragment(@PoiMapFragment.PoiMapFragmentMode int mode) {
        PoiMapFragment fragment = PoiMapFragment.newInstance(mode);
        getSupportFragmentManager().beginTransaction().replace(R.id.activityPoi_fragmentContainer, fragment, POI_MAP_FRAGMENT_TAG).commit();
    }

    private boolean inSingleFragmentMode() {
        return mBinding.activityPoiFragmentContainer != null;
    }

    private boolean inPoiListFragment() {
        if (inSingleFragmentMode()) {
            return getSupportFragmentManager().findFragmentByTag(POI_LIST_FRAGMENT_TAG) != null;
        } else {
            return true;
        }

    }
}
