package cz.intesys.trainalert.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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
import cz.intesys.trainalert.fragment.TripFragment;
import cz.intesys.trainalert.fragment.TripIdDialogFragment;
import cz.intesys.trainalert.fragment.TripIdManuallyDialogFragment;
import cz.intesys.trainalert.repository.DataHelper;

import static android.support.v4.widget.DrawerLayout.STATE_IDLE;
import static android.support.v4.widget.DrawerLayout.STATE_SETTLING;
import static cz.intesys.trainalert.TaConfig.TRIP_FRAGMENT_NEXT_STOP_COUNT;
import static cz.intesys.trainalert.TaConfig.TRIP_FRAGMENT_PREVIOUS_STOP_COUNT;
import static cz.intesys.trainalert.TaConfig.USE_OFFLINE_MAPS;

public class MainActivity extends AppCompatActivity implements TripIdDialogFragment.OnFragmentInteractionListener, TripFragment.OnFragmentInteractionListener {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final String MAIN_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.mainFragmentTag";
    private static final String TRIP_ID_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.tripIdTag";
    private static final String TRIP_ID_MANUALLY_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.tripIdManuallyTag";
    private static final String TRIP_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.sideFragmentTag";


    private ActivityMainBinding mBinding;
    private ActionBarDrawerToggle mToggle;
    private TripIdDialogFragment mTripDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().add(R.id.activityMain_fragmentContainer, MainFragment.newInstance(), MAIN_FRAGMENT_TAG).commit();
        getSupportFragmentManager().executePendingTransactions();
        setupActionBar();
        RecyclerView recyclerView = mBinding.activityMainNavigationView.findViewById(R.id.activityMain_recyclerView);
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
        if (DataHelper.getInstance().isFirstRun()) {
            DataHelper.getInstance().getTrainId(new TaCallback<String>() { // Load train id to SharedPreferences.
                @Override public void onResponse(String response) {
                }

                @Override public void onFailure(Throwable t) {
                }
            });
        }
        showTripIdDialogFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        fragment.setAnimating(true);

        DialogFragment tripIdDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(TRIP_ID_DIALOG_FRAGMENT_TAG);

        if (tripIdDialogFragment != null && tripIdDialogFragment.getDialog() != null && tripIdDialogFragment.getDialog().isShowing()) {
            tripIdDialogFragment.dismiss();
        }

        DialogFragment tripIdManuallyDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(TRIP_ID_MANUALLY_DIALOG_FRAGMENT_TAG);

        if (tripIdManuallyDialogFragment != null && tripIdManuallyDialogFragment.getDialog() != null && tripIdManuallyDialogFragment.getDialog().isShowing()) {
            tripIdManuallyDialogFragment.dismiss();
        }

    }

    @Override
    public void onTripSelected(String tripId) { // Returned from TripIdDialogFragment or TripIdManuallyDialogFragment
        DataHelper.getInstance().setTrip(tripId, new TaCallback<Void>() {
            @Override public void onResponse(Void response) {
                mBinding.activityMainInclude.activityMainSideContainer.setVisibility(View.VISIBLE);
                Fragment fragment = TripFragment.newInstance(TRIP_FRAGMENT_PREVIOUS_STOP_COUNT, TRIP_FRAGMENT_NEXT_STOP_COUNT);
                getSupportFragmentManager().beginTransaction().replace(R.id.activityMain_sideContainer, fragment, TRIP_FRAGMENT_TAG).commit();
                getSupportFragmentManager().executePendingTransactions();
            }

            @Override public void onFailure(Throwable t) {

            }
        });
    }

    @Override public void onBusinessTripSelected() {
        mBinding.activityMainInclude.activityMainSideContainer.setVisibility(View.GONE);
    }

    @Override public void onTripManuallySelected() {
        DialogFragment tripIdDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(TRIP_ID_DIALOG_FRAGMENT_TAG);

        if (tripIdDialogFragment != null && tripIdDialogFragment.getDialog() != null && tripIdDialogFragment.getDialog().isShowing()) {
            tripIdDialogFragment.dismiss();
        }
        TripIdManuallyDialogFragment.newInstance().show(getSupportFragmentManager(), TRIP_ID_MANUALLY_DIALOG_FRAGMENT_TAG);
    }

    @Override public void onFragmentInteraction(Uri uri) {

    }

    /**
     * There must be already set train id
     */
    synchronized private void showTripIdDialogFragment() {
        DataHelper.getInstance().getTrips(new TaCallback<List<String>>() {
            @Override public void onResponse(List<String> response) {

                DialogFragment tripIdDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(TRIP_ID_DIALOG_FRAGMENT_TAG);

                if (tripIdDialogFragment != null && tripIdDialogFragment.getDialog() != null && tripIdDialogFragment.getDialog().isShowing()) {
                    return;
                }

                DialogFragment tripIdManuallyDialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag(TRIP_ID_MANUALLY_DIALOG_FRAGMENT_TAG);

                if (tripIdManuallyDialogFragment != null && tripIdManuallyDialogFragment.getDialog() != null && tripIdManuallyDialogFragment.getDialog().isShowing()) {
                    return;
                }

                mTripDialogFragment = TripIdDialogFragment.newInstance(response);
                mTripDialogFragment.show(getSupportFragmentManager(), TRIP_ID_DIALOG_FRAGMENT_TAG);
            }

            @Override public void onFailure(Throwable t) {

            }
        });
    }

    private void onNavigationItemSelected(@StringRes int id) {
        if (id == R.string.nav_pois) { // POIs
            mBinding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(PoiActivity.newIntent(this));
        } else if (id == R.string.nav_categories) {
            mBinding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(CategoryActivity.newIntent(this));
        }
    }

    private void setupActionBar() {
        setSupportActionBar(mBinding.activityMainToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, mBinding.activityMainDrawerLayout, mBinding.activityMainToolbar, R.string.activity_main_navigation_drawer_open, R.string.activity_main_navigation_drawer_close);
        mBinding.activityMainDrawerLayout.addDrawerListener(mToggle);
        mBinding.activityMainDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mToggle.syncState();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        mBinding.activityMainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
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
