package cz.intesys.trainalert.fragment;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.config.IConfigurationProvider;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.IRegisterReceiver;
import org.osmdroid.tileprovider.MapTile;
import org.osmdroid.tileprovider.MapTileProviderArray;
import org.osmdroid.tileprovider.modules.MapTileDownloader;
import org.osmdroid.tileprovider.modules.MapTileFilesystemProvider;
import org.osmdroid.tileprovider.modules.MapTileModuleProviderBase;
import org.osmdroid.tileprovider.modules.NetworkAvailabliltyCheck;
import org.osmdroid.tileprovider.modules.TileWriter;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.util.SimpleRegisterReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.animation.GeoPointInterpolator;
import cz.intesys.trainalert.databinding.FragmentMainBinding;
import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.viewmodel.MainFragmentViewModel;

import static cz.intesys.trainalert.TaConfig.GPS_TIME_INTERVAL;
import static cz.intesys.trainalert.TaConfig.MAP_DEFAULT_ZOOM;
import static cz.intesys.trainalert.TaConfig.OSMDROID_DEBUGGING;
import static cz.intesys.trainalert.TaConfig.REST_BASE_URL;
import static cz.intesys.trainalert.TaConfig.USE_OFFLINE_MAPS;
import static cz.intesys.trainalert.utility.Utility.convertToDegrees;
import static cz.intesys.trainalert.utility.Utility.getMarkerRotation;

public class MainFragment extends Fragment {

