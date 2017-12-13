package cz.intesys.trainalert.fragment;

import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
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
import cz.intesys.trainalert.animation.GeoPointInterpolator;
import cz.intesys.trainalert.databinding.FragmentMainBinding;
import cz.intesys.trainalert.entity.Alarm;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.PostgreSqlRepository;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.viewmodel.MainFragmentViewModel;

import static cz.intesys.trainalert.TaConfig.GPS_TIME_INTERVAL;
import static cz.intesys.trainalert.utility.Utility.convertToDegrees;
import static cz.intesys.trainalert.utility.Utility.getMarkerRotation;
import static cz.intesys.trainalert.utility.Utility.playSound;

public class MainFragment extends Fragment {

    private FragmentMainBinding mBinding;
    private MainFragmentViewModel mViewModel;

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);
        getLifecycle().addObserver(PostgreSqlRepository.getInstance());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentMainBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initAnimation();
    }

    /**
     * Note: Loading of current location is started  automatically in this.onResume() due to added lifecycle observer to PostgreSqlRepository singleton.
     * Warning: Works with activity {@link Context}, activity must be already attached!
     */
    private void initAnimation() {
        //mViewModel.loadPOIs(getActivity());
        initMap(getActivity()); // Initialize map using osmdroid library and set current position on the map.
        final Marker trainMarker = getTrainMarker(mBinding.fragmentMainMapview);
        mBinding.fragmentMainMapview.getOverlayManager().add(trainMarker); // Add train marker.
        mViewModel.getCurrentLocation().observe(this, currentLocation -> handleLocationChange(trainMarker, currentLocation));
        mViewModel.getPois().observe(this, pois -> handlePOIsChange(pois));
        mViewModel.loadPOIs();
    }

    /**
     * Creates marker which will be animated
     *
     * @param mapView where marker will be created
     * @return marker
     */
    private Marker getTrainMarker(MapView mapView) {
        Marker trainMarker = new Marker(mapView);
        trainMarker.setTitle("Train LocationAPI");
        trainMarker.setPosition(mViewModel.getStarterLocation().toGeoPoint());
        trainMarker.setIcon(getResources().getDrawable(R.drawable.marker_train_left));
        return trainMarker;
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.
        mBinding.fragmentMainMapview.setMultiTouchControls(true);
        mBinding.fragmentMainMapview.setTilesScaledToDpi(true);
        IMapController mapController = mBinding.fragmentMainMapview.getController();
        mapController.setZoom(14);
        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private void handleLocationChange(Marker trainMarker, Location currentLocation) {
        Log.d("handler", "executing repetitious code from to position " + currentLocation.getLatitude() + ", " + currentLocation.getLongitude());
        animateMarkerTo(mBinding.fragmentMainMapview, trainMarker, currentLocation.toGeoPoint(), new GeoPointInterpolator.LinearFixed());
        mBinding.fragmentMainMapview.getController().setCenter(currentLocation.toGeoPoint());
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

    /**
     * Warning: Works with activity {@link Context}, activity must be already attached!
     *
     * @param currentLocation
     */
    private void handleNotification(GeoPoint currentLocation) {
        if (getActivity() == null || !mViewModel.areLoadedPois()) { // If not attached or not loaded POIs yet.
            return;
        }
        for (Poi poi : mViewModel.getPois().getValue()) {
            Log.d("distance", "distance to " + poi.getMetaIndex() + " is " + currentLocation.distanceTo(poi));
            for (Alarm alarm : poi.getPOIConfiguration().getAlarmList()) {
                if (alarm.isDisabled()) {
                    continue;
                }
                if (currentLocation.distanceTo(poi) < alarm.getDistance()) {
                    Log.d("showNotification", "poi: " + poi.getMetaIndex() + ", distance: " + alarm.getDistance());
                    showTravelNotification(alarm);
                    mViewModel.disableAlarm(alarm);
                }
            }
        }

        // Enable alarm of Poi with sufficient distance again
        List<Alarm> alarmsToRemove = new ArrayList<Alarm>();

        for (Alarm alarm : mViewModel.getDisabledAlarms()) {
            if (currentLocation.distanceTo(alarm.getPoi()) > alarm.getDistance()) {
                alarmsToRemove.add(alarm);
            }
        }

        for (Alarm alarm : alarmsToRemove) {
            mViewModel.enableAlarm(alarm);
        }
    }

    private void showTravelNotification(Alarm alarm) {
        mBinding.fragmentMainNotificationText.setText(alarm.getMessageText(getActivity()));
        mBinding.fragmentMainNotificationContainer.setVisibility(View.VISIBLE);
        playSound(getActivity());

        Runnable action = () -> mBinding.fragmentMainNotificationContainer.setVisibility(View.GONE);
        mBinding.fragmentMainNotificationContainer.postDelayed(action, 3000);
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

        final ArrayList<OverlayItem> items = new ArrayList<>();
        for (Poi poi : pois) {
            OverlayItem item = new OverlayItem(poi.getTitle(), "", poi);
            item.setMarker(getActivity().getResources().getDrawable(poi.getPOIConfiguration().getMarkerDrawable()));
            item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
            items.add(item);
        }

        ItemizedOverlay<OverlayItem> overlay = new ItemizedIconOverlay<>(items, getResources().getDrawable(R.drawable.poi_crossing),
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

        mBinding.fragmentMainMapview.getOverlays().add(overlay);
    }

//    private void handleNotification(int metaIndex) {
//        switch (metaIndex) {
//            case 3:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 29:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 37:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 39:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 45:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 54:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 60:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 63:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 67:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 71:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 74:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 77:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 90:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 93:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 105:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 110:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 124:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 129:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 130:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 134:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 140:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 143:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 146:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 149:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 153:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 157:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 159:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 167:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 177:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 179:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 182:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 184:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 186:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 189:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 202:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 209:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 212:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 214:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 217:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 221:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 227:
//                showTravelNotification("Přejezd za 200m");
//                break;
//            case 230:
//                showTravelNotification("Přejezd za 200m");
//                break;
//        }
//    }

    public void restartAnimation(Context context) {
        mBinding.fragmentMainMapview.removeAllViews();
        mViewModel.restartRepository();
        initAnimation();
    }
}