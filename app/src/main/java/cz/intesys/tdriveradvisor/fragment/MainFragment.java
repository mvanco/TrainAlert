package cz.intesys.tdriveradvisor.fragment;

import android.animation.ValueAnimator;
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

import cz.intesys.tdriveradvisor.R;
import cz.intesys.tdriveradvisor.animation.GeoPointInterpolator;
import cz.intesys.tdriveradvisor.databinding.FragmentMainBinding;
import cz.intesys.tdriveradvisor.entity.POI;
import cz.intesys.tdriveradvisor.entity.TDAOverlayItem;
import cz.intesys.tdriveradvisor.repository.Repository;
import cz.intesys.tdriveradvisor.repository.SimulatedRepository;
import cz.intesys.tdriveradvisor.utility.Utility;

import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.GPS_TIME_INTERVAL;
import static cz.intesys.tdriveradvisor.TDriverAdvisorConfig.TIME_OF_ANIMATION;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;
    private Repository repository; // TODO: Move this to ViewModel

    public MainFragment() {
        repository = SimulatedRepository.getInstance();
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
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

        initMap();
        startAnimation();
    }

    private void initMap() {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.

        binding.fragmentMainMapview.setMultiTouchControls(true);
        binding.fragmentMainMapview.setTilesScaledToDpi(true);
        IMapController mapController = binding.fragmentMainMapview.getController();

        final ArrayList<OverlayItem> items = new ArrayList<>();
        for (POI poi : repository.getPOIs()) {
            items.add(new TDAOverlayItem(poi));
        }

        ItemizedOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(items, getResources().getDrawable(R.drawable.ic_clear),
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        Toast.makeText(
                                getActivity(),
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got single tapped up", Toast.LENGTH_LONG).show();
                        return true; // We 'handled' this event.
                    }

                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        Toast.makeText(
                                getActivity(),
                                "Item '" + item.getTitle() + "' (index=" + index
                                        + ") got long pressed", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }, getActivity().getApplicationContext());

        binding.fragmentMainMapview.getOverlays().add(overlay);

        mapController.setZoom(14);
        mapController.setCenter(repository.getCurrentLocation());

        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private void startAnimation() {
        // Creates marker which will be animated
        final Marker trainMarker = new Marker(binding.fragmentMainMapview);
        trainMarker.setTitle("Train Location");
        trainMarker.setPosition(repository.getCurrentLocation().toGeoPoint());
        trainMarker.setIcon(getResources().getDrawable(R.drawable.ic_subway));
        binding.fragmentMainMapview.getOverlayManager().add(trainMarker);

        int numberOfRepetitions = (TIME_OF_ANIMATION * 1000) / GPS_TIME_INTERVAL;
        for (int i = 0; i < numberOfRepetitions; i++) {
            final int metaIndex = i;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GeoPoint currentLocation = repository.getCurrentLocation().toGeoPoint();
                    animateMarkerTo(binding.fragmentMainMapview, trainMarker, currentLocation, new GeoPointInterpolator.LinearFixed());
                    if ((metaIndex % Utility.getMapRefreshGPSCycles()) == 0) {
                        binding.fragmentMainMapview.getController().setCenter(currentLocation);
                    }
                    handleNotification(metaIndex);
                }
            }, GPS_TIME_INTERVAL * (i + 1));
        }
    }

    public ValueAnimator animateMarkerTo(final MapView map, final Marker marker, final GeoPoint finalPosition, final GeoPointInterpolator GeoPointInterpolator) {
        final GeoPoint startPosition = marker.getPosition();
        ValueAnimator valueAnimator = new ValueAnimator();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = animation.getAnimatedFraction();
                Log.d("anim", "animation portion is " + v);
                GeoPoint newPosition = GeoPointInterpolator.interpolate(v, startPosition, finalPosition);
                marker.setPosition(newPosition);
                map.invalidate();
            }
        });
        valueAnimator.setFloatValues(0, 1); // Ignored.
        valueAnimator.setRepeatCount(0);
//        int distance = startPosition.distanceTo(finalPosition);
//        int distanceFraction = distance / 250;
//        long duration = TDriverAdvisorConfig.TIME_SPEED * distanceFraction;
        valueAnimator.setDuration(GPS_TIME_INTERVAL);
        valueAnimator.start();
        return valueAnimator;
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

        Runnable action = new Runnable() {
            @Override
            public void run() {
                binding.fragmentMainNotificationContainer.setVisibility(View.GONE);
            }
        };
        binding.fragmentMainNotificationContainer.postDelayed(action, 3000);
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