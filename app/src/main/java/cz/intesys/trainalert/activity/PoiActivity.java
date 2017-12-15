package cz.intesys.trainalert.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ActivityPoiBinding;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.fragment.PoiListFragment;
import cz.intesys.trainalert.fragment.PoiMapFragment;

public class PoiActivity extends AppCompatActivity implements PoiListFragment.OnFragmentInteractionListener, PoiMapFragment.OnFragmentInteractionListener {

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
                onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPoiClick(Poi poi) {
        Log.d("fraginteraction", "was clicked on poi " + poi.getTitle());
    }

    @Override
    public void onPoiSave(Poi poi) {

    }

    private void setupLayout() {
        if (mBinding.fragmentContainer != null) {
            Fragment fragment = PoiListFragment.newInstance("ahoj", "druhje");
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    private void setupActionBar() {
        setSupportActionBar(mBinding.myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.activity_poi_title);
    }
}
