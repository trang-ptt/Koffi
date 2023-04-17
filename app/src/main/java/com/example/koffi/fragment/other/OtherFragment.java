package com.example.koffi.fragment.other;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.koffi.dialog.DialogLogOut;
import com.example.koffi.activity.LoginActivity;
import com.example.koffi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class OtherFragment extends Fragment {


    private FirebaseAuth auth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_other, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Order History
        LinearLayout orderHistory = view.findViewById(R.id.other_orderHistory);
        orderHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(getContext(), "Bạn chưa đăng nhập!\nVui lòng đăng nhập!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                } else
                Navigation.findNavController(getView()).navigate(R.id.action_otherFragment_to_orderHistoryFragment);
            }
        });

        //Terms
        LinearLayout terms = view.findViewById(R.id.other_terms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_otherFragment_to_termFragment);
            }
        });

        //Contact
        LinearLayout contact = view.findViewById(R.id.other_contact);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_otherFragment_to_contactFragment);
            }
        });

        //Profile
        LinearLayout profile = view.findViewById(R.id.other_profile);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_otherFragment_to_profileFragment);
            }
        });

        //Address
        LinearLayout address = view.findViewById(R.id.other_address);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user==null)
                {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    Toast.makeText(getContext(), "Bạn chưa đăng nhâp. Mời bạn đăng nhập!", Toast.LENGTH_LONG).show();
                }

                Bundle bundle = new Bundle();
                bundle.putString("from","Other");
                Navigation.findNavController(getView()).navigate(R.id.action_otherFragment_to_addressFragment2,bundle);
            }
        });

        //Logout
        LinearLayout logout = view.findViewById(R.id.other_logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogLogOut louOutDialog = new DialogLogOut(getContext());
                louOutDialog.show();
            }
        });
    }
}