package cz.intesys.trainalert.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.repository.DataHelper;
import cz.intesys.trainalert.utility.Utility;

public class CategoryPickerDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_fragment_category_picker);
        builder.setItems(
                Utility.convertArray(
                        DataHelper.getInstance().getCategories(),
                        category -> category.getTitle(),
                        CharSequence[]::new
                ),
                (dialog, which) -> {

                }
        );

        return builder.create();
    }
}
