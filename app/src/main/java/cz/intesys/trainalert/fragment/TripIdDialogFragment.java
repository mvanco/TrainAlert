package cz.intesys.trainalert.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.repository.DataHelper;

public class TripIdDialogFragment extends DialogFragment {

    public static final String TRIPS_KEY = "cz.intesys.trainalert.trainiddialogfragment.trips";
    public OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener extends TripIdManuallyDialogFragment.OnFragmentInteractionListener {
        // void onTripSelected(int tripId); This is already in TripIdManuallyDialogFragment interaction listener.
        void onBusinessTripSelected(String tripId);

        void onTripManuallySelected();
    }

    public static TripIdDialogFragment newInstance(List<String> trips) {

        Bundle args = new Bundle();
        args.putStringArrayList(TRIPS_KEY, new ArrayList<>(trips));

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
        builder.setItems(items.toArray(new String[items.size()]), (dialog, which) -> {
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
}