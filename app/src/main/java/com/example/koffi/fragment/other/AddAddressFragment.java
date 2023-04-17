package com.example.koffi.fragment.other;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.models.Address;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class AddAddressFragment extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String from;
    public AddAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Define
        EditText nameEdit = view.findViewById(R.id.address_nameEdit);
        EditText addressEdit= view.findViewById(R.id.address_edit);
        EditText noteEdit=view.findViewById(R.id.note_edit);
        Button btnXong=view.findViewById(R.id.btnXong);
        Button btnXoa=view.findViewById(R.id.btnXoa);
        TextView title=view.findViewById(R.id.toolbar_title);

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.addaddress_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        //Set address name
        String type="";
        if (getArguments()!=null) {
            type = getArguments().getString("type");
            from = getArguments().getString("from");
        }
        //set Nha, cong ty
        if (type!="" && !type.equals("Normal"))
            nameEdit.setText(type);
        //Home address show
        if(type.equals("Nhà")) {
            btnXoa.setVisibility(View.GONE);
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {

                                        addressEdit.setText((CharSequence) document.get("Nhà.Địa chỉ"));
                                        noteEdit.setText((((CharSequence) document.get("Nhà.Ghi chú"))));

                                    }
                                }
                            }
                        }

                    });
        }
        //company address show
        else if(type.equals("Công ty")) {
            btnXoa.setVisibility(View.GONE);
            db.collection("users").document(user.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {

                                        addressEdit.setText((CharSequence) document.get("Công ty.Địa chỉ"));
                                        noteEdit.setText((((CharSequence) document.get("Công ty.Ghi chú"))));

                                    }
                                }
                            }
                        }

                    });
        }

        //listview address show
        else if(type.equals("editAddress"))
        {
            title.setText("Sửa địa chỉ");
            db.collection("users").document(user.getUid()).collection("SaveAddress")
                    .document(getArguments().getString("doc")).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document != null) {
                                    if (document.exists()) {

                                        addressEdit.setText((CharSequence) document.get("address"));
                                        noteEdit.setText((((CharSequence) document.get("Ghi chú"))));
                                        nameEdit.setText((((CharSequence) document.get("name"))));
                                    }
                                }
                            }
                        }

                    });
        }
        else
        {
            btnXoa.setVisibility(View.GONE);
        }


        //buttonXongClick
        btnXong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update home address
                if(nameEdit.getText().toString().equals("Nhà"))
                {
                    if(addressEdit.getText().toString().equals(""))
                    {
                        Toast.makeText(getContext(), "Bạn chưa nhập địa chỉ!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        db.collection("users").document(user.getUid()).update(
                                "Nhà.Địa chỉ", addressEdit.getText().toString(),
                                "Nhà.Ghi chú",noteEdit.getText().toString()
                        );
                        Bundle bundle = new Bundle();
                        bundle.putString("from",from);
                        System.out.println("Nhìn: "+from);
                        Navigation.findNavController(getView()).navigate(R.id.action_addAddressFragment_to_addressFragment2,bundle);
                    }

                }
                //update company address
                else if(nameEdit.getText().toString().equals("Công ty"))
                    {
                        if(addressEdit.getText().toString().equals(""))
                        {
                            Toast.makeText(getContext(), "Bạn chưa nhập địa chỉ!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            db.collection("users").document(user.getUid()).update(
                                    "Công ty.Địa chỉ", addressEdit.getText().toString(),
                                    "Công ty.Ghi chú",noteEdit.getText().toString()
                            );
                            Bundle bundle = new Bundle();
                            bundle.putString("from",from);
                            Navigation.findNavController(getView()).navigate(R.id.action_addAddressFragment_to_addressFragment2,bundle);
                        }
                    }
                   else if(!nameEdit.getText().toString().equals("")) {
                    //update listview address
                    if (getArguments().getString("type").equals("editAddress")) {
                        title.setText("Sửa địa chỉ");
                        db.collection("users").document(user.getUid()).collection("SaveAddress")
                                .document(getArguments().getString("doc")).update("address", addressEdit.getText().toString(),
                                "name", nameEdit.getText().toString(), "Ghi chú", noteEdit.getText().toString());
                        Navigation.findNavController(getView()).navigate(R.id.action_addAddressFragment_to_addressFragment2);
                    } else {
                        if (addressEdit.getText().toString().equals("")) {
                            Toast.makeText(getContext(), "Bạn chưa nhập địa chỉ!", Toast.LENGTH_LONG).show();
                        } else {
                            Address address = new Address(nameEdit.getText().toString(), addressEdit.getText().toString());
                            db.collection("users").document(user.getUid()).collection("SaveAddress")
                                    .add(address).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    db.collection("users").document(user.getUid()).collection("SaveAddress")
                                            .document(documentReference.getId()).update("Ghi chú", noteEdit.getText().toString());
                                }
                            });
                            Bundle bundle = new Bundle();
                            bundle.putString("from", from);
                            Navigation.findNavController(getView()).navigate(R.id.action_addAddressFragment_to_addressFragment2, bundle);
                        }
                    }
                }
                        else
                        {
                            //add new address to listview

                            if (addressEdit.getText().toString().equals("")) {
                                Toast.makeText(getContext(), "Bạn chưa nhập địa chỉ!", Toast.LENGTH_LONG).show();
                            } else {
                                if (nameEdit.getText().toString().isEmpty()) {
                                    System.out.println("noname");
                                    String a = addressEdit.getText().toString();
                                    String[] name = a.split(",", 2);
                                    Address address = new Address(name[0].toString(), addressEdit.getText().toString());
                                    db.collection("users").document(user.getUid()).collection("SaveAddress")
                                            .add(address).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            db.collection("users").document(user.getUid()).collection("SaveAddress")
                                                    .document(documentReference.getId()).update("Ghi chú", noteEdit.getText().toString());
                                        }
                                    });
                                    Bundle bundle = new Bundle();
                                    bundle.putString("from", from);
                                    Navigation.findNavController(getView()).navigate(R.id.action_addAddressFragment_to_addressFragment2, bundle);
                                }
                            }
                        }
                }

        });
        btnXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                //AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Xác nhận!");
                builder.setMessage("Bạn chắc chắn muốn xóa?");

                //Yes Button

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("users").document(user.getUid()).collection("SaveAddress")
                                .document(getArguments().getString("doc")).delete();
                        Navigation.findNavController(getView()).navigate(R.id.action_addAddressFragment_to_addressFragment2);
                        dialog.dismiss();
                    }
                });

                //No Button
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
                alert.getButton(DialogInterface.BUTTON_POSITIVE)
                        .setTextColor(Color.parseColor("#795C34"));
                alert.getButton(DialogInterface.BUTTON_NEGATIVE)
                        .setTextColor(Color.parseColor("#795C34"));
            }
        });

    }
}