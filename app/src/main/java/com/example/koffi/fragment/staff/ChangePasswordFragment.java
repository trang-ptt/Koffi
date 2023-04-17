package com.example.koffi.fragment.staff;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangePasswordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangePasswordFragment newInstance(String param1, String param2) {
        ChangePasswordFragment fragment = new ChangePasswordFragment();
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
        return inflater.inflate(R.layout.fragment_change_password, container, false);
    }

    TextView warningOld, warningNew, warningRetype;
    EditText edtOldPass, edtNewPass, edtRetypePass;
    String oldPass, newPass, retypePass;
    Button done;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init
        warningOld = view.findViewById(R.id.warning_old);
        warningNew = view.findViewById(R.id.warning_new);
        warningRetype = view.findViewById(R.id.warning_retype);
        edtOldPass = view.findViewById(R.id.old_pass);
        edtNewPass = view.findViewById(R.id.new_pass);
        edtRetypePass = view.findViewById(R.id.retype_pass);
        done = view.findViewById(R.id.confirm_change);
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.checkout_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        edtNewPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                oldPass = edtOldPass.getText().toString();
                newPass = edtNewPass.getText().toString();
                if (oldPass.equals(newPass) || newPass.length() < 6)
                    warningNew.setVisibility(View.VISIBLE);
                else warningNew.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtRetypePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newPass = edtNewPass.getText().toString();
                retypePass = edtRetypePass.getText().toString();
                if (!newPass.equals(retypePass))
                    warningRetype.setVisibility(View.VISIBLE);
                else warningRetype.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                oldPass = edtOldPass.getText().toString();
                newPass = edtNewPass.getText().toString();
                retypePass = edtRetypePass.getText().toString();
                if (oldPass.isEmpty() || newPass.isEmpty() || retypePass.isEmpty()) {
                    Toast.makeText(getContext(), "Không được để trống thông tin!", Toast.LENGTH_LONG).show();
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    AuthCredential auth = EmailAuthProvider.getCredential(user.getEmail(), oldPass);
                    user.reauthenticate(auth).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            warningOld.setVisibility(View.GONE);
                            if (passwordValidate()) {
                                user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(), "Đổi mật khẩu thành công!", Toast.LENGTH_LONG).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Đổi mật khẩu thất bại!", Toast.LENGTH_LONG).show();
                                    }
                                });
                            } else
                                Toast.makeText(getContext(), "Vui lòng kiểm tra lại!", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            warningOld.setVisibility(View.VISIBLE);
                            System.out.println("Failure: " + e);
                        }
                    });
                }
            }
        });
    }

    public boolean passwordValidate() {
        oldPass = edtOldPass.getText().toString();
        newPass = edtNewPass.getText().toString();
        retypePass = edtRetypePass.getText().toString();

        if (newPass.equals(oldPass) || newPass.length() < 6) {
            warningNew.setVisibility(View.VISIBLE);
            return false;
        }
        if (!newPass.equals(retypePass)) {
            warningRetype.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
}