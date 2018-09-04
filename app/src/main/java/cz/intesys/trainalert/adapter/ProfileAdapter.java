package cz.intesys.trainalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cz.intesys.trainalert.databinding.SettingItemBinding;
import cz.intesys.trainalert.entity.realm.Profile;
import io.realm.Realm;
import io.realm.RealmList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {
    private List<Profile> mItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onCheckedItem(String profileName);
    }

    public ProfileAdapter(OnItemClickListener listener) {
        mItems = new RealmList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SettingItemBinding binding = SettingItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Profile item = mItems.get(position);
        holder.mBinding.setData(item);
        holder.mBinding.getRoot().setOnClickListener((view) -> {
            mListener.onCheckedItem(item.getName());
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setData(List<Profile> profiles) {
        mItems.clear();
        mItems.addAll(profiles);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SettingItemBinding mBinding;

        public ViewHolder(SettingItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        public SettingItemBinding getBinding() {
            return mBinding;
        }
    }
}
