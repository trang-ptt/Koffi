package com.example.koffi.fragment.staff;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.R;
import com.example.koffi.activity.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StaffProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StaffProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StaffProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StaffProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StaffProfileFragment newInstance(String param1, String param2) {
        StaffProfileFragment fragment = new StaffProfileFragment();
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
        return inflater.inflate(R.layout.fragment_staff_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init
        LinearLayout changePw = view.findViewById(R.id.staff_changepassword);
        LinearLayout logout = view.findViewById(R.id.staff_logout);
        TextView name = view.findViewById(R.id.staff_name);
        TextView email = view.findViewById(R.id.staff_email);
        TextView store = view.findViewById(R.id.staff_store);
        ImageView imageView = view.findViewById(R.id.image_avatar);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("staff").whereEqualTo("email", user.getEmail())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        name.setText(snapshot.getString("name"));
                        email.setText(snapshot.getString("email"));
                        int drawableId = view.getResources().getIdentifier(snapshot.getString("image"),
                                "drawable", getContext().getPackageName());
                        imageView.setImageResource(drawableId);
                        System.out.println("here store " + snapshot.getString("store"));
                        db.collection("stores").document(snapshot.getString("store"))
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    System.out.println("here found ");
                                    DocumentSnapshot doc = task.getResult();
                                    store.setText(doc.getString("address"));
                                }
                            }
                        });
                    }
                }
            }
        });

        //Change Password
        changePw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_staffProfileFragment_to_changePasswordFragment);
            }
        });

        //Log out
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Handle staff logout
                Dialog logOutDialog = new Dialog(getContext(), androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
                View logOutView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_logout,
                        view.findViewById(R.id.logout_dialog));
                logOutDialog.setContentView(logOutView);
                TextView cancelBtn = logOutDialog.findViewById(R.id.logout_cancelBtn);
                TextView logoutBtn = logOutDialog.findViewById(R.id.logout_logoutBtn);

                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        logOutDialog.dismiss();
                    }
                });
                logoutBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getContext(), "Đăng xuất thành công!", Toast.LENGTH_LONG).show();
                        logOutDialog.dismiss();
                        startActivity(new Intent(getActivity(), MainActivity.class));
                    }
                });
                logOutDialog.show();
            }
        });
    }
}