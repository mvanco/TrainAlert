package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.intesys.trainalert.adapter.PoiListAdapter;
import cz.intesys.trainalert.databinding.FragmentPoiListBinding;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.viewmodel.PoiListFragmentViewModel;

public class PoiListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FragmentPoiListBinding mBinding;
    private RecyclerView.LayoutManager mLayoutManager;
    private PoiListFragmentViewModel mViewModel;
    private PoiListAdapter mAdapter;

    public interface OnFragmentInteractionListener extends PoiListAdapter.OnItemClickListener {
        void onPoiAdd();
    }

    public PoiListFragment() {
    }

    public static PoiListFragment newInstance() {
        PoiListFragment fragment = new PoiListFragment();
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PoiListFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPoiListBinding.inflate(inflater, container, false);
        mBinding.fragmentPoiListFab.setOnClickListener((view) -> mListener.onPoiAdd());
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.fragmentPoiListRecyclerView.hasFixedSize();
        mBinding.fragmentPoiListRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PoiListAdapter(mListener);
        mBinding.fragmentPoiListRecyclerView.setAdapter(mAdapter);
        mViewModel.getPoisLiveData().observe(this, pois -> {
            mAdapter.setData(pois);
            mAdapter.notifyDataSetChanged();
        });

//        TODO: Find why this causes bug, if it is called right after onResume(), bigger delay is ok.
//        List<Poi> sExamplePOIs = new ArrayList<Poi>();
//        sExamplePOIs.add(new Poi("Přechod 1", 50.47902, 14.03453, POI_TYPE_CROSSING));
//        sExamplePOIs.add(new Poi("Omezení (50) 1", 50.47394, 14.00254, POI_TYPE_SPEED_LIMITATION_50));
//        sExamplePOIs.add(new Poi("Přechod 2", 50.47916, 13.99642, POI_TYPE_CROSSING));
//        new Handler().postDelayed(() -> {
//            handlePOIsChange(sExamplePOIs);
//        }, 5);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void select(Poi poi) {
        mAdapter.selectPoi(poi);
    }
}
