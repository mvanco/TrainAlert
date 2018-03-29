package cz.intesys.trainalert.activity;

import android.Manifest;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.adapter.NavAdapter;
import cz.intesys.trainalert.databinding.ActivityMainBinding;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.fragment.MainFragment;
import cz.intesys.trainalert.fragment.PasswordDialogFragment;
import cz.intesys.trainalert.fragment.TripFragment;
import cz.intesys.trainalert.fragment.TripIdDialogFragment;
import cz.intesys.trainalert.fragment.TripIdManuallyDialogFragment;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.view.FinishAnimationView;
import cz.intesys.trainalert.viewmodel.MainActivityViewModel;

import static android.support.v4.widget.DrawerLayout.STATE_IDLE;
import static android.support.v4.widget.DrawerLayout.STATE_SETTLING;
import static cz.intesys.trainalert.TaConfig.TRIP_FRAGMENT_NEXT_STOP_COUNT;
import static cz.intesys.trainalert.TaConfig.TRIP_FRAGMENT_PREVIOUS_STOP_COUNT;
import static cz.intesys.trainalert.TaConfig.USE_OFFLINE_MAPS;
import static cz.intesys.trainalert.repository.DataHelper.TRIP_NO_TRIP;

public class MainActivity extends AppCompatActivity implements TripIdDialogFragment.OnFragmentInteractionListener, TripFragment.OnFragmentInteractionListener, PasswordDialogFragment.OnFragmentInteractionListener {

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    private static final String MAIN_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.mainFragmentTag";
    private static final String TRIP_ID_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.tripIdTag";
    private static final String TRIP_ID_MANUALLY_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.tripIdManuallyTag";
    private static final String PASSWORD_DIALOG_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.passwordTag";
    private static final String TRIP_FRAGMENT_TAG = "cz.intesys.trainAlert.mainActivity.sideFragmentTag";

    private ActivityMainBinding mBinding;
    private ActionBarDrawerToggle mToggle;
    private TripIdDialogFragment mTripDialogFragment;
    private Menu mMenu;
    private MainActivityViewModel mViewModel;
    private TextView mClockTextView;
    private boolean mShouldShowPasswordDialog = true;
    private AnimatorSet mAnimSet;
    private boolean mTripSelectionIconEnabled = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        getLifecycle().addObserver(mViewModel);
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

