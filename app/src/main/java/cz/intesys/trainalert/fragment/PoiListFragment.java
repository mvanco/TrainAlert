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

import java.util.List;

import cz.intesys.trainalert.adapter.PoiListAdapter;
import cz.intesys.trainalert.databinding.FragmentPoiListBinding;
import cz.intesys.trainalert.entity.Poi;
import cz.intesys.trainalert.viewmodel.MainFragmentViewModel;

public class PoiListFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private FragmentPoiListBinding mBinding;
    private RecyclerView.LayoutManager mLayoutManager;
    private MainFragmentViewModel mViewModel;
    private PoiListAdapter mAdapter;

    public interface OnFragmentInteractionListener extends PoiListAdapter.OnItemClickListener {
    }

    public PoiListFragment() {
    }

    public static PoiListFragment newInstance() {
        PoiListFragment fragment = new PoiListFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainFragmentViewModel.class);
        mViewModel.getPois().observe(this, pois -> handlePOIsChange(pois));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentPoiListBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.fragmentPoiListRecyclerView.setLayoutManager(mLayoutManager);
        mViewModel.loadPOIs();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setListener(OnFragmentInteractionListener listener) {
        mListener = listener;
        mAdapter.setListener(mListener);
    }

    private void handlePOIsChange(List<Poi> pois) {
        mAdapter = new PoiListAdapter(pois, mListener);
        mAdapter.setListener(mListener);
        mBinding.fragmentPoiListRecyclerView.setAdapter(mAdapter); //If mBinding null so far move mViweModel.getPois() to onCreateView()
    }
}
