package cz.intesys.trainalert.activity;

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
import cz.intesys.trainalert.fragment.PoiListFragment;
import cz.intesys.trainalert.fragment.PoiMapFragment;

public class PoiActivity extends AppCompatActivity implements PoiListFragment.OnFragmentInteractionListener, PoiMapFragment.OnFragmentInteractionListener {

    private static final String POI_MAP_FRAGMENT_TAG = "cz.intesys.trainAlert.poiActivity.poiMapTag";
    private static final String POI_LIST_FRAGMENT_TAG = "cz.intesys.trainAlert.poiActivity.poiListTag";

    private ActivityPoiBinding mBinding;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, PoiActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_poi);

        setupActionBar();
        setupLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mBinding.fragmentContainer != null) { // In portrait mode with single fragment.
                    if (getSupportFragmentManager().findFragmentByTag(POI_MAP_FRAGMENT_TAG) != null) {
                        switchToPoiListFragment();
                    } else {
                        onBackPressed();
                    }
                } else { // In landscape mode with combined fragments.
                    onBackPressed();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoiSelect(Poi poi) {
        if (mBinding.fragmentContainer != null) {
            switchToPoiMapFragment(poi);
        } else {
            PoiMapFragment fragment = (PoiMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_poi_map);
            fragment.editPoi(poi);
        }
        Log.d("fraginteraction", "was clicked on poi " + poi.getTitle());
    }

    @Override
    public void onPoiSave(Poi poi) {

    }

    public void onAddPoiClick() {

    }

    private void setupLayout() {
        if (mBinding.fragmentContainer != null) {
            switchToPoiListFragment();
        }
    }

    private void setupActionBar() {
        setSupportActionBar(mBinding.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_poi_title);
    }

    private void switchToPoiListFragment() {
        PoiListFragment fragment = PoiListFragment.newInstance();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, POI_LIST_FRAGMENT_TAG).commit();
    }

    private void switchToPoiMapFragment(Poi poi) {
        PoiMapFragment fragment = PoiMapFragment.newInstance(poi);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, POI_MAP_FRAGMENT_TAG).commit();
    }
}
