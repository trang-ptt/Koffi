package com.example.koffi.fragment.other;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.koffi.activity.LoginActivity;
import com.example.koffi.R;
import com.example.koffi.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    DatePickerDialog.OnDateSetListener setListenerDate;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Define
        RoundedImageView imageView=view.findViewById(R.id.image_avatar);
        Button btnUpdate = view.findViewById(R.id.btnUpdateProfile);
        EditText Ho = view.findViewById(R.id.surnameEdit);
        EditText Ten = view.findViewById(R.id.nameEdit);
        EditText Email = view.findViewById(R.id.profile_editEmail);
        EditText Sdt = view.findViewById(R.id.profile_editPhoneNumber);
        TextView NgaySinh = view.findViewById(R.id.dobText);
        TextView GioiTinh = view.findViewById(R.id.genderText);

        //Get user info
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
            Toast.makeText(getContext(), "Bạn chưa đăng nhâp. Mời bạn đăng nhập!", Toast.LENGTH_LONG).show();
        } else {
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                System.out.println("found");
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {
                                        if (document.get("Ten") != null) {
                                            String a = document.get("Ten").toString();
                                            String[] name = a.split(" ", 2);
                                            if (name.length > 1) {
                                                Ho.setText(name[1]);
                                                Ten.setText(name[0]);
                                            }
                                        }
                                        if (document.get("Email") != null)
                                        Email.setText((CharSequence) document.get("Email"));
                                        if (document.get("Sdt") != null)
                                        Sdt.setText((((CharSequence) document.get("Sdt"))));
                                        NgaySinh.setText((CharSequence) document.get("NgaySinh"));
                                        if (document.getString("GioiTinh") != null)
                                        if (!document.getString("GioiTinh").isEmpty())
                                        GioiTinh.setText((CharSequence) document.get("GioiTinh"));
                                    }
                                }
                            }
                        }
                    });
                String a = user.getDisplayName();
                if (!a.equals("") && a != null) {
                    System.out.println("here");
                    String[] name = a.split(" ", 2);
                    if (name.length > 1)
                    Ho.setText(name[1]);
                    Ten.setText(name[0]);
                }
                if (user.getEmail() != null && !user.getEmail().isEmpty())
                Email.setText((user.getEmail()));
            if (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                Sdt.setText(user.getPhoneNumber());


        }

            //Update profile
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String firstname = Ho.getText().toString();
                    String lastname = Ten.getText().toString();
                    String name = lastname + " " + firstname;
                    String mail = Email.getText().toString();
                    String sdt = Sdt.getText().toString();
                    String DateOfBirth = NgaySinh.getText().toString();
                    String genderr = GioiTinh.getText().toString();
                    User userr = new User(name, mail, sdt, DateOfBirth, genderr);
                    //update in authen
                    UserProfileChangeRequest profileupdate = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    //Update in firestore
                    db.collection("users").document(user.getUid()).set(userr);

                    Navigation.findNavController(view).navigate(R.id.otherFragment);
                }


            });

            //Ngay Sinh
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            ImageView cal = view.findViewById(R.id.imageCalendar);
            cal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog_MinWidth, setListenerDate, year, month, day);
                    datePickerDialog.show();
                }
            });
            setListenerDate = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int day) {
                    month = month + 1;
                    String date = day + "/" + month + "/" + year;
                    NgaySinh.setText(date);
                }
            };
            //Toolbar
            Toolbar toolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        //Back to main
        ImageButton backBtn = view.findViewById(R.id.profile_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

            //Bottom sheet avatar
            ConstraintLayout avatar = view.findViewById(R.id.profile_avatar);
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Bottom sheet dialog
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_profile_avatar,
                            (LinearLayout) view.findViewById(R.id.avatar_bottomsheet));
                    bottomSheetDialog.setContentView(bottomSheetView);

                    //Handle dialog
                    TextView openCam = bottomSheetDialog.findViewById(R.id.openCam);
                    TextView openLib = bottomSheetDialog.findViewById(R.id.openLibrary);
                    TextView Can = bottomSheetDialog.findViewById(R.id.AvtarHuy);
                    openCam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            bottomSheetDialog.cancel();
                        }
                    });
                    openLib.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            bottomSheetDialog.cancel();
                        }
                    });
                    Can.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.cancel();
                        }
                    });

                    //Show dialog
                    bottomSheetDialog.show();
                }
            });

            //Bottom sheet gender
            LinearLayout gender = view.findViewById(R.id.profile_gender);
            gender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Bottom sheet dialog
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
                    View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_profile_gender,
                            (LinearLayout) view.findViewById(R.id.gender_bottomsheet));
                    bottomSheetDialog.setContentView(bottomSheetView);

                    //Handle dialog
                    TextView btmNam = bottomSheetDialog.findViewById(R.id.bottomNam);
                    TextView btmNu = bottomSheetDialog.findViewById(R.id.bottomNu);
                    TextView btmHuy = bottomSheetDialog.findViewById(R.id.bottomHuy);
                    btmNam.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GioiTinh.setText("Nam");
                            bottomSheetDialog.cancel();
                        }
                    });
                    btmNu.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GioiTinh.setText("Nữ");
                            bottomSheetDialog.cancel();
                        }
                    });
                    btmHuy.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            bottomSheetDialog.cancel();
                        }
                    });
                    //Show dialog
                    bottomSheetDialog.show();
                }
            });
        }


    }
