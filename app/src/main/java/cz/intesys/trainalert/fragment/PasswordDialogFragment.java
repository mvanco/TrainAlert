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

public class PasswordDialogFragment extends DialogFragment {

    private OnFragmentInteractionListener mListener;


    public interface OnFragmentInteractionListener {
        void onPasswordEntered(int password);

        void onDialogCanceled();
    }

    public static PasswordDialogFragment newInstance() {
        Bundle args = new Bundle();

        PasswordDialogFragment fragment = new PasswordDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        LinearLayout customView = (LinearLayout) inflater.inflate(R.layout.dialog_password, null);


        builder.setTitle(R.string.title_fragment_password);
        builder.setView(customView);

        builder.setPositiveButton(
                R.string.button_confirm,
                (dialog, which) -> {
                    EditText editText = customView.findViewById(R.id.trainId);

                    int password = -1;
                    try {
                        password = Integer.parseInt(editText.getText().toString());
                    } catch (NumberFormatException e) {

                    }

                    if (password != 0) {

                    }
                    mListener.onPasswordEntered(password);
                }
        );

        builder.setNegativeButton(
                R.string.button_cancel,
                (dialog, which) -> {
                    mListener.onDialogCanceled();
                    PasswordDialogFragment.this.getDialog().cancel();
                }
        );

        return builder.create();
    }

    @Override public void onPause() {
        super.onPause();
    }

    @Override public void onStop() {
        super.onStop();
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mListener.onDialogCanceled();
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
