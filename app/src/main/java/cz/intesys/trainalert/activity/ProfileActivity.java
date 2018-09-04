package cz.intesys.trainalert.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ActivityProfileBinding;
import cz.intesys.trainalert.entity.realm.Profile;
import cz.intesys.trainalert.fragment.ProfileFragment;
import cz.intesys.trainalert.viewmodel.ProfileActivityViewModel;

/**
 * Not used.
 */
public class ProfileActivity extends AppCompatActivity implements ProfileFragment.OnFragmentInteractionListener {

    private ActivityProfileBinding mBinding;
    private ProfileActivityViewModel mViewModel;

    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, ProfileActivity.class);
        return intent;
    }

    @Override
    public void onFabClick() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Název");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
//        alertDialog.setIcon(R.drawable.key);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String title = input.getText().toString();
                        mViewModel.addProfile(ProfileActivity.this, title);
                        ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.activityProfile_fragment);
                        fragment.reload();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onProfileDeleted(Profile profile) {
        mViewModel.deleteProfile(profile);
    }

    @Override
    public void onProfileClick(String profileName) {
        mViewModel.loadProfile(this, profileName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
        mViewModel = ViewModelProviders.of(this).get(ProfileActivityViewModel.class);

        setupActionBar();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void setupActionBar() {
        setSupportActionBar(mBinding.activityProfileToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_profile);
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
