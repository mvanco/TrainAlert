package cz.intesys.trainalert.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import cz.intesys.trainalert.databinding.PoiListItemBinding;
import cz.intesys.trainalert.entity.Poi;

public class PoiListAdapter extends RecyclerView.Adapter<PoiListAdapter.ViewHolder> {
    private List<Poi> mPois;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onPoiSelect(Poi poi);
    }

    public PoiListAdapter(List<Poi> pois, OnItemClickListener listener) {
        mPois = pois;
        mListener = listener;
    }

    @Override
    public PoiListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        PoiListItemBinding binding = PoiListItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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

    public void setListener(OnItemClickListener listener) {
        mListener = listener;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        PoiListItemBinding mBinding;

        public ViewHolder(PoiListItemBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }
    }
}