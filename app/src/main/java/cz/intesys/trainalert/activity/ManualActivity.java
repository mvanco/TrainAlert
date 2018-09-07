package cz.intesys.trainalert.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ActivityManualBinding;
import cz.intesys.trainalert.fragment.ManualFragment;
import cz.intesys.trainalert.viewmodel.ManualActivityViewModel;

public class ManualActivity extends AppCompatActivity implements ManualFragment.OnFragmentInteractionListener{

    ActivityManualBinding mBinding;
    ManualActivityViewModel mViewModel;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ManualActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_manual);
        mViewModel = ViewModelProviders.of(this).get(ManualActivityViewModel.class);
        setupActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        setSupportActionBar(mBinding.activityManualToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_manual);
    }
}
