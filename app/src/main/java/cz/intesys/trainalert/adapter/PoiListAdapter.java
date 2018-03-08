package cz.intesys.trainalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.databinding.FragmentPoiListItemBinding;
import cz.intesys.trainalert.entity.Poi;

public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {
    private List<Poi> mPois;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onPoiSelect(Poi poi);
    }

    public PoiListAdapter(OnItemClickListener listener) {
        mListener = listener;
        mPois = new ArrayList<>();
    }

    @Override
    public PoiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FragmentPoiListItemBinding binding = FragmentPoiListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        ViewHolder viewHolder = new ViewHolder(binding);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(PoiListAdapter.ViewHolder holder, int position) {
        Poi poi = mPois.get(position);
        holder.mBinding.setData(poi);
        holder.mBinding.getRoot().setOnClickListener((view) -> mListener.onPoiSelect(poi));
    }

    @Override
    public int getItemCount() {
        return mPois.size();
    }

    public void setData(List<Poi> pois) {
        mPois.clear();
        mPois.addAll(pois);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FragmentPoiListItemBinding mBinding;

        public ViewHolder(FragmentPoiListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}