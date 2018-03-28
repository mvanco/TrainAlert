package cz.intesys.trainalert.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.entity.Category;
import cz.intesys.trainalert.repository.DataHelper;

public class CategoryPickerDialogFragment extends DialogFragment {

    public static final String CATEGORY_KEY = "cz.intesys.trainalert.categorypickerfragment.category";

    private int mSelectedItem;
    private OnFragmentInteractionListener mListener;


    public interface OnFragmentInteractionListener {
        void onCategorySelected(@DataHelper.CategoryId int categoryId);
    }

    public static CategoryPickerDialogFragment newInstance(@DataHelper.CategoryId int categoryId) {
        CategoryPickerDialogFragment fragment = new CategoryPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(CATEGORY_KEY, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_fragment_category_picker);

        List<String> categoryTitles = new ArrayList<>();
        for (Category category : DataHelper.getInstance().getCategories()) {
            categoryTitles.add(getContext().getString(category.getTitleRes()));
        }

        CharSequence[] items = categoryTitles.toArray(new String[categoryTitles.size()]);

        builder.setSingleChoiceItems(items,
                getArguments().getInt(CATEGORY_KEY),
                (dialog, which) -> {
                    mSelectedItem = which;
                }
        );
        builder.setPositiveButton(
                R.string.button_save,
                (dialog, which) -> {
                    mListener.onCategorySelected(mSelectedItem);
                }
        );

        builder.setNegativeButton(
                R.string.button_cancel,
                (dialog, which) -> {
                    // Nothing to do here.
                }
        );

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
}
