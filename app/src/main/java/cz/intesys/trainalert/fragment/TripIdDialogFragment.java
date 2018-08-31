package cz.intesys.trainalert.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.DrawerItemBinding;
import cz.intesys.trainalert.repository.DataHelper;

public class TripIdDialogFragment extends DialogFragment {

    public static final String TRIPS_KEY = "cz.intesys.trainalert.trainiddialogfragment.trips";
    public static final String ACTIVE_TRIP_KEY = "cz.intesys.trainalert.trainiddialogfragment.activetrip";

    public OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener extends TripIdManuallyDialogFragment.OnFragmentInteractionListener {
        // void onTripSelected(int tripId); This is already in TripIdManuallyDialogFragment interaction listener.
        void onBusinessTripSelected(String tripId);

        void onTripManuallySelected();
    }

    public static TripIdDialogFragment newInstance(List<String> trips, String activeTrip) {

        Bundle args = new Bundle();
        args.putStringArrayList(TRIPS_KEY, new ArrayList<>(trips));
        args.putString(ACTIVE_TRIP_KEY, activeTrip);
        TripIdDialogFragment fragment = new TripIdDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_fragment_trip_id);

        List<String> items = new ArrayList<>();
        for (String item : getArguments().getStringArrayList(TRIPS_KEY)) {
            items.add(item);
        }
        items.add(getContext().getResources().getString(R.string.fragment_trip_id_business_trip_0));
        items.add(getContext().getResources().getString(R.string.fragment_trip_id_business_trip_1));
        items.add(getContext().getResources().getString(R.string.fragment_trip_id_manual_selection));
//        builder.setItems(items.toArray(new String[items.size()]), (dialog, which) -> {
//            if (which == items.size() - 3) { // Business trip 1.
//                mListener.onBusinessTripSelected(DataHelper.TRIP_ID_BUSINESS_TRIP_0);
//            } else if (which == items.size() - 2) { // Business trip 2.
//                mListener.onBusinessTripSelected(DataHelper.TRIP_ID_BUSINESS_TRIP_1);
//            } else if (which == items.size() - 1) { // Manual selection.
//                mListener.onTripManuallySelected();
//            } else {
//                mListener.onTripSelected(getArguments().getStringArrayList(TRIPS_KEY).get(which));
//            }
//        });

        String[] objects = items.toArray(new String[items.size()]);
        String activeTrip = getArguments().getString(ACTIVE_TRIP_KEY);

        builder.setAdapter(new TripIdDialogAdapter(getContext(), R.layout.array_adapter_item, R.id.array_adapter_text, objects, activeTrip), (dialog, which) -> {
            if (which == items.size() - 3) { // Business trip 1.
                mListener.onBusinessTripSelected(DataHelper.TRIP_ID_BUSINESS_TRIP_0);
            } else if (which == items.size() - 2) { // Business trip 2.
                mListener.onBusinessTripSelected(DataHelper.TRIP_ID_BUSINESS_TRIP_1);
            } else if (which == items.size() - 1) { // Manual selection.
                mListener.onTripManuallySelected();
            } else {
                mListener.onTripSelected(getArguments().getStringArrayList(TRIPS_KEY).get(which));
            }
        });

        return builder.create();
    }

    @Override public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    protected class TripIdDialogAdapter extends ArrayAdapter<String> {
        String mActiveTrip;

        public TripIdDialogAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull String[] objects, String activeTrip) {
            super(context, resource, textViewResourceId, objects);
            mActiveTrip = activeTrip;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View root = super.getView(position, convertView, parent);
            TextView tv = root.findViewById(R.id.array_adapter_text);
            View background = root.findViewById(R.id.array_adapter_background);
            if (tv.getText().equals(mActiveTrip)) {
                tv.setTextColor(Color.WHITE);
                background.setBackgroundResource(R.drawable.trip_id_background_red);
            }
            else {
                tv.setTextColor(Color.BLACK);
                background.setBackgroundResource(R.drawable.trip_id_background);
            }

            return root;
        }
    }
}