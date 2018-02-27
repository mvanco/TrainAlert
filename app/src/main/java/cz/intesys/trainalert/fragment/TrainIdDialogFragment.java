package cz.intesys.trainalert.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;

import cz.intesys.trainalert.R;

public class TrainIdDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;


    public interface OnFragmentInteractionListener {
        void onTrainIdReturned(int trainId);
    }

    public static TrainIdDialogFragment newInstance() {
        Bundle args = new Bundle();

        TrainIdDialogFragment fragment = new TrainIdDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout customView = (LinearLayout) inflater.inflate(R.layout.dialog_train_id, null);


        builder.setTitle(R.string.title_fragment_train_id);
        builder.setMessage(R.string.fragment_train_id_message);
        builder.setView(customView);

        builder.setPositiveButton(
                R.string.dialog_fragment_positive_button,
                (dialog, which) -> {
                    EditText editText = customView.findViewById(R.id.trainId);
                    int trainId = Integer.parseInt(editText.getText().toString());
                    mListener.onTrainIdReturned(trainId);

                }
        );

        builder.setNegativeButton(
                R.string.dialog_fragment_negative_button,
                (dialog, which) -> {
                    TrainIdDialogFragment.this.getDialog().cancel();
                }
        );

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
