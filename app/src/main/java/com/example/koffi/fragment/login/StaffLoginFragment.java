package com.example.koffi.fragment.login;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.koffi.R;
import com.example.koffi.activity.MainActivity;
import com.example.koffi.activity.StaffActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffLoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffLoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StaffLoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StaffLoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StaffLoginFragment newInstance(String param1, String param2) {
        StaffLoginFragment fragment = new StaffLoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_staff_login, container, false);
    }
    EditText edtEmail;
    EditText edtPass;
    Button staffLogin;
    ImageButton closeStaffLogin;
    ProgressBar pb;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        edtEmail = getView().findViewById(R.id.editTextEmail);
        edtPass = getView().findViewById(R.id.editTextPassword);
        staffLogin = getView().findViewById(R.id.staffLoginBtn);
        closeStaffLogin = getView().findViewById(R.id.staffLogin_closeBtn);
        pb = getView().findViewById(R.id.staffLogin_progress_bar);
        staffLogin.setEnabled(false);
        setTextChangedListener(edtEmail);
        setTextChangedListener(edtPass);
        closeStaffLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.action_staffLoginFragment_to_loginFragment);
            }
        });
        staffLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pb.setVisibility(View.VISIBLE);
                staffLogin.setVisibility(View.INVISIBLE);
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    Navigation.findNavController(view).navigate(R.id.action_staffLoginFragment_to_staffOrderFragment2);

                                }
                                else {
                                    Toast.makeText(getContext(), "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    public void setTextChangedListener(EditText text) {
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String email = edtEmail.getText().toString().trim();
                String password = edtPass.getText().toString().trim();
                if (Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length() >= 6) {
                    staffLogin.setEnabled(true);
                    staffLogin.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.purple_200));
                } else {
                    staffLogin.setEnabled(false);
                    staffLogin.setBackgroundTintList(ContextCompat.getColorStateList(getContext(), R.color.disable));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}