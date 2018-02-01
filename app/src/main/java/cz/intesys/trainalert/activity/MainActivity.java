package cz.intesys.trainalert.activity;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;

import cz.intesys.trainalert.R;
import cz.intesys.trainalert.adapter.NavAdapter;
import cz.intesys.trainalert.databinding.ActivityMainBinding;
import cz.intesys.trainalert.fragment.MainFragment;

import static android.support.v4.widget.DrawerLayout.STATE_IDLE;
import static android.support.v4.widget.DrawerLayout.STATE_SETTLING;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setupActionBar();
        RecyclerView recyclerView = binding.activityMainNavigationView.findViewById(R.id.activityMain_recyclerView);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new NavAdapter((id) -> onNavigationItemSelected(id)));

    }

    @Override
    protected void onResume() {
        super.onResume();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.activityMainContent_mainFragment);
        fragment.setAnimating(true);
    }

    private void onNavigationItemSelected(@StringRes int id) {
        if (id == R.string.nav_pois) { // POIs
            binding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(PoiActivity.newIntent(this));
        } else if (id == R.string.nav_categories) {
            binding.activityMainDrawerLayout.closeDrawer(Gravity.LEFT, false);
            startActivity(CategoryActivity.newIntent(this));
        }
    }

    private void setupActionBar() {
        setSupportActionBar(binding.activityMainToolbar);

        mToggle = new ActionBarDrawerToggle(
                this, binding.activityMainDrawerLayout, binding.activityMainToolbar, R.string.activity_main_navigation_drawer_open, R.string.activity_main_navigation_drawer_close);
        binding.activityMainDrawerLayout.addDrawerListener(mToggle);
        binding.activityMainDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mToggle.syncState();
        MainFragment fragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.activityMainContent_mainFragment);
        binding.activityMainDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == STATE_SETTLING) { // Animation is in progress.
                    fragment.setAnimating(false); // Stop animation in fragment to allow finer animation of drawer.
                } else if (newState == STATE_IDLE) { // Animation is not in progress.
                    fragment.setAnimating(true);
                }
            }
        });
    }
}
