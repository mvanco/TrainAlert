package cz.intesys.trainalert.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ActivitySettingBinding;
import cz.intesys.trainalert.viewmodel.SettingsActivityViewModel;

public class SettingActivity extends AppCompatActivity {

    private ActivitySettingBinding mBinding;
    private SettingsActivityViewModel mViewModel;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        mViewModel = ViewModelProviders.of(this).get(SettingsActivityViewModel.class);

        setupActionBar();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void setupActionBar() {
        setSupportActionBar(mBinding.activitySettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_poi);
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
}
