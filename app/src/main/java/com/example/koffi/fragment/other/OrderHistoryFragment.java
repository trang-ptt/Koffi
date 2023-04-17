package com.example.koffi.fragment.other;

import static com.example.koffi.FunctionClass.setListViewHeight;

import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.koffi.R;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.adapter.OrderAdapter;
import com.example.koffi.models.CartItem;
import com.example.koffi.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class OrderHistoryFragment extends Fragment {

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    public static OrderHistoryFragment newInstance(String param1, String param2) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    FirebaseFirestore db;
    FirebaseUser user;
    CartItemAdapter cartAdapter;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.otherFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        user = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.history_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        ArrayList<Order> orderArray = new ArrayList<>();

        //Sample data
        Date date = Calendar.getInstance().getTime();
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,4,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
//        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,2,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));

        ListView listView = view.findViewById(R.id.historyLv);
        OrderAdapter orderAdapter = new OrderAdapter(getContext(),orderArray);
        listView.setAdapter(orderAdapter);
        setListViewHeight(listView);
        orderAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(listView);
            }
        });
        db.collection("order").whereEqualTo("userID", user.getUid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        orderArray.clear();
                        getOrderArray(orderArray, orderAdapter);
                    }
                });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ArrayList<CartItem> cart = new ArrayList<>();
                Bundle bundle = new Bundle();
                Order order = (Order) listView.getItemAtPosition(i);
                if (order.status==4 || order.status==5) {
                    bundle.putString("from", "yes");
                    if (order.method == 1) {
                        bundle.putString("nhanhang", "taicho");
                    }
                    bundle.putString("documentID", order.orderID);
                    Navigation.findNavController(getView()).navigate(R.id.action_orderHistoryFragment_to_orderDetailFragment2, bundle);
                }
                else {
//                                bundle.putParcelableArrayList("orderItems", cart);
//                                bundle.putLong("numberOfItems", num[0]);
                    bundle.putInt("method", order.method);
                    bundle.putString("orderID", order.orderID);
                    bundle.putLong("total", order.total);
                    bundle.putLong("subtotal", order.subtotal);
                    bundle.putString("receiverName", order.name);
                    bundle.putString("receiverPhone", order.phoneNumber);
                    bundle.putString("address", order.address);
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    bundle.putString("time", sdf.format(order.date));
                    Navigation.findNavController(getView()).navigate(R.id.action_orderHistoryFragment_to_orderFragment, bundle);
                }
//                cartAdapter = new CartItemAdapter(getContext(),cart,true);
//
//                long num[] = new long[1];
//                num[0] = 0;
//                db.collection("cartItems").whereEqualTo("orderID", order.orderID)
//                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
//                                CartItem item = snapshot.toObject(CartItem.class);
//                                cart.add(item);
//                                num[0] += item.quantity;
//                            }
//                            cartAdapter.notifyDataSetChanged();
//
//                        }
//                    }
//                });
            }
        });
    }

    private void getOrderArray(ArrayList<Order> orderArray, OrderAdapter orderAdapter) {
        db.collection("order").whereEqualTo("userID", user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    orderArray.clear();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        if (snapshot.get("status", Integer.class) != 0) {
                            System.out.println("found order");
                            String sDate1 = snapshot.getString("date");
                            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                            try {
                                Date date1 = df.parse(sDate1);
                                System.out.println(date1);
                                String orderID = snapshot.getId();
                                String address = snapshot.getString("address");
                                String deliveryNote = snapshot.getString("deliveryNote");
                                int method = snapshot.get("method", Integer.class);
                                String name = snapshot.getString("name");
                                String phone = snapshot.getString("phoneNumber");
                                long ship = snapshot.getLong("ship");
                                int status = snapshot.get("status", Integer.class);
                                String storeID = snapshot.getString("storeID");
                                long subtotal = snapshot.getLong("subtotal");
                                long total = snapshot.getLong("total");
                                String userID = snapshot.getString("userID");
                                Order order = new Order(orderID, userID, name, storeID, date1, status, address, phone,
                                        subtotal, ship, total, deliveryNote, method);
                                orderArray.add(order);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (orderArray.size() > 1) {
                        Collections.sort(orderArray, Collections.reverseOrder());
                    }
                    orderAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater inflater1 = getActivity().getMenuInflater();
        inflater1.inflate(R.menu.order_history_option_menu, menu);
    }
}