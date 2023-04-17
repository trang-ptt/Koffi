package com.example.koffi.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.koffi.R;
import com.example.koffi.fragment.home.HomeFragment;
import com.example.koffi.fragment.order.MenuFragment;
import com.example.koffi.fragment.other.OtherFragment;
import com.example.koffi.fragment.store.StoreFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainFragment extends Fragment {

    String from;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.delivery:
                    replaceFragment(new MenuFragment());
                    break;
                case R.id.store:
                    replaceFragment(new StoreFragment());
                    break;
                case R.id.coupon:
                    break;
                case R.id.other:
                    replaceFragment(new OtherFragment());
                    break;
            }
            return true;
        });

        //Get argument
        if (getArguments()!=null) {
            from = getArguments().getString("from");
            switch (getArguments().getString("back")) {
                case "menu":
                    bottomNavigationView.setSelectedItemId(R.id.delivery);
                    break;
                case "store":
                    bottomNavigationView.setSelectedItemId(R.id.store);
                    break;
                case "other":
                    bottomNavigationView.setSelectedItemId(R.id.other);
                    break;
            }
        }
        else {
            replaceFragment(new HomeFragment());
        }

    }
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        if (from!=null) {
            Bundle bundle =new Bundle();
            bundle.putString("from",from);
            fragment.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}