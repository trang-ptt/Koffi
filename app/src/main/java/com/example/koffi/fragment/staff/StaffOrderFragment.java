package com.example.koffi.fragment.staff;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.koffi.R;
import com.example.koffi.adapter.ViewPagerAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class StaffOrderFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    public StaffOrderFragment() {
        // Required empty public constructor
    }

    public static StaffOrderFragment newInstance(String param1, String param2) {
        StaffOrderFragment fragment = new StaffOrderFragment();
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
        return inflater.inflate(R.layout.fragment_staff_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init
        viewPager = (ViewPager) view.findViewById(R.id.order_Viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.order_tabLayout);

        //Tab layout
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new DeliveryTabFragment(), "Giao hàng");
        adapter.addFragment(new TATabFragment(), "Tự đến lấy");
        viewPager.setAdapter(adapter);
    }
}