    private FragmentMainBinding mBinding;
    private MainFragmentViewModel mViewModel;
    private Marker mTrainMarker;
    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        // Prepared for possible future use.
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);
        new Handler().postDelayed(() -> getLifecycle().addObserver(mViewModel), TaConfig.LOCATION_INITIAL_DELAY);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        mBinding.fragmentMainFab.setOnClickListener(view -> onFabClick());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAnimation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mTrainMarker.setPosition(mViewModel.getLocation().toGeoPoint());
        setAnimating(true);
    }

    @Override public void onPause() {
        super.onPause();
        setAnimating(false);
    }

    public void restartAnimation(Context context) {
        setFabAsNotFixed();
    }

    public boolean isAnimating() {
        return mViewModel.isAnimating();
    }

    public void setAnimating(boolean shouldAnimating) {
        mViewModel.setAnimating(shouldAnimating);
    }

    private void showGpsUnavailableLoader() {
        mBinding.fragmentMainProgressBarInclude.setVisibility(View.VISIBLE);
        mBinding.fragmentMainFab.setVisibility(View.INVISIBLE);
    }

    private void hideGpsUnavailableLoader() {
        mBinding.fragmentMainProgressBarInclude.setVisibility(View.INVISIBLE);
        mBinding.fragmentMainFab.setVisibility(View.VISIBLE);
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.

        if (USE_OFFLINE_MAPS) {
            OnlineTileSourceBase tileSourceBase = new OnlineTileSourceBase(
                    "osm",
                    8,
                    16,
                    256,
                    ".png",
                    new String[]{REST_BASE_URL}
            ) {
                @Override public String getTileURLString(MapTile aTile) {
                    return getBaseUrl() + "/osm/tiles/" + aTile.getZoomLevel() + "/" + aTile.getX() + "/" + aTile.getY() + mImageFilenameEnding;
                }
            };

            final IRegisterReceiver registerReceiver = new SimpleRegisterReceiver(getActivity().getApplicationContext());
            final TileWriter tileWriter = new TileWriter();
            final MapTileFilesystemProvider fileSystemProvider = new MapTileFilesystemProvider(registerReceiver, tileSourceBase);
            final NetworkAvailabliltyCheck networkAvailabilityCheck = new NetworkAvailabliltyCheck(context);
            final MapTileDownloader downloaderProvider = new MapTileDownloader(tileSourceBase, tileWriter, networkAvailabilityCheck);
            MapTileProviderArray tileProviderArray = new MapTileProviderArray(tileSourceBase, registerReceiver, new MapTileModuleProviderBase[]{fileSystemProvider, downloaderProvider});

            IConfigurationProvider configuration = Configuration.getInstance();
            configuration.setDebugTileProviders(OSMDROID_DEBUGGING);
            configuration.setDebugMode(OSMDROID_DEBUGGING);
            configuration.setDebugMapTileDownloader(OSMDROID_DEBUGGING);

            mBinding.fragmentMainMapView.setUseDataConnection(false);
            mBinding.fragmentMainMapView.setTileProvider(tileProviderArray);
        }

        mBinding.fragmentMainMapView.setMultiTouchControls(true);
        mBinding.fragmentMainMapView.setTilesScaledToDpi(true);
        mBinding.fragmentMainMapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return onMapScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });

        mBinding.fragmentMainMapView.getController().setZoom(MAP_DEFAULT_ZOOM);

        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private void onFabClick() {
        setMapPosition(mViewModel.getLocation().toGeoPoint());
        setFabAsFixed();
    }

    /**
     * Note: Loading of current location is started  automatically in this.onResume() due to added lifecycle observer to PostgreSqlRepository singleton.
     * Warning: Works with activity {@link Context}, activity must be already attached!
     */
    private void initAnimation() {
        initMap(getActivity()); // Initialize map using osmdroid library and set current position on the map.
        initTrainMarker(mBinding.fragmentMainMapView);
        mBinding.fragmentMainMapView.getOverlayManager().add(mTrainMarker); // Add train marker.
        mViewModel.getLocationLiveData().observe(this, currentLocation -> {
            handleLocationChange(mTrainMarker, currentLocation);
            hideGpsUnavailableLoader();
        });
        mViewModel.getPoisLiveData().observe(this, pois -> {
            handlePOIsChange(pois);
        });

        mViewModel.getGpsTimeoutLiveData(this).subscribe((show) -> {
            if (show) {
                showGpsUnavailableLoader();
            }
        });

        mViewModel.getTrainStatusLiveData().observe(this, trainStatus -> {
            setSpeedLimit(trainStatus.getSpeedLimit());
        });
    }

    private boolean onMapScroll(ScrollEvent event) {
        if (event.getY() == 0 || event.getX() == 0) { // Filter weird scroll events.
            return true;
        }

        IGeoPoint centerPoint = event.getSource().getMapCenter();
        if (mViewModel.isShouldSwitchToFreeMode()) {
            setFabAsNotFixed(); // Only after second confirmation of free movement, it is in real "free mode".
            mViewModel.setFreeMode(true);
        } else {
            mViewModel.setShouldSwitchToFreeMode(true);
        }
        return true;
    }

    /**
     * Creates marker which will be animated
     *
     * @param mapView where marker will be created
     * @return marker
     */
    private void initTrainMarker(MapView mapView) {
        if (mTrainMarker == null) {
            mTrainMarker = new Marker(mapView);
            mTrainMarker.setTitle("Train LocationApi");
            mTrainMarker.setPosition(mViewModel.getLocation().toGeoPoint());
            mTrainMarker.setIcon(getResources().getDrawable(R.drawable.ic_current_location));
            mTrainMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        }
    }

    private void handleLocationChange(Marker trainMarker, Location currentLocation) {
        Log.d("handler", "executing repetitious code from to position " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
        if (mViewModel.isAnimating()) {
            animateMarkerTo(mBinding.fragmentMainMapView, trainMarker, currentLocation.toGeoPoint(), new GeoPointInterpolator.Linear());
        } else {
            trainMarker.setPosition(currentLocation.toGeoPoint());
        }
        if (!mViewModel.isFreeMode()) {
            setMapPosition(currentLocation.toGeoPoint());
        }
        handleNotification(trainMarker.getPosition());

//        Poi passingPoi = mViewModel.getPassingPoi();
//        if (passingPoi != null) {
//            if (passingPoi.getCategory() > POI_TYPE_SPEED_LIMITATION_20 && passingPoi.getCategory() < POI_TYPE_SPEED_LIMITATION_70) {
//            setSpeedLimit(passingPoi.getCategory());
//            }
//        }
    }

    private void animateMarkerTo(final MapView map, final Marker marker, final GeoPoint finalPosition, final GeoPointInterpolator GeoPointInterpolator) {
        if (getActivity() == null) {
            return;
        }

        final GeoPoint startPosition = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();

        valueAnimator.addUpdateListener(animator -> {
            if (!mViewModel.isAnimating()) {
                animator.cancel();
                return;
            }
            float v = animator.getAnimatedFraction();
            Log.d("anim", "animation portion is " + v);
            GeoPoint newPosition = GeoPointInterpolator.interpolate(v, startPosition, finalPosition); //TODO: Make this using
            marker.setPosition(newPosition);
            map.invalidate();
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setRepeatCount(0);
        valueAnimator.setDuration(GPS_TIME_INTERVAL);
        valueAnimator.start();
    }

    /**
     * Rotate train marker according to direction of movement.
     *
     * @param marker        to rotate
     * @param startPosition start position
     * @param finalPosition end position
     */
    private void rotateTrainMarker(final Marker marker, final GeoPoint startPosition, final GeoPoint finalPosition) {
        float markerRotation = convertToDegrees(getMarkerRotation(startPosition, finalPosition));
        Log.d("markerRotation", String.format("startPosition %f %f, endPosition %f %f, markerRotation %f", startPosition.getLatitude(), startPosition.getLongitude(), finalPosition.getLatitude(), finalPosition.getLongitude(), markerRotation));

        if (Utility.isLeftTrainDirection(startPosition, finalPosition)) {
            marker.setRotation(markerRotation);
            marker.setIcon(getResources().getDrawable(R.drawable.marker_train_left));
        } else {
            marker.setRotation(markerRotation - 180);
            marker.setIcon(getResources().getDrawable(R.drawable.marker_train_right));
        }
    }

    /**
     * Warning: Works with activity {@link Context}, activity must be already attached!
     * TODO: Move to ViewModel.
     *
     * @param currentLocation
     */
    private void handleNotification(GeoPoint currentLocation) {
        if (getActivity() == null || !mViewModel.areLoadedPois()) { // If not attached or not loaded POIs yet.
            return;
        }

        for (Alarm currentAlarm : mViewModel.getCurrentAlarms()) {
            showTravelNotification(currentAlarm);
        }
    }

    private void showTravelNotification(Alarm alarm) {
        mBinding.fragmentMainSignView.setVisibility(View.VISIBLE);
        mBinding.fragmentMainSignView.setText(alarm.getMessage());
        mBinding.fragmentMainSignView.setGraphics(alarm.getGraphics());

        Utility.playSound(alarm.getRingtone(), getActivity());
        if (alarm.shouldVibrate()) {
            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                vibrator.vibrate(700);
            } else {
                VibrationEffect effect = VibrationEffect.createOneShot(700, 255);
                vibrator.vibrate(effect);
            }
        }

        Runnable hideNotificationAction = () -> mBinding.fragmentMainSignView.setVisibility(View.GONE);
        new Handler().postDelayed(hideNotificationAction, 3000);
    }

    /**
     * Warning: Works with activity {@link Context}, activity must be already attached!
     *
     * @param pois
     */
    private void handlePOIsChange(List<Poi> pois) {
        if (getActivity() == null) {
            return;
        }
        if (mBinding.fragmentMainMapView.getOverlays().size() > 1) {
            mBinding.fragmentMainMapView.getOverlays().remove(1);
        }

        ItemizedIconOverlay.OnItemGestureListener onItemGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                return false;
            }
        };
        mBinding.fragmentMainMapView.getOverlays().add(Utility.loadOverlayFromPois(pois, onItemGestureListener, getActivity()));
    }

    /**
     * Set map position programmatically and ensures correct handling of "free mode" (with blocking of setting free mode).
     * Warning: This function should be used every time {@link IMapController} setCenter() method is about to be called.
     *
     * @param newPosition
     */
    private void setMapPosition(GeoPoint newPosition) {
        mViewModel.setFreeMode(false);
        mViewModel.setShouldSwitchToFreeMode(false);
        mBinding.fragmentMainMapView.getController().setCenter(newPosition);
    }

    private void setFabAsFixed() {
        mBinding.fragmentMainFab.setImageResource(R.drawable.fab_gps_fixed);
    }

    private void setFabAsNotFixed() {
        mBinding.fragmentMainFab.setImageResource(R.drawable.fab_gps_not_fixed);
    }

    private void setSpeedLimit(@DataHelper.CategoryId int id) {
        mBinding.fragmentMainSpeedLimitView.setCategory(id);
        mBinding.fragmentMainSpeedLimitView.setVisibility(View.VISIBLE);
    }
}