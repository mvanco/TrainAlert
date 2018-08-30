package cz.intesys.trainalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.databinding.SettingItemBinding;
import cz.intesys.trainalert.entity.Settings;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.ViewHolder> {
    private List<Settings> mItems;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        /**
         * @param itemId string resource id of item title, which works like id of navigation item
         */
        void onCheckedItem(int itemId);
    }

    public SettingAdapter(OnItemClickListener listener) {
        mItems = new ArrayList<Settings>();
        mItems.add(new Settings());
        mItems.add(new Settings());
        mItems.add(new Settings());

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
//        holder.mBinding.setData(mItems.get(position));
        holder.mBinding.getRoot().setOnClickListener((view) -> {
            mListener.onCheckedItem(1);
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private SettingItemBinding mBinding;

        public ViewHolder(SettingItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}
