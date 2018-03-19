package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.adapter.TripAdapter;
import cz.intesys.trainalert.databinding.FragmentTripBinding;
import cz.intesys.trainalert.entity.TaCallback;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.viewmodel.TripFragmentViewModel;

public class TripFragment extends Fragment {
    private static final String ARG_PREVIOUS_STOP_COUNT = "previousStopCount";
    private static final String ARG_NEXT_STOP_COUNT = "nextStopCount";

    private int previousStopCount;
    private int nextStopCount;

    private OnFragmentInteractionListener mListener;
    private FragmentTripBinding mBinding;
    private TripFragmentViewModel mViewModel;
    private RecyclerView.LayoutManager mLayoutManager;
    private TripAdapter mAdapter;

    public interface OnFragmentInteractionListener extends TripAdapter.OnItemClickListener {
        void onFragmentInteraction(Uri uri);
    }

    public TripFragment() {
    }

    public static TripFragment newInstance(int previousStopCount, int nextStopCount) {
        TripFragment fragment = new TripFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PREVIOUS_STOP_COUNT, previousStopCount);
        args.putInt(ARG_NEXT_STOP_COUNT, nextStopCount);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            previousStopCount = getArguments().getInt(ARG_PREVIOUS_STOP_COUNT);
            nextStopCount = getArguments().getInt(ARG_NEXT_STOP_COUNT);
        }
        mViewModel = ViewModelProviders.of(
                this,
                new TripFragmentViewModel.ViewModelFactory(previousStopCount, nextStopCount)
        ).get(TripFragmentViewModel.class);
        getLifecycle().addObserver(mViewModel);
        mViewModel.getPreviousStopsLiveData().observe(this, (previousStops) -> {
            mAdapter.setPreviousStops(previousStops);
            mAdapter.notifyDataSetChanged();
        });
        mViewModel.getNextStopsLiveData().observe(this, (nextStops) -> {
            mAdapter.setNextStops(nextStops);
            if (nextStops.size() < TaConfig.TRIP_FRAGMENT_NEXT_STOP_COUNT) {
                mAdapter.setFinalStage(true);
            }
            mAdapter.notifyDataSetChanged();
        });
        mViewModel.getFinalStopLiveData().observe(this, (finalStop) -> {
            mAdapter.setFinalStop(finalStop);
            mAdapter.notifyDataSetChanged();
        });

        mViewModel.getTrainStatusLiveData().observe(this, trainStatus -> {
            setTripHeader(trainStatus.isPressed(), trainStatus.isCanPass());
        });

//        mViewModel.getShouldStopLiveData().observe(this, shouldStop -> setTripHeader(shouldStop));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentTripBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.fragmentTripRecycler.setLayoutManager(mLayoutManager);
        mAdapter = new TripAdapter(getContext(), mListener);
        mBinding.fragmentTripRecycler.setAdapter(mAdapter);

        DataHelper.getInstance().getTrainId(new TaCallback<String>() {
            @Override public void onResponse(String response) {
                mBinding.trainId.setText(response);
            }

            @Override public void onFailure(Throwable t) {
            }
        });
        mBinding.tripId.setText(String.valueOf(DataHelper.getInstance().getTripId()));

//        mViewModel.getTrainStatusLiveData().observe(this, trainStatus -> {
//            setTripHeader(trainStatus.isPressed(), trainStatus.isCanPass());
//            setCanPass(trainStatus.isCanPass());
//        });
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void setTripHeader(boolean pressed, boolean canPass) {
        if (canPass) {
            mBinding.fragmentTripGreenBackground.setVisibility(View.VISIBLE);
            mBinding.fragmentTripGoAhead.setVisibility(View.VISIBLE);
            mBinding.fragmentTripRightArrow.setVisibility(View.VISIBLE);
        } else {
            mBinding.fragmentTripGreenBackground.setVisibility(View.INVISIBLE);
            mBinding.fragmentTripGoAhead.setVisibility(View.INVISIBLE);
            mBinding.fragmentTripRightArrow.setVisibility(View.INVISIBLE);
        }

        if (pressed) {
            mBinding.fragmentTripHeaderHand.setVisibility(View.VISIBLE);
            mBinding.fragmentTripOrangeBackground.setVisibility(View.VISIBLE);
            if (canPass) {
                mBinding.fragmentTripHeaderHand.setImageResource(R.drawable.ic_hand_green);
            } else {
                mBinding.fragmentTripHeaderHand.setImageResource(R.drawable.ic_hand_white);
            }
        } else {
            mBinding.fragmentTripOrangeBackground.setVisibility(View.INVISIBLE);
            mBinding.fragmentTripHeaderHand.setVisibility(View.INVISIBLE);
        }
    }
}
