package cz.intesys.trainalert.fragment;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TrainAlertConfig;
import cz.intesys.trainalert.animation.GeoPointInterpolator;
import cz.intesys.trainalert.databinding.FragmentMainBinding;
import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.POI;
import cz.intesys.trainalert.entity.TDAOverlayItem;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.viewmodel.MainFragmentViewModel;

import static cz.intesys.trainalert.TrainAlertConfig.GPS_TIME_INTERVAL;
import static cz.intesys.trainalert.utility.Utility.convertToDegrees;
import static cz.intesys.trainalert.utility.Utility.getMarkerRotation;
import static cz.intesys.trainalert.utility.Utility.playSound;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private MainFragmentViewModel viewModel;
    private LocationPoller mLocationPoller;
    private Marker trainMarker;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAnimation();
    }

    private void initAnimation() {
        viewModel.loadPOIs(getActivity());
        initMap(getActivity());
        final Marker trainMarker = getTrainMarker(binding.fragmentMainMapview);
        binding.fragmentMainMapview.getOverlayManager().add(trainMarker);
        mLocationPoller = new LocationPoller(() -> handleLocationChange(trainMarker));
    }

    /**
     * Creates marker which will be animated
     *
     * @param mapView where marker will be created
     * @return marker
     */
    private Marker getTrainMarker(MapView mapView) {
        Marker trainMarker = new Marker(mapView);
        trainMarker.setTitle("Train Location");
        trainMarker.setPosition(viewModel.getCurrentLocation().toGeoPoint());
        trainMarker.setIcon(getResources().getDrawable(R.drawable.marker_train_left));
        return trainMarker;
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.

        binding.fragmentMainMapview.setMultiTouchControls(true);
        binding.fragmentMainMapview.setTilesScaledToDpi(true);
        IMapController mapController = binding.fragmentMainMapview.getController();

        final ArrayList<OverlayItem> items = new ArrayList<>();
        for (POI poi : viewModel.getPOIs().getValue()) {
            TDAOverlayItem item = new TDAOverlayItem(poi);
            item.setMarker(context.getResources().getDrawable(poi.getPOIConfiguration().getMarkerDrawable()));
            item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
            items.add(item);
        }

        ItemizedOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(items, getResources().getDrawable(R.drawable.poi_crossing),
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(
                                context,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got single tapped up", Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(
                                context,
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got long pressed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, context.getApplicationContext());

        binding.fragmentMainMapview.getOverlays().add(overlay);

        mapController.setZoom(14);
        mapController.setCenter(viewModel.getCurrentLocation());

        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private void handleLocationChange(Marker trainMarker) {
        Location currentLocation = viewModel.getCurrentLocation();
        Log.d("handler", "executing repetitious code from to position with metaindex " + currentLocation.getMetaIndex());
        animateMarkerTo(binding.fragmentMainMapview, trainMarker, currentLocation.toGeoPoint(), new GeoPointInterpolator.LinearFixed());
        binding.fragmentMainMapview.getController().setCenter(currentLocation.toGeoPoint());
        handleNotification(trainMarker.getPosition());
    }

    private void animateMarkerTo(final MapView map, final Marker marker, final GeoPoint finalPosition, final GeoPointInterpolator GeoPointInterpolator) {
        if (getActivity() == null) {
            return;
        }

        final GeoPoint startPosition = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();

        valueAnimator.addUpdateListener(animation -> {
            float v = animation.getAnimatedFraction();
            Log.d("anim", "animation portion is " + v);
            GeoPoint newPosition = GeoPointInterpolator.interpolate(v, startPosition, finalPosition); //TODO: make this using
            marker.setPosition(newPosition);
            map.invalidate();
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setRepeatCount(0);
        valueAnimator.setDuration(GPS_TIME_INTERVAL);
        valueAnimator.start();

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

    private void handleNotification(GeoPoint currentLocation) {
        for (POI poi : viewModel.getPOIs().getValue()) {
            Log.d("distance", "distance to " + poi.getMetaIndex() + " is " + currentLocation.distanceTo(poi));
            for (Alarm alarm : poi.getPOIConfiguration().getAlarmList()) {
                if (alarm.isDisabled()) {
                    continue;
                }
                if (currentLocation.distanceTo(poi) < alarm.getDistance()) {
                    Log.d("showNotification", "poi: " + poi.getMetaIndex() + ", distance: " + alarm.getDistance());
                    showTravelNotification(alarm.getMessage());
                    viewModel.disableAlarm(alarm);
                }
            }
        }

        // Enable alarm of POI with sufficient distance again
        List<Alarm> alarmsToRemove = new ArrayList<Alarm>();

        for (Alarm alarm : viewModel.getDisabledAlarms()) {
            if (currentLocation.distanceTo(alarm.getPoi()) > alarm.getDistance()) {
                alarmsToRemove.add(alarm);
            }
        }

        for (Alarm alarm : alarmsToRemove) {
            viewModel.enableAlarm(alarm);
        }
    }

    private void showTravelNotification(String text) {
        binding.fragmentMainNotificationText.setText(text);
        binding.fragmentMainNotificationContainer.setVisibility(View.VISIBLE);
        playSound(getActivity());

        Runnable action = () -> binding.fragmentMainNotificationContainer.setVisibility(View.GONE);
        binding.fragmentMainNotificationContainer.postDelayed(action, 3000);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationPoller.startPolling();
    }

    @Override
    public void onStop() {
        super.onStop();
        mLocationPoller.stopPolling();
    }

    public void restartAnimation(Context context) {
        mLocationPoller.stopPolling();
        binding.fragmentMainMapview.removeAllViews();
        viewModel.restartRepository();
        initAnimation();
        mLocationPoller.startPolling();
    }

    private void handleNotification(int metaIndex) {
        switch (metaIndex) {
            case 3:
                showTravelNotification("Přejezd za 200m");
                break;
            case 29:
                showTravelNotification("Přejezd za 200m");
                break;
            case 37:
                showTravelNotification("Přejezd za 200m");
                break;
            case 39:
                showTravelNotification("Přejezd za 200m");
                break;
            case 45:
                showTravelNotification("Přejezd za 200m");
                break;
            case 54:
                showTravelNotification("Přejezd za 200m");
                break;
            case 60:
                showTravelNotification("Přejezd za 200m");
                break;
            case 63:
                showTravelNotification("Přejezd za 200m");
                break;
            case 67:
                showTravelNotification("Přejezd za 200m");
                break;
            case 71:
                showTravelNotification("Přejezd za 200m");
                break;
            case 74:
                showTravelNotification("Přejezd za 200m");
                break;
            case 77:
                showTravelNotification("Přejezd za 200m");
                break;
            case 90:
                showTravelNotification("Přejezd za 200m");
                break;
            case 93:
                showTravelNotification("Přejezd za 200m");
                break;
            case 105:
                showTravelNotification("Přejezd za 200m");
                break;
            case 110:
                showTravelNotification("Přejezd za 200m");
                break;
            case 124:
                showTravelNotification("Přejezd za 200m");
                break;
            case 129:
                showTravelNotification("Přejezd za 200m");
                break;
            case 130:
                showTravelNotification("Přejezd za 200m");
                break;
            case 134:
                showTravelNotification("Přejezd za 200m");
                break;
            case 140:
                showTravelNotification("Přejezd za 200m");
                break;
            case 143:
                showTravelNotification("Přejezd za 200m");
                break;
            case 146:
                showTravelNotification("Přejezd za 200m");
                break;
            case 149:
                showTravelNotification("Přejezd za 200m");
                break;
            case 153:
                showTravelNotification("Přejezd za 200m");
                break;
            case 157:
                showTravelNotification("Přejezd za 200m");
                break;
            case 159:
                showTravelNotification("Přejezd za 200m");
                break;
            case 167:
                showTravelNotification("Přejezd za 200m");
                break;
            case 177:
                showTravelNotification("Přejezd za 200m");
                break;
            case 179:
                showTravelNotification("Přejezd za 200m");
                break;
            case 182:
                showTravelNotification("Přejezd za 200m");
                break;
            case 184:
                showTravelNotification("Přejezd za 200m");
                break;
            case 186:
                showTravelNotification("Přejezd za 200m");
                break;
            case 189:
                showTravelNotification("Přejezd za 200m");
                break;
            case 202:
                showTravelNotification("Přejezd za 200m");
                break;
            case 209:
                showTravelNotification("Přejezd za 200m");
                break;
            case 212:
                showTravelNotification("Přejezd za 200m");
                break;
            case 214:
                showTravelNotification("Přejezd za 200m");
                break;
            case 217:
                showTravelNotification("Přejezd za 200m");
                break;
            case 221:
                showTravelNotification("Přejezd za 200m");
                break;
            case 227:
                showTravelNotification("Přejezd za 200m");
                break;
            case 230:
                showTravelNotification("Přejezd za 200m");
                break;
        }
    }

    private class LocationPoller {
        private Handler mHandler;
        private Runnable mPeriodicUpdateRunnable;

        LocationPoller(Runnable locationChangedRunnable) {
            mHandler = new Handler();
            mPeriodicUpdateRunnable = () -> {
                locationChangedRunnable.run();
                mHandler.postDelayed(mPeriodicUpdateRunnable, TrainAlertConfig.GPS_TIME_INTERVAL);
            };
        }

        void startPolling() {
            mPeriodicUpdateRunnable.run();
        }

        void stopPolling() {
            mHandler.removeCallbacks(mPeriodicUpdateRunnable);
        }
    }

    //    /**
//     * Alternative method for calling repetitious animations of train move
//     * @param trainMarker marker which should be moved
//     * @param counter always zero when called outside, used by this recursive function for proper functionality
//     */
//    private void runAnimation(final Marker trainMarker, final int counter) {
//        Location currentLocation = repository.getCurrentLocation();
//        //handleNotification(currentLocation.getMetaIndex());
//
//        if (counter == 5) {
//            binding.fragmentMainMapview.getController().setCenter(currentLocation.toGeoPoint());
//        }
//
//        ValueAnimator valueAnimator = animateMarkerTo(binding.fragmentMainMapview, trainMarker, currentLocation.toGeoPoint(), new GeoPointInterpolator.Linear());
//
//        valueAnimator.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                Log.d("anim", "end of portion animation");
//                if (counter == 5) {
//                    runAnimation(trainMarker, 0);
//                } else {
//                    runAnimation(trainMarker, counter + 1);
//                }
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//            }
//        });
//
//    }
}