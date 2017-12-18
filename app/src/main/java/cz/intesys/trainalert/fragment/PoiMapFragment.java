package cz.intesys.trainalert.fragment;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;

import cz.intesys.trainalert.databinding.FragmentPoiMapBinding;
import cz.intesys.trainalert.entity.Poi;

import static cz.intesys.trainalert.TaConfig.MAP_DEFAULT_ZOOM;

public class PoiMapFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FragmentPoiMapBinding mBinding;

    public interface OnFragmentInteractionListener {
        void onPoiSave(Poi poi);
    }


    public static PoiMapFragment newInstance(String param1, String param2) {
        PoiMapFragment fragment = new PoiMapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPoiMapBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initMap(getActivity());
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    private void initMap(Context context) {
        Configuration.getInstance().load(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Load configuration.
        mBinding.fragmentMainMapview.setMultiTouchControls(true);
        mBinding.fragmentMainMapview.setTilesScaledToDpi(true);
        mBinding.fragmentMainMapview.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                return onMapScroll(event);
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                return false;
            }
        });
        mBinding.fragmentMainMapview.getController().setZoom(MAP_DEFAULT_ZOOM);
        Configuration.getInstance().save(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity())); // Save configuration.
    }

    private boolean onMapScroll(ScrollEvent event) {
        IGeoPoint centerPoint = event.getSource().getMapCenter();

        return true;
    }
}
