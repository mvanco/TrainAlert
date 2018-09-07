package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cz.intesys.trainalert.adapter.ProfileAdapter;
import cz.intesys.trainalert.databinding.FragmentProfileBinding;
import cz.intesys.trainalert.entity.realm.Profile;
import cz.intesys.trainalert.viewmodel.ProfileFragmentViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends DialogFragment implements ProfileAdapter.OnItemClickListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private FragmentProfileBinding mBinding;
    private ProfileFragmentViewModel mViewModel;
    private ProfileAdapter mAdapter;
    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onFabClick();

        void onProfileDeleted(Profile profile);

        void onProfileClicked(String profileName);

        void onProfileUpdated(String profileName);
    }

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileFragmentViewModel.class);
        getLifecycle().addObserver(mViewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        mBinding.setViewModel(mViewModel);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.fragmentProfileRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ProfileAdapter(this);
        mBinding.fragmentProfileRecyclerView.setAdapter(mAdapter);
        mBinding.fragmentProfileFab.setOnClickListener((view) -> {
            if (mListener != null) {
                mListener.onFabClick();
            }
        });

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ProfileAdapter.ViewHolder) return 1f;
                return super.getSwipeThreshold(viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                if (swipeDir == ItemTouchHelper.RIGHT) {
                    if (viewHolder instanceof ProfileAdapter.ViewHolder) {
                        Profile profile = ((ProfileAdapter.ViewHolder) viewHolder).getBinding().getData();
                        mListener.onProfileDeleted(profile);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mBinding.fragmentProfileRecyclerView);
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
    public void onResume() {
        super.onResume();
        reload();
    }

    public void reload() {
        List<Profile> profiles = mViewModel.getProfiles();
        mAdapter.setData(profiles);
        if (profiles != null && !profiles.isEmpty()) {
            mBinding.fragmentProfileEmptyView.setVisibility(View.GONE);
        } else {
            mBinding.fragmentProfileEmptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClickedItem(String profileName) {
        mListener.onProfileClicked(profileName);
        dismiss();
    }

    @Override
    public void onLongClickedItem(String profileName) {
        mListener.onProfileUpdated(profileName);
    }
}
