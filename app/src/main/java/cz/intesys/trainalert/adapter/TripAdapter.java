package cz.intesys.trainalert.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.entity.Stop;
import cz.intesys.trainalert.view.StopView;

import static cz.intesys.trainalert.view.StopView.TYPE_PREVIOUS_STOP;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.ViewHolder> {

    private List<Stop> mPreviousStops;
    private List<Stop> mNextStops;
    private Stop mFinalStop;
    private Context mContext;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
    }

    public TripAdapter(Context context, OnItemClickListener listener) {
        mContext = context; // TODO: make with Dagger
        mListener = listener;
        mPreviousStops = new ArrayList<>();
        mNextStops = new ArrayList<>();
        mFinalStop = null;
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StopView stopView = new StopView(parent.getContext());
        stopView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(stopView);
        return viewHolder;
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
        Stop stop = getStop(position);
        if (stop == null) {
            holder.stopView.setAsTrainMarker(true);
        } else {
            holder.stopView.setAsTrainMarker(false); // Can be stored from previous
            holder.stopView.setName(stop.getName());
            holder.stopView.setArrival(stop.getArrival());
            holder.stopView.setDelay(stop.getDelay());
            holder.stopView.setButtonPressed(stop.isPressed());
        }

        int listPosition = 0;
        if (position == 0) { // First position.
            listPosition = StopView.LIST_POSITION_START;
        } else if (position == getItemCount() - 1) { // Last position.
            listPosition = StopView.LIST_POSITION_END;
        } else {
            listPosition = StopView.LIST_POSITION_MIDDLE;
        }
        holder.stopView.setListPosition(listPosition);

        //colorizeStopView(holder.stopView, position);
        holder.stopView.setType(getStopType(position));
    }

    @Override public int getItemCount() {
        int finalStopSize = (mFinalStop == null) ? 0 : 1;
        return mPreviousStops.size() + 1 + mNextStops.size() + finalStopSize;
    }

    public void setData(List<Stop> previousStops, List<Stop> nextStops, Stop finalStop) {
        mPreviousStops.clear();
        mPreviousStops.addAll(previousStops);
        mNextStops.clear();
        mNextStops.addAll(nextStops);
        mFinalStop = finalStop;
    }

    public void setPreviousStops(List<Stop> previousStops) {
        mPreviousStops.clear();
        mPreviousStops.addAll(previousStops);
        notifyDataSetChanged();
    }

    public void setNextStops(List<Stop> nextStops) {
        mNextStops.clear();
        mNextStops.addAll(nextStops);
        notifyDataSetChanged();

    }

    public void setFinalStop(Stop finalStop) {
        mFinalStop = finalStop;
        notifyDataSetChanged();
    }

    private Stop getStop(int position) {
        if (position < mPreviousStops.size()) { // Previous stops.
            return mPreviousStops.get(position);
        } else if (position == mPreviousStops.size()) {
            return null; // There is train marker on this position
        } else if (position < (mPreviousStops.size() + 1 + mNextStops.size())) { // Next stops.
            return mNextStops.get(position - (mPreviousStops.size() + 1));
        } else { // Final stop.
            return mFinalStop;
        }
    }

    private @StopView.StopType int getStopType(int position) {
        if (position < mPreviousStops.size()) { // Previous stops.
            return TYPE_PREVIOUS_STOP;
        } else if (position == mPreviousStops.size()) {
            return StopView.TYPE_TRAIN_MARKER;
        } else if (position == mPreviousStops.size() + 1) { // Closest next stop.
            return StopView.TYPE_CLOSEST_NEXT_STOP;
        } else if (position < (mPreviousStops.size() + mNextStops.size()) + 1) { // Next stops.
            return StopView.TYPE_NEXT_STOP;
        } else { // Final stop.
            return StopView.TYPE_FINAL_STOP;
        }
    }

    private void colorizeStopView(StopView stopView, int position) {
        if (position < mPreviousStops.size()) { // Previous stops.
            stopView.setColor(ContextCompat.getColor(mContext, R.color.stop_green));
        } else if (position == mPreviousStops.size()) {
            stopView.setColor(ContextCompat.getColor(mContext, android.R.color.white));
        } else if (position == mPreviousStops.size() + 1) { // Closest next stop.
            stopView.setColor(ContextCompat.getColor(mContext, R.color.stop_orange));
        } else if (position < (mPreviousStops.size() + mNextStops.size()) + 1) { // Next stops.
            stopView.setColor(ContextCompat.getColor(mContext, R.color.stop_red));
        } else { // Final stop.
            stopView.setColor(ContextCompat.getColor(mContext, R.color.stop_blue));
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        StopView stopView;

        public ViewHolder(StopView view) {
            super(view);
            stopView = view;
        }
    }
}
