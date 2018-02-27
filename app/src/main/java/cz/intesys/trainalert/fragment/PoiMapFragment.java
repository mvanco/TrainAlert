package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.adapter.PoiListAdapter;
import cz.intesys.trainalert.databinding.FragmentPoiMapBinding;
import cz.intesys.trainalert.entity.Location;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;
import cz.intesys.trainalert.viewmodel.PoiMapFragmentViewModel;

import static cz.intesys.trainalert.TaConfig.MAP_DEFAULT_ZOOM;

public class PoiMapFragment extends Fragment {

    public static final String POI_KEY = "cz.intesys.trainalert.poimapfragment.poi";
    public static final String MODE_KEY = "cz.intesys.trainalert.poimapfragment.mode";

    public static final int MODE_NONE = 0;
    public static final int MODE_EDIT_POI = 1; //edit poi
    public static final int MODE_ADD_POI = 2; //add poi

    private OnFragmentInteractionListener mListener;
    private FragmentPoiMapBinding mBinding;
    private PoiMapFragmentViewModel mViewModel;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({MODE_NONE, MODE_EDIT_POI, MODE_ADD_POI})
    public @interface PoiMapFragmentMode {
    }

    public interface OnFragmentInteractionListener extends PoiListAdapter.OnItemClickListener {
        void onPoiAdded(Poi poi);

        void onPoiEdited(long id, Poi poi);

        void onCategoryIconClick(@DataHelper.CategoryId int categoryId);
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
        getLifecycle().addObserver(mViewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPoiMapBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);

        // Click events. TODO: Make part of UI using data binding.
        mBinding.fragmentPoiMapFab.setOnClickListener((view) -> onFabClick());
        mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoConfirmButton.setOnClickListener(view -> {
            if (mViewModel.getMode() == MODE_ADD_POI) {
                mListener.onPoiAdded(mViewModel.getWorkingPoi());
            } else if (mViewModel.getMode() == MODE_EDIT_POI) {
                mListener.onPoiEdited(mViewModel.getWorkingPoi().getId(), mViewModel.getWorkingPoi());
            }
        });
        mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoIcon.setOnClickListener(v -> mListener.onCategoryIconClick(mViewModel.getWorkingPoi().getCategory()));

//        View.OnClickListener inputListener = v -> {
//
//        };
        TextWatcher tw = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do here.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mViewModel.setPoiTitle(s.toString());
//                mViewModel.setPoiCoordinates(
//                        Double.valueOf(mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoLatitude.getText().toString()),
//                        Double.valueOf(mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoLongitude.getText().toString())
//                );
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Nothing to do here.
            }
        };
        mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoTitle.addTextChangedListener(tw);
        mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoTitle.setOnClickListener(v ->
                mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoTitle.setText("")
        );

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Swich fragment to editPoi mode and pripare UI
     *
     * @param poi
     */
    public void editPoi(Poi poi) {
        mBinding.fragmentPoiMapMapView.getController().setCenter(poi);
        mViewModel.setWorkingPoi(poi);
        mViewModel.setMode(MODE_EDIT_POI);
        mViewModel.setPoiId(poi.getId());
    }

    /**
     * Swich fragment to addPoi mode and pripare UI
     */
    public void addPoi() {
        mBinding.fragmentPoiMapMapView.getController().setCenter(mViewModel.getLocation());
        mViewModel.setWorkingPoi(new Poi(mViewModel.getLocation()));
        mViewModel.setMode(MODE_ADD_POI);
    }

    public void showNewPoiAddedNotification() {
        showNotification(R.string.new_poi_added);
    }

    public void showNewPoiEditedNotification() {
        showNotification(R.string.new_poi_edited);
    }

    public void loadPois(List<Poi> pois) {
        if (mBinding.fragmentPoiMapMapView.getOverlays().size() > 1) {
            mBinding.fragmentPoiMapMapView.getOverlays().remove(1);
        }

        ItemizedIconOverlay.OnItemGestureListener onItemGestureListener = new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
            @Override
            public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                Toast.makeText(getActivity(), item.getTitle(), Toast.LENGTH_LONG).show();
                return true;
            }

            @Override
            public boolean onItemLongPress(final int index, final OverlayItem item) {
                mListener.onPoiSelect(pois.get(index));
                return true;
            }
        };
        mBinding.fragmentPoiMapMapView.getOverlays().clear();
        mBinding.fragmentPoiMapMapView.getOverlays().add(Utility.loadOverlayFromPois(pois, onItemGestureListener, getActivity()));
        mBinding.fragmentPoiMapMapView.invalidate();
    }

    public void setCategory(int categoryId) {
        mViewModel.setPoiCategory(categoryId);
    }


    private void showNotification(@StringRes int text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
//        mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoRoot.setVisibility(View.INVISIBLE);
//        mBinding.fragmentPoiMapNewPoiAddedInclude.newPoiAddedRoot.setVisibility(View.VISIBLE);
//        mBinding.fragmentPoiMapNewPoiAddedInclude.newPoiAddedTextView.setText(text);
//        new Handler().postDelayed(() -> {
//            mBinding.fragmentPoiMapPoiMapInfoInclude.poiMapInfoRoot.setVisibility(View.VISIBLE);
//            mBinding.fragmentPoiMapNewPoiAddedInclude.newPoiAddedRoot.setVisibility(View.INVISIBLE);
//        }, 3000);
    }

    private void onFabClick() {
        setFreeMode(false);
    }

    private void initMap() {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.
        mBinding.fragmentPoiMapMapView.getController().setCenter(mViewModel.getLocation()); // Set map to current location.
        mBinding.fragmentPoiMapMapView.setMultiTouchControls(true);
        mBinding.fragmentPoiMapMapView.setTilesScaledToDpi(true);
        mBinding.fragmentPoiMapMapView.setMapListener(new MapListener() {
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
        mBinding.fragmentPoiMapMapView.getController().setZoom(MAP_DEFAULT_ZOOM);

        mViewModel.getPoisLiveData().observe(this, pois -> loadPois(pois));

        if (getArguments() != null && getArguments().containsKey(POI_KEY)) {
            Poi poi = getArguments().getParcelable(POI_KEY);
            editPoi(poi);
        }

        Observer<Location> observer = new Observer<Location>() {
            @Override
            public void onChanged(@Nullable Location location) {
                handleMode();
                mViewModel.getLocationLiveData().removeObserver(this);
            }
        };
        mViewModel.getLocationLiveData().observe(this, observer);

        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.
    }

    private void handleMode() {
        if (getArguments() == null || !getArguments().containsKey(MODE_KEY)) {
            return;
        }

        @PoiMapFragmentMode int mode = getArguments().getInt(MODE_KEY);
        switch (mode) {
            case MODE_ADD_POI:
                addPoi();
                break;
        }
    }

    private boolean onMapScroll(ScrollEvent event) {
        IGeoPoint centerPoint = event.getSource().getMapCenter();
        mViewModel.setPoiCoordinates(centerPoint.getLatitude(), centerPoint.getLongitude());

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
            mBinding.fragmentPoiMapFab.setImageResource(R.drawable.fab_gps_not_fixed);
            mViewModel.setInFreeMode(true);
        } else {
            mBinding.fragmentPoiMapMapView.getController().setCenter(mViewModel.getLocation());
            mBinding.fragmentPoiMapFab.setImageResource(R.drawable.fab_gps_fixed);
            mViewModel.setInFreeMode(false);
        }
    }
}
