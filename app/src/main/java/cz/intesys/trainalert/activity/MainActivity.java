package cz.intesys.trainalert.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.adapter.NavAdapter;
import cz.intesys.trainalert.databinding.ActivityMainBinding;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.fragment.MainFragment;
import cz.intesys.trainalert.fragment.TrainIdDialogFragment;
import cz.intesys.trainalert.fragment.TripIdDialogFragment;
import cz.intesys.trainalert.repository.DataHelper;

import static android.support.v4.widget.DrawerLayout.STATE_IDLE;
import static android.support.v4.widget.DrawerLayout.STATE_SETTLING;
import static cz.intesys.trainalert.TaConfig.USE_OFFLINE_MAPS;

public class MainActivity extends AppCompatActivity implements TrainIdDialogFragment.OnFragmentInteractionListener, TripIdDialogFragment.OnFragmentInteractionListener {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final String MAIN_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.mainFragmentTag";
    private static final String TRAIN_ID_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.trainIdTag";
    private static final String TRIP_ID_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.tripIdTag";

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.activityMain_fragmentContainer, MainFragment.newInstance(), MAIN_FRAGMENT_TAG).commit();
        getSupportFragmentManager().executePendingTransactions();
        setupActionBar();
        RecyclerView recyclerView = binding.activityMainNavigationView.findViewById(R.id.activityMain_recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NavAdapter((id) -> onNavigationItemSelected(id)));

        if (!USE_OFFLINE_MAPS) { //
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                    ) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
                    getSupportFragmentManager().beginTransaction()
                            .remove(fragment)
                            .add(R.id.activityMain_fragmentContainer, MainFragment.newInstance(), MAIN_FRAGMENT_TAG)
                            .commit();
                    getSupportFragmentManager().executePendingTransactions();
                }
                return;
        }
    }

    @Override protected void onStart() {
        super.onStart();
        if (DataHelper.getInstance().isFirstRun() || DataHelper.getInstance().getTrainId() == 0) {
            TrainIdDialogFragment.newInstance().show(getSupportFragmentManager(), TRAIN_ID_DIALOG_FRAGMENT_TAG);
        } else { // We can immediately start selection of trip (should be in every start of application).
            showTripIdDialogFragment();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        fragment.setAnimating(true);
    }

    @Override public void onTrainIdReturned(int trainId) {
        DataHelper.getInstance().setTrainId(trainId);
        showTripIdDialogFragment();
    }

    @Override public void onTripSelected(int tripId) {

    }

    /**
     * There must be already set train id
     */
    private void showTripIdDialogFragment() {
        DataHelper.getInstance().getTrips(new TaCallback<List<Integer>>() {
            @Override public void onResponse(List<Integer> response) {
                TripIdDialogFragment.newInstance(response).show(getSupportFragmentManager(), TRIP_ID_DIALOG_FRAGMENT_TAG);
            }

            @Override public void onFailure(Throwable t) {

            }
        });
    }

    private void onNavigationItemSelected(@StringRes int id) {
        if (id == R.string.nav_pois) { // POIs
            binding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(PoiActivity.newIntent(this));
        } else if (id == R.string.nav_categories) {
            binding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(CategoryActivity.newIntent(this));
        }
    }

    private void setupActionBar() {
        setSupportActionBar(binding.activityMainToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, binding.activityMainDrawerLayout, binding.activityMainToolbar, R.string.activity_main_navigation_drawer_open, R.string.activity_main_navigation_drawer_close);
        binding.activityMainDrawerLayout.addDrawerListener(mToggle);
        binding.activityMainDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mToggle.syncState();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        binding.activityMainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == STATE_SETTLING) { // Animation is in progress.
                    fragment.setAnimating(false); // Stop animation in fragment to allow finer animation of drawer.
                } else if (newState == STATE_IDLE) { // Animation is not in progress.
                    fragment.setAnimating(true);
                }
            }
        });
    }
}
