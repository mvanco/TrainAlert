package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.adapter.PoiListAdapter;
import cz.intesys.trainalert.databinding.FragmentPoiMapBinding;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.viewmodel.PoiMapFragmentViewModel;

import static cz.intesys.trainalert.TaConfig.MAP_DEFAULT_ZOOM;
import static cz.intesys.trainalert.TaConfig.REPOSITORY;
import static cz.intesys.trainalert.viewmodel.PoiMapFragmentViewModel.MODE_EDIT_POI;

public class PoiMapFragment extends Fragment {

    public static final String POI_KEY = "cz.intesys.trainalert.poimapfragment.poi";
    public static final String MODE_KEY = "cz.intesys.trainalert.poimapfragment.mode";
    public static final int MODE_NORMAL = 0;
    public static final int MODE_NEW_POI = 1;
    private OnFragmentInteractionListener mListener;
    private FragmentPoiMapBinding mBinding;
    private PoiMapFragmentViewModel mViewModel;

    @Retention (RetentionPolicy.SOURCE)
    @IntDef ( {MODE_NORMAL, MODE_NEW_POI})
    public @interface PoiMapFragmentMode {
    }

    public interface OnFragmentInteractionListener extends PoiListAdapter.OnItemClickListener {
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

    public static PoiMapFragment newInstance(@PoiMapFragmentMode int mode) {
        PoiMapFragment fragment = new PoiMapFragment();
        Bundle args = new Bundle();
        args.putInt(MODE_KEY, mode);
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
        getLifecycle().addObserver(REPOSITORY);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPoiMapBinding.inflate(inflater, container, false);
        mBinding.fragmentMainFab.setOnClickListener((view) -> onFabClick());
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
        mBinding.fragmentMainMapView.getController().setCenter(poi);
        mBinding.fragmentMainMapView.getController().setZoom(MAP_DEFAULT_ZOOM);
        PoiMapInfoViewHandler poiHandler = new PoiMapInfoViewHandler(mBinding.fragmentMainPoiMapInfo);
        bindPoiMapInfo(poiHandler, poi);
        mViewModel.setMode(MODE_EDIT_POI);
        mViewModel.setPoiId(poi.getId());
    }

    public long getPoiId() {
        return mViewModel.getPoiId();
    }

    public void addPoi() {
        mBinding.fragmentMainMapView.getController().setCenter(mViewModel.getLastLocation());
        mBinding.fragmentMainMapView.getController().setZoom(MAP_DEFAULT_ZOOM);
        PoiMapInfoViewHandler poiHandler = new PoiMapInfoViewHandler(mBinding.fragmentMainPoiMapInfo);
        bindPoiMapInfo(poiHandler, getNewPoi(getActivity()));
        mViewModel.setMode(PoiMapFragmentViewModel.MODE_ADD_POI);
    }

    public @PoiMapFragmentViewModel.PoiActivityMode
    int getFragmentMode() {
        return mViewModel.getMode();
    }

    public Poi getNewPoi(Context context) {
        return new Poi(mViewModel.getLastLocation(), context);
    }

    public void showNewPoiAddedNotification() {
        showNotification(R.string.new_poi_added);
    }

    public void showNewPoiEditedNotification() {
        showNotification(R.string.new_poi_edited);
    }

    private void bindPoiMapInfo(PoiMapInfoViewHandler poiHandler, Poi poi) { // TODO: make as custom view
        poiHandler.mIcon.setImageResource(poi.getMarkerDrawable());
        poiHandler.mTitle.setText(poi.getTitle());
        poiHandler.mLatitude.setText(String.valueOf(poi.getLatitude()));
        poiHandler.mLongitude.setText(String.valueOf(poi.getLongitude()));
        poiHandler.mOkButton.setOnClickListener((view) -> mListener.onPoiSave(obtainPoi(poiHandler)));
    }

    private Poi obtainPoi(PoiMapInfoViewHandler handler) {
        return new Poi(handler.getTitle().getText().toString(),
                handler.getLatitude().getText().toString(),
                handler.getLongitude().getText().toString(),
                Utility.POI_TYPE_CROSSING);
    }

