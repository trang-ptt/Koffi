package com.example.koffi.activity;

import android.content.Intent;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;

public class MainActivity extends AppCompatActivity {

    Task<GetTokenResult> task;
    ActivityMainBinding binding;
    NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check if staff
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser()!=null) {
            task = auth.getCurrentUser().getIdToken(false).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    String label = task.getResult().getSignInProvider();
                    if (!label.equals("phone"))
                        if (label.equals("password") ) {
                            Intent intent = new Intent(MainActivity.this,StaffActivity.class);
                            startActivity(intent);
                        }
                }
            });
        }

        //Bottom nav
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        BottomNavigationView bottomNavigationView = findViewById(R.id.main_bottom_nav);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {
                String label = navDestination.getLabel().toString();
                if (label.equals("fragment_home") || label.equals("fragment_menu") || label.equals("fragment_store")|| label.equals("fragment_other"))
                    bottomNavigationView.setVisibility(View.VISIBLE);
                else
                    bottomNavigationView.setVisibility(View.GONE);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        navController.navigate(R.id.action_global_homeFragment);
                        break;
                    case R.id.delivery:
                        navController.navigate(R.id.menuFragment);
                        break;
                    case R.id.store:
                        navController.navigate(R.id.storeFragment);
                        break;
                    case R.id.other:
                        navController.navigate(R.id.otherFragment);
                        break;
                }
                return true;
            }
        });
    }
}