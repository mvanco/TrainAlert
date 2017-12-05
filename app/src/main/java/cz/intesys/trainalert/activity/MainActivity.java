package cz.intesys.trainalert.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.databinding.ActivityMainBinding;
import cz.intesys.trainalert.fragment.MainFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupActionBar();
        binding.activityMainNavigationView.setNavigationItemSelectedListener(
                (item) -> {
                    if (item.getItemId() == R.id.menu_restart) {
                        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_content_main_activity);
                        fragment.restartAnimation(this);
                        return true;
                    }
                    return false;
                }
        );
    }

    private void setupActionBar() {
        setSupportActionBar(binding.activityMainToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, binding.activityMainDrawerLayout, binding.activityMainToolbar, R.string.activity_main_navigation_drawer_open, R.string.activity_main_navigation_drawer_close);
        binding.activityMainDrawerLayout.addDrawerListener(mToggle);
        binding.activityMainDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mToggle.syncState();
    }
}
