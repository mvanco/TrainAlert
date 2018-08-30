package cz.intesys.trainalert.fragment;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.adapter.SettingAdapter;
import cz.intesys.trainalert.databinding.FragmentPoiListBinding;
import cz.intesys.trainalert.databinding.FragmentProfileBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProfileFragment extends Fragment implements SettingAdapter.OnItemClickListener {

    private RecyclerView.LayoutManager mLayoutManager;
    private FragmentProfileBinding mBinding;

    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentProfileBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mBinding.fragmentProfileRecyclerView.setLayoutManager(mLayoutManager);
        mBinding.fragmentProfileRecyclerView.setAdapter(new SettingAdapter(this));
    }

    @Override
    public void onCheckedItem(int itemId) {

    }
}
