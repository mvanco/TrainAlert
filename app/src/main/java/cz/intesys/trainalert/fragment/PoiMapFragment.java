package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.FragmentPoiMapBinding;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.SimulatedRepository;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.viewmodel.PoiMapFragmentViewModel;

import static cz.intesys.trainalert.TaConfig.MAP_DEFAULT_ZOOM;

public class PoiMapFragment extends Fragment {

    private static final String POI_KEY = "cz.intesys.trainalert.poimapfragment.poi";

    private OnFragmentInteractionListener mListener;
    private FragmentPoiMapBinding mBinding;
    private PoiMapFragmentViewModel mViewModel;

    public interface OnFragmentInteractionListener {
        void onPoiSave(Poi poi);

    }

    public static PoiMapFragment newInstance() {
        PoiMapFragment fragment = new PoiMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static PoiMapFragment newInstance(Poi poi) {
        PoiMapFragment fragment = new PoiMapFragment();
        Bundle args = new Bundle();
        args.putParcelable(POI_KEY, poi);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PoiMapFragmentViewModel.class);
        getLifecycle().addObserver(SimulatedRepository.getInstance()); // TODO: change to real PostgreSqlRepository
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPoiMapBinding.inflate(inflater, container, false);
        mBinding.fab.setOnClickListener((view) -> onFabClick());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap(getActivity());
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    public void editPoi(Poi poi) {
        mBinding.fragmentMainMapview.getController().setCenter(poi);
        mBinding.fragmentMainMapview.getController().setZoom(MAP_DEFAULT_ZOOM);
        PoiMapInfoViewHandler poiHandler = new PoiMapInfoViewHandler(mBinding.fragmentMainPoiMapInfo);
        bindPoiMapInfo(poiHandler, poi);
    }

    private void bindPoiMapInfo(PoiMapInfoViewHandler poiHandler, Poi poi) {
        poiHandler.mIcon.setImageResource(poi.getPoiConfiguration().getMarkerDrawable());
        poiHandler.mTitle.setText(poi.getTitle());
        poiHandler.mLatitude.setText(String.valueOf(poi.getLatitude()));
        poiHandler.mLongitude.setText(String.valueOf(poi.getLongitude()));
        poiHandler.mOkButton.setOnClickListener((view) -> {
            // TODO: add implementation
        });
    }

    private void onFabClick() {
        setFreeMode(false);
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.
        mBinding.fragmentMainMapview.getController().setCenter(mViewModel.getLastLocation()); // Set map to current location.
        mBinding.fragmentMainMapview.setMultiTouchControls(true);
        mBinding.fragmentMainMapview.setTilesScaledToDpi(true);
        mBinding.fragmentMainMapview.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                setFreeMode(true);
                return onMapScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });
        mBinding.fragmentMainMapview.getController().setZoom(MAP_DEFAULT_ZOOM);
        mViewModel.enableLastLocation(this);

        mViewModel.getPois().observe(this, pois -> {
            mBinding.fragmentMainMapview.getOverlays().add(Utility.loadOverlayFromPois(pois, getActivity()));
        });

        if (getArguments() != null && getArguments().containsKey(POI_KEY)) {
            Poi poi = getArguments().getParcelable(POI_KEY);
            editPoi(poi);
        }
        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private boolean onMapScroll(ScrollEvent event) {
        IGeoPoint centerPoint = event.getSource().getMapCenter();
        EditText latitude = mBinding.fragmentMainPoiMapInfo.findViewById(R.id.poi_map_info_latitude);
        EditText longitude = mBinding.fragmentMainPoiMapInfo.findViewById(R.id.poi_map_info_longitude);
        latitude.setText(String.valueOf(centerPoint.getLatitude()));
        longitude.setText(String.valueOf(centerPoint.getLongitude()));
        return true;
    }

    private boolean isFreeMode() {
        return mViewModel.isInFreeMode();
    }

    private void setFreeMode(boolean free) {
        if (free) {
            if (isFreeMode()) {
                return;
            }
            mBinding.fab.setImageResource(R.drawable.fab_gps_not_fixed);
            mViewModel.setInFreeMode(true);
        } else {
            mBinding.fragmentMainMapview.getController().setCenter(mViewModel.getLastLocation());
            mBinding.fab.setImageResource(R.drawable.fab_gps_fixed);
            mViewModel.setInFreeMode(false);
        }
    }

    private class PoiMapInfoViewHandler {
        private ImageView mIcon;
        private EditText mTitle;
        private EditText mLatitude;
        private EditText mLongitude;
        private Button mOkButton;

        /**
         * @param parent which views are searched in
         */
        public PoiMapInfoViewHandler(ViewGroup parent) {
            mIcon = parent.findViewById(R.id.poi_map_info_icon);
            mTitle = parent.findViewById(R.id.poi_map_info_title);
            mLatitude = parent.findViewById(R.id.poi_map_info_latitude);
            mLongitude = parent.findViewById(R.id.poi_map_info_longitude);
            mOkButton = parent.findViewById(R.id.poi_map_info_confirm_button);
        }

        public ImageView getIcon() {
            return mIcon;
        }

        public EditText getTitle() {
            return mTitle;
        }

        public EditText getLatitude() {
            return mLatitude;
        }

        public EditText getLongitude() {
            return mLongitude;
        }

        public Button getOkButton() {
            return mOkButton;
        }
    }
}
