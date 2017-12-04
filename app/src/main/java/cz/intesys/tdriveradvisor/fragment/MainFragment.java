package cz.intesys.tdriveradvisor.fragment;

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

import cz.intesys.tdriveradvisor.R;
import cz.intesys.tdriveradvisor.animation.GeoPointInterpolator;
import cz.intesys.tdriveradvisor.databinding.FragmentMainBinding;
import cz.intesys.tdriveradvisor.entity.Alarm;
import cz.intesys.tdriveradvisor.entity.Location;
import cz.intesys.tdriveradvisor.entity.POI;
import cz.intesys.tdriveradvisor.entity.TDAOverlayItem;
import cz.intesys.tdriveradvisor.utility.Utility;
import cz.intesys.tdriveradvisor.viewmodel.MainFragmentViewModel;

import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.GPS_TIME_INTERVAL;
import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.INFINITE_ANIMATION;
import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.TIME_OF_ANIMATION;
import static cz.intesys.tdriveradvisor.utility.Utility.convertToDegrees;
import static cz.intesys.tdriveradvisor.utility.Utility.getMapRefreshGPSCycles;
import static cz.intesys.tdriveradvisor.utility.Utility.getMarkerRotation;
import static cz.intesys.tdriveradvisor.utility.Utility.playSound;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private MainFragmentViewModel viewModel;

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


        viewModel.loadPOIs(getActivity());
        viewModel.getPOIs().observe(this, (POIs) -> {
            // Nothing yet.
        });

        initMap(getActivity());

        // Creates marker which will be animated
        final Marker trainMarker = new Marker(binding.fragmentMainMapview);
        trainMarker.setTitle("Train Location");
        trainMarker.setPosition(viewModel.getCurrentLocation().toGeoPoint());
        trainMarker.setIcon(getResources().getDrawable(R.drawable.ic_train_left));
        binding.fragmentMainMapview.getOverlayManager().add(trainMarker);
        startAnimation(trainMarker);

    }

    public void restartAnimation(Context context) {
        // TODO: restart repository from viewmodel
        binding.fragmentMainMapview.getController().setCenter(viewModel.getCurrentLocation());

        binding.fragmentMainMapview.removeAllViews();

        if (!INFINITE_ANIMATION) {
            initMap(context);

            // Creates marker which will be animated
            final Marker trainMarker = new Marker(binding.fragmentMainMapview);
            trainMarker.setTitle("Train Location");
            trainMarker.setPosition(viewModel.getCurrentLocation().toGeoPoint());
            trainMarker.setIcon(getResources().getDrawable(R.drawable.ic_train_left));
            binding.fragmentMainMapview.getOverlayManager().add(trainMarker);
            startAnimation(trainMarker);
        }
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.

        binding.fragmentMainMapview.setMultiTouchControls(true);
        binding.fragmentMainMapview.setTilesScaledToDpi(true);
        IMapController mapController = binding.fragmentMainMapview.getController();

        final ArrayList<OverlayItem> items = new ArrayList<>();
        for (POI poi : viewModel.getPOIs().getValue()) {
            items.add(new TDAOverlayItem(poi));
        }

        ItemizedOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(items, getResources().getDrawable(R.drawable.ic_clear),
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

    //TODO: make with Service or infinite loop, not recursive function (looks bad)
    private void startAnimation(final Marker trainMarker) {

        int numberOfRepetitions = (INFINITE_ANIMATION) ? 1 : (TIME_OF_ANIMATION * 1000) / GPS_TIME_INTERVAL;
        for (int i = 0; i < numberOfRepetitions; i++) {
            new Handler().postDelayed(() -> {
                Location currentLocation = viewModel.getCurrentLocation();
                animateMarkerTo(binding.fragmentMainMapview, trainMarker, currentLocation.toGeoPoint(), new GeoPointInterpolator.LinearFixed());
                if ((currentLocation.getMetaIndex() % getMapRefreshGPSCycles()) == 0) {
                    binding.fragmentMainMapview.getController().setCenter(currentLocation.toGeoPoint());
                }
                //handleNotification(currentLocation.getMetaIndex());
                handleNotification(trainMarker.getPosition());
                if (INFINITE_ANIMATION) {
                    startAnimation(trainMarker);
                }
            }, GPS_TIME_INTERVAL * (i + 1));
        }
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
//        int distance = startPosition.distanceTo(finalPosition);
//        int distanceFraction = distance / 250;
//        long duration = TDriverAdvisorConfig.TIME_SPEED * distanceFraction;
        valueAnimator.setDuration(GPS_TIME_INTERVAL);
        valueAnimator.start();

        float markerRotation = convertToDegrees(getMarkerRotation(startPosition, finalPosition));
        Log.d("markerRotation", String.format("startPosition %f %f, endPosition %f %f, markerRotation %f", startPosition.getLatitude(), startPosition.getLongitude(), finalPosition.getLatitude(), finalPosition.getLongitude(), markerRotation));

        if (Utility.isLeftTrainDirection(startPosition, finalPosition)) {
            marker.setRotation(markerRotation);
            marker.setIcon(getResources().getDrawable(R.drawable.ic_train_left));
        } else {
            marker.setRotation(markerRotation - 180);
            marker.setIcon(getResources().getDrawable(R.drawable.ic_train_right));
        }
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

    private void showTravelNotification(String text) {
        binding.fragmentMainNotificationText.setText(text);
        binding.fragmentMainNotificationContainer.setVisibility(View.VISIBLE);
        playSound(getActivity());

        Runnable action = () -> binding.fragmentMainNotificationContainer.setVisibility(View.GONE);
        binding.fragmentMainNotificationContainer.postDelayed(action, 3000);
    }

    private void handleNotification(GeoPoint currentLocation) {
        for (POI poi : viewModel.getPOIs().getValue()) {
            Log.d("distance", "distance to " + poi.getMetaIndex() + " is " + currentLocation.distanceTo(poi));
            for (Alarm alarm : poi.getPOIType().getAlarmList()) {
                if (alarm.isDisabled()) {
                    continue;
                }
                if (currentLocation.distanceTo(poi) < alarm.getDistance()) {
                    Log.d("showNotification", "poi: " + poi.getMetaIndex() + ", distance: " + alarm.getDistance());
                    showTravelNotification(alarm.getMessage());
                    viewModel.disableNewAlarm(alarm);
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
            viewModel.enableDisabledAlarm(alarm);
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