    private void showNotification(@StringRes int text) {
        ConstraintLayout poiMapInfo = mBinding.fragmentMainPoiMapInfo.findViewById(R.id.poiMapInfo_root);
        ConstraintLayout newPoiAdded = mBinding.fragmentMainPoiMapInfo.findViewById(R.id.newPoiAdded_root);
        TextView tv = newPoiAdded.findViewById(R.id.newPoiAdded_textView);
        tv.setText(text);
        poiMapInfo.setVisibility(View.INVISIBLE);
        newPoiAdded.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            poiMapInfo.setVisibility(View.VISIBLE);
            newPoiAdded.setVisibility(View.INVISIBLE);
        }, 3000);
    }

    private void onFabClick() {
        setFreeMode(false);
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.
        mBinding.fragmentMainMapView.getController().setCenter(mViewModel.getLastLocation()); // Set map to current location.
        mBinding.fragmentMainMapView.setMultiTouchControls(true);
        mBinding.fragmentMainMapView.setTilesScaledToDpi(true);
        mBinding.fragmentMainMapView.setMapListener(new MapListener() {
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
        mBinding.fragmentMainMapView.getController().setZoom(MAP_DEFAULT_ZOOM);

        mViewModel.getPois().observe(this, pois -> {
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
//                    editPoi(mViewModel.getLastPois().get(index));
                    mListener.onPoiSelect(mViewModel.getLastPois().get(index));
                    return true;
                }
            };
            mBinding.fragmentMainMapView.getOverlays().add(Utility.loadOverlayFromPois(pois, onItemGestureListener, getActivity()));
        });

        if (getArguments() != null && getArguments().containsKey(POI_KEY)) {
            Poi poi = getArguments().getParcelable(POI_KEY);
            editPoi(poi);
        }

        Observer<Location> observer = new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                handleMode();
                mViewModel.getLocation().removeObserver(this);
            }
        };
        mViewModel.getLocation().observe(this, observer);

        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private void handleMode() {
        if (getArguments() == null || !getArguments().containsKey(MODE_KEY)) {
            return;
        }

        @PoiMapFragmentMode int mode = getArguments().getInt(MODE_KEY);
        switch (mode) {
            case MODE_NEW_POI:
                addPoi();
                break;
        }
    }

    private boolean onMapScroll(ScrollEvent event) {
        IGeoPoint centerPoint = event.getSource().getMapCenter();
        EditText latitude = mBinding.fragmentMainPoiMapInfo.findViewById(R.id.poiMapInfo_latitude);
        EditText longitude = mBinding.fragmentMainPoiMapInfo.findViewById(R.id.poiMapInfo_longitude);
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
            mBinding.fragmentMainFab.setImageResource(R.drawable.fab_gps_not_fixed);
            mViewModel.setInFreeMode(true);
        } else {
            mBinding.fragmentMainMapView.getController().setCenter(mViewModel.getLastLocation());
            mBinding.fragmentMainFab.setImageResource(R.drawable.fab_gps_fixed);
            mViewModel.setInFreeMode(false);
        }
    }

    public class PoiMapInfoViewHandler {
        private ImageView mIcon;
        private EditText mTitle;
        private EditText mLatitude;
        private EditText mLongitude;
        private Button mOkButton;

        /**
         * @param parent which views are searched in
         */
        public PoiMapInfoViewHandler(ViewGroup parent) {
            mIcon = parent.findViewById(R.id.poiMapInfo_icon);
            mTitle = parent.findViewById(R.id.poiMapInfo_title);
            mLatitude = parent.findViewById(R.id.poiMapInfo_latitude);
            mLongitude = parent.findViewById(R.id.poiMapInfo_longitude);
            mOkButton = parent.findViewById(R.id.poiMapInfo_confirmButton);
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

        private void hidePoiInfo() {
            mBinding.fragmentMainPoiMapInfo.setVisibility(View.GONE);
        }

        private void showPoiInfo() {
            mBinding.fragmentMainPoiMapInfo.setVisibility(View.VISIBLE);
        }
    }
}
