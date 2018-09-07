package cz.intesys.trainalert.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.intesys.trainalert.TaConfig;
import cz.intesys.trainalert.databinding.FragmentManualBinding;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.viewmodel.ManualFragmentViewModel;

public class ManualFragment extends Fragment {

    FragmentManualBinding mBinding;
    ManualFragmentViewModel mViewModel;

    private OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
    }

    public ManualFragment() {
    }

    public static ManualFragment newInstance() {
        ManualFragment fragment = new ManualFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ManualFragmentViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = FragmentManualBinding.inflate(inflater, container, false);
        mBinding.fragmentManualWebView.getSettings().setJavaScriptEnabled(true);
        mBinding.fragmentManualWebView.loadUrl("http://www.adobe.com/devnet/acrobat/pdfs/pdf_open_parameters.pdf");
        return mBinding.getRoot();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
