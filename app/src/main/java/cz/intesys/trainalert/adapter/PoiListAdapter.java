package cz.intesys.trainalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.databinding.FragmentPoiListItemBinding;
import cz.intesys.trainalert.entity.Poi;

public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {
    private List<Poi> mPois;
    private OnItemClickListener mListener;
    private SparseBooleanArray selectedItems;
    private int latestSelectedPosition;

    public interface OnItemClickListener {
        void onPoiSelect(Poi poi);
    }

    public PoiListAdapter(OnItemClickListener listener) {
        mListener = listener;
        mPois = new ArrayList<>();
        selectedItems = new SparseBooleanArray();
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
        holder.mBinding.poiListItemCardViewContent.setSelected(selectedItems.get(position, false));
        holder.mBinding.getRoot().setOnClickListener((view) -> {
            selectItem(position);
            mListener.onPoiSelect(poi);
        });
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

    public void selectPoi(Poi poi) {
        int i = mPois.indexOf(poi);
        if (i != -1) {
            selectItem(i);
        }
    }

    private void selectItem(int position) {
        selectedItems.clear();
        selectedItems.put(position, true);

        notifyItemChanged(position);
        notifyItemChanged(latestSelectedPosition);

        latestSelectedPosition = position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FragmentPoiListItemBinding mBinding;

        public ViewHolder(FragmentPoiListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}