package com.example.koffi.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.koffi.R;
import com.example.koffi.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StaffActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_staff);
        BottomNavigationView bottomNavigationView = findViewById(R.id.staff_bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                String label = navDestination.getLabel().toString();
                if (label.equals("fragment_staff_order") || label.equals("fragment_statistic")|| label.equals("fragment_staff_profile"))
                    bottomNavigationView.setVisibility(View.VISIBLE);
                else
                    bottomNavigationView.setVisibility(View.GONE);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order:
                        navController.navigate(R.id.action_global_staffOrderFragment);
                        break;
                    case R.id.statistic:
                        navController.navigate(R.id.statisticFragment);
                        break;
                    case R.id.profile:
                        navController.navigate(R.id.staffProfileFragment);
                }
                return true;
            }
        });

//        //Bottom navigation badge
//        bottomNavigationView.getOrCreateBadge(R.id.order).setNumber(3);
//        TabLayout tabLayout = findViewById(R.id.order_tabLayout);
//        System.out.println(tabLayout);
    }
}