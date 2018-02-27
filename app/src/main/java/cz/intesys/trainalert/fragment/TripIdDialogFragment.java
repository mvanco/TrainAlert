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

public class TripIdDialogFragment extends DialogFragment {

    public static final String TRIPS_KEY = "cz.intesys.trainalert.trainiddialogfragment.trips";
    public OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        void onTripSelected(int tripId);
    }

    public static TripIdDialogFragment newInstance(List<Integer> trips) {

        Bundle args = new Bundle();
        args.putIntegerArrayList(TRIPS_KEY, new ArrayList<>(trips));

        TripIdDialogFragment fragment = new TripIdDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_fragment_trip_id);

        List<String> items = new ArrayList<>();
        for (Integer item : getArguments().getIntegerArrayList(TRIPS_KEY)) {
            items.add(String.valueOf(item));
        }
        builder.setItems(items.toArray(new String[items.size()]), (dialog, which) -> {
            mListener.onTripSelected(getArguments().getIntegerArrayList(TRIPS_KEY).get(which));
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