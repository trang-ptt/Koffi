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

        }
        //company address show
        else if(type.equals("Công ty")) {
            btnXoa.setVisibility(View.GONE);

        }

        //listview address show

        else
        {
            btnXoa.setVisibility(View.GONE);
        }



    }
}