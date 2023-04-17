package com.example.koffi.fragment.store;


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.adapter.StoreAdapter;
import com.example.koffi.models.Store;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class StoreFragment extends Fragment {

    ListView listView;
    ArrayList<Store> storeArray;
    StoreAdapter adapter;
    EditText editText;
    String from="";

    public StoreFragment() {
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
        return inflater.inflate(R.layout.fragment_store, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Get argument
        if (getArguments()!=null) {
            from = getArguments().getString("from");
        }

        //Custom toolbar
        Toolbar toolbar = view.findViewById(R.id.store_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        //Init
        listView = view.findViewById(R.id.storesListView);
        storeArray = new ArrayList<Store>();
        adapter = new StoreAdapter(getContext(),storeArray);
        listView.setAdapter(adapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("stores").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            Store store = new Store(documentSnapshot.getId(), documentSnapshot.getString("address"),
                                    documentSnapshot.getString("image"), documentSnapshot.getString("phoneNumber"),
                                    documentSnapshot.getDouble("latitude"),documentSnapshot.getDouble("longitude"));
                            storeArray.add(store);
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        //Search store
        editText = view.findViewById(R.id.addressEdit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        //Store Detail Bottom Sheet
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_storedetail,
                        (LinearLayout)view.findViewById(R.id.store_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                //Assign data
                Store store = (Store) listView.getItemAtPosition(i);

                String address = store.address;
                String shortenedAddress = address.substring(0,address.indexOf(","));

                TextView shortAddressText = bottomSheetView.findViewById(R.id.storedetail_shortaddress);
                shortAddressText.setText(shortenedAddress);

                TextView addressText = bottomSheetView.findViewById(R.id.storedetail_fulladdress);
                addressText.setText(address);

                TextView phoneText = bottomSheetView.findViewById(R.id.storedetail_contact);
                phoneText.setText("Liên hệ: "+store.phoneNumber);

                RoundedImageView storeImage = bottomSheetView.findViewById(R.id.bottomsheet_image);
                int drawableId = view.getResources().getIdentifier(store.image, "drawable", getContext().getPackageName());
                storeImage.setImageResource(drawableId);

                //Hide bottomsheet
                ImageButton closeBtn = bottomSheetView.findViewById(R.id.store_closeBtn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                //Handle bottom sheet
                Button takeawayBtn = bottomSheetView.findViewById(R.id.store_takeawayBtn);
                takeawayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("store",store.id);
                        editor.putString("storeAddress",store.address);
                        editor.putInt("orderMethod",1);
                        editor.apply();

                        if (from.equals("checkout")) {
                            Navigation.findNavController(getView()).popBackStack();
                        }
                        else {
                            BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.main_bottom_nav);
                            bottomNavigationView.setSelectedItemId(R.id.delivery);
                            Navigation.findNavController(getView()).navigate(R.id.menuFragment);
                        }

                        bottomSheetDialog.dismiss();
                    }
                });

                //Show dialog
                bottomSheetDialog.show();

                //Store Full Address
                LinearLayout fullAddress = bottomSheetView.findViewById(R.id.store_fulladress);
                fullAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            // Create a Uri from an intent string. Use the result to create an Intent
                            Uri gmmIntentUri = Uri.parse("https://www.google.co.in/maps/dir//" + store.address);

                            // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
                            Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                            // Make the Intent explicit by setting the Google Maps package
                            intent.setPackage("com.google.android.apps.maps");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }   catch (ActivityNotFoundException e) {
                            // When google map is not installed
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
                            Intent ggPlayIntent = new Intent(Intent.ACTION_VIEW, uri);
                            ggPlayIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(ggPlayIntent);
                        }
                    }
                });

                //Share Address
                LinearLayout shareAddress = bottomSheetView.findViewById(R.id.store_shareaddress);
                shareAddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Hẹn bạn tại Koffi, ");
                        String directionsUrl = "https://www.google.co.in/maps/dir/''/'" + store.latitude + "," + store.longitude + "'/";
                        intent.putExtra(Intent.EXTRA_TEXT,
                                "https://www.google.co.in/maps/dir//" + store.address);
                        startActivity(Intent.createChooser(intent, "Chia sẻ với thiết bị"));
                    }
                });

                //Store Contact
                LinearLayout storeContact = bottomSheetView.findViewById(R.id.store_contact);
                storeContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+store.phoneNumber));
                        startActivity(intent);
                    }
                });
            }
        });

        //Show map
        Button mapBtn = view.findViewById(R.id.mapBtn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(getView()).navigate(R.id.action_storeFragment_to_mapsFragment);
            }
        });
    }

    private void filter (String text) {
        ArrayList<Store> filteredList = new ArrayList<>();

        for (Store store : storeArray) {
            if (store.address.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(store);
            }
        }
        adapter.filteredList(filteredList);
    }
}