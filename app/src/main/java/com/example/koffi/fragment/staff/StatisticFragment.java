package com.example.koffi.fragment.staff;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.adapter.OrderAdapter;
import com.example.koffi.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatisticFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public StatisticFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatisticFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatisticFragment newInstance(String param1, String param2) {
        StatisticFragment fragment = new StatisticFragment();
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
        return inflater.inflate(R.layout.fragment_statistic, container, false);
    }

    FirebaseFirestore db;
    FirebaseUser user;
    CartItemAdapter cartAdapter;
    TextView textView;
    EditText editText;
    static final String TAG = "Read Data Activity";
    ArrayList<String> idList;
    ArrayList<Order> orderArray;
    OrderAdapter orderAdapter;
    ListView listView;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orderArray = new ArrayList<Order>();

        //Sample data
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,4,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));

        listView = view.findViewById(R.id.statisticLv);
        orderAdapter = new OrderAdapter(getContext(),orderArray);
        listView.setAdapter(orderAdapter);

        db = FirebaseFirestore.getInstance();
        db.collection("order").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed", error);
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    Log.d(TAG, "Modified Order: " + dc.getDocument().getData());
                }
                orderArray.clear();
                orderAdapter.notifyDataSetChanged();
                user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    db.collection("staff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document1 : task.getResult()) {
                                    if (user.getEmail().toString().equals(document1.getString("email"))) {
                                        db.collection("order").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document1.getString("store").toString().equals(document.getString("storeID"))) {

                                                            if (document.get("status", Integer.class) == 5 || document.get("status", Integer.class) == 4) {
                                                                String sDate1 = document.getString("date");
                                                                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                                                                Order order = null;

                                                                try {
                                                                    order = new Order(document.getString("orderID"), document.getString("userID"), document.getString("name")
                                                                            , document.getString("storeID"), df.parse(sDate1), document.getLong("status").intValue()
                                                                            , document.getString("address"), document.getString("phoneNumber")
                                                                            , document.getLong("subtotal"), document.getLong("ship")
                                                                            , document.getLong("total"), document.getString("deliveryNote")
                                                                            , document.getLong("method").intValue());

                                                                } catch (ParseException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                orderArray.add(order);

                                                            }
                                                        }
                                                    }
                                                    if (orderArray.size() > 1) {
                                                        Collections.sort(orderArray, Collections.reverseOrder());
                                                    }
                                                    orderAdapter.notifyDataSetChanged();
                                                    textView = view.findViewById(R.id.number_order);
                                                    textView.setText(orderArray.size()+"");
                                                }
                                                else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });

        //Search order
        editText = view.findViewById(R.id.search_order);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("documentID", orderArray.get(i).orderID);
                bundle.putString("from", "yes");
                Navigation.findNavController(getView()).navigate(R.id.action_statisticFragment_to_orderDetailFragment, bundle);
            }
        });
    }
    private void filter (String text) {
        ArrayList<Order> filteredList = new ArrayList<>();

        for (Order order : orderArray) {
            if (order.orderID.toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(order);
            };
        }
        orderAdapter.filteredList(filteredList);
    }
}