        mViewModel.getLocationLiveData().observe(this, (location) -> onTimeChanged(location.getTime()));

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mAnimSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.ic_trip_selection_animator);
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
    }

    @Override
    protected void onResume() {
        super.onResume();

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

    @Override
    public void onTripSelected(String tripId) { // Returned from TripIdDialogFragment or TripIdManuallyDialogFragment
        showTripIdSelectionIconLoader();
        DataHelper.getInstance().setTrip(tripId, new TaCallback<Void>() {
            @Override public void onResponse(Void response) {
                mBinding.activityMainInclude.activityMainSideContainer.setVisibility(View.VISIBLE);
                Fragment fragment = TripFragment.newInstance(TRIP_FRAGMENT_PREVIOUS_STOP_COUNT, TRIP_FRAGMENT_NEXT_STOP_COUNT);
                getSupportFragmentManager().beginTransaction().replace(R.id.activityMain_sideContainer, fragment, TRIP_FRAGMENT_TAG).commit();
                getSupportFragmentManager().executePendingTransactions();
                hideTripIdSelectionIconLoader();
                mMenu.findItem(R.id.menu_trip_selection).setIcon(R.drawable.ic_trip_selection);
            }

            @Override public void onFailure(Throwable t) {
                hideTripIdSelectionIconLoader();
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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem tripSelectionItem = menu.findItem(R.id.menu_trip_selection);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.custom_action_view, null);
        iv.setOnClickListener(v -> MainActivity.this.onOptionsItemSelected(tripSelectionItem));
        tripSelectionItem.setActionView(iv);

        MenuItem clockItem = menu.findItem(R.id.menu_clock);
        mClockTextView = (TextView) clockItem.getActionView();
        mClockTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.activityMain_clock_textSize));

        mMenu = menu;
        hideTripIdSelectionIconLoader();
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_trip_selection:
                if (mTripSelectionIconEnabled) {
                    showTripIdDialogFragment();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override public void onPasswordEntered(int password) {
        DataHelper.getInstance().register(password, new TaCallback<Void>() {
            @Override public void onResponse(Void response) {
                mBinding.activityMainDrawerLayout.openDrawer(Gravity.LEFT);
            }

            @Override public void onFailure(Throwable t) {
                Toast.makeText(MainActivity.this, R.string.error_wrong_password, Toast.LENGTH_SHORT).show();
            }
        });
        mShouldShowPasswordDialog = true;
    }

    @Override public void onDialogCanceled() {
        mShouldShowPasswordDialog = true;
    }

    @Override public void onFinishAnimationStarted() {
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.setAnimating(false);
        }
        new Handler().postDelayed(() -> {
            fragment.setAnimating(true);
        }, FinishAnimationView.ANIMATION_DURATION);
    }

    @Override public void onTripFinished() {
        DataHelper.getInstance().unregisterTrip();
        initTripIdSelectionIconLoader();
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TRIP_FRAGMENT_TAG);
        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        getSupportFragmentManager().executePendingTransactions();
        mBinding.activityMainInclude.activityMainSideContainer.setVisibility(View.GONE);
    }

    public void onTimeChanged(Date time) {
        if (mClockTextView != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(time);
            int hours = cal.get(Calendar.HOUR_OF_DAY);
            int minutes = cal.get(Calendar.MINUTE);
            mClockTextView.setText(Utility.getTwoDigitString(hours) + ":" + Utility.getTwoDigitString(minutes));
        }
    }

    /**
     * There must be already set train id
     */
    synchronized private void showTripIdDialogFragment() {
        showTripIdSelectionIconLoader();
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
                hideTripIdSelectionIconLoader();
            }

            @Override public void onFailure(Throwable t) {
                hideTripIdSelectionIconLoader();
            }
        });
    }

    private void showTripIdSelectionIconLoader() {
        MenuItem item = mMenu.findItem(R.id.menu_trip_selection);
        mAnimSet.setTarget(item.getActionView());
        mAnimSet.start();
        mTripSelectionIconEnabled = false; // Suppress functionality.
        item.setEnabled(false); // Suppress click animation.
    }

    private void hideTripIdSelectionIconLoader() {
        initTripIdSelectionIconLoader();
    }

    /**
     * Init trip id selection icon loader according to current trip id registration.
     */
    private void initTripIdSelectionIconLoader() {
        MenuItem item = mMenu.findItem(R.id.menu_trip_selection);

        // SET DEFAULT VALUES
        mAnimSet.end();
        item.getActionView().setRotation(0f);
        item.getActionView().setAlpha(1f);

        if (TRIP_NO_TRIP.equals(DataHelper.getInstance().getTripId())) {
            ((ImageView) item.getActionView()).setImageResource(R.drawable.ic_trip_selection_red);
            //Toast.makeText(this, R.string.activity_main_unsuccessful_trip_selection, Toast.LENGTH_SHORT).show();
        } else {
            ((ImageView) item.getActionView()).setImageResource(R.drawable.ic_trip_selection);
        }
        mTripSelectionIconEnabled = true;
        item.setEnabled(true);
    }

    private void onNavigationItemSelected(@StringRes int id) {
        if (id == R.string.nav_pois) { // POIs
            mBinding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(PoiActivity.newIntent(this));
        } else if (id == R.string.nav_categories) {
            mBinding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(CategoryActivity.newIntent(this));
        } else if (id == R.string.nav_logout) {
            mBinding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            Toast.makeText(this, R.string.message_successful_logout, Toast.LENGTH_SHORT).show();
            DataHelper.getInstance().unregister();
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
                if (!DataHelper.getInstance().isRegistered()) {
                    mBinding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT);
                    if (mShouldShowPasswordDialog) {
                        PasswordDialogFragment.newInstance().show(getSupportFragmentManager(), PASSWORD_DIALOG_FRAGMENT_TAG);
                        mShouldShowPasswordDialog = false;
                    }
                }
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

        //setDrawerState(false);
    }

    private void setDrawerState(boolean enabled) {
        if (enabled) {
            mBinding.activityMainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            mToggle.syncState();

        } else {
            mBinding.activityMainDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mToggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            mToggle.syncState();
        }
    }
}
