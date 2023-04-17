package com.example.koffi.fragment.staff;

import static android.content.ContentValues.TAG;
import static com.example.koffi.FunctionClass.setListViewHeight;

import android.app.Notification;
import android.app.NotificationManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.example.koffi.NotificationApp;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.koffi.R;
import com.example.koffi.adapter.OrderAdapter;
import com.example.koffi.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class TATabFragment extends Fragment {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private NotificationManagerCompat notificationManagerCompat;
    public TATabFragment() {
    }

    public static TATabFragment newInstance(String param1, String param2) {
        TATabFragment fragment = new TATabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_t_a_tab, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        this.notificationManagerCompat = NotificationManagerCompat.from(getContext());
        ArrayList<Order> orderArray = new ArrayList<Order>();
        Date date = Calendar.getInstance().getTime();

        ListView listView = view.findViewById(R.id.deliveryLv);
        TabLayout tabLayout = getParentFragment().getView().findViewById(R.id.order_tabLayout);
        OrderAdapter orderAdapter = new OrderAdapter(getContext(),orderArray);
        listView.setAdapter(orderAdapter);
        setListViewHeight(listView);
        ArrayList<String> idList = new ArrayList<String>();

        //getData from firebase
        db.collection("order").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null)
                {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (user != null) {
                        db.collection("staff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                    for (QueryDocumentSnapshot document1 : task.getResult()) {
                                        if (user.getEmail().toString().equals(document1.getString("email"))) {
                                            if (document1.getString("store").toString().equals(dc.getDocument().getString("storeID"))) {
                                                if (dc.getDocument().getLong("method") == 1) {
                                                    {
                                                        switch (dc.getType()) {
                                                            case MODIFIED:
                                                                String message1;
                                                                if (dc.getDocument().getLong("status") == 1)
                                                                    message1 = "Bạn có đơn hàng mới: " + dc.getDocument().getString("orderID");
                                                                else if(dc.getDocument().getLong("status")==5)
                                                                    message1="Hủy đơn hàng: "+dc.getDocument().getString("orderID");
                                                                else if(dc.getDocument().getLong("status")==2)
                                                                    message1="Xác nhận đơn hàng: "+dc.getDocument().getString("orderID");
                                                                else if(dc.getDocument().getLong("status")==3)
                                                                    message1="Xác nhận chuẩn bị xong đơn hàng: "+dc.getDocument().getString("orderID");
                                                                else
                                                                    message1="Xác nhận hoàn thành đơn hàng: "+dc.getDocument().getString("orderID");

                                                                Notification notification = new NotificationCompat.Builder(getContext(), NotificationApp.CHANNEL_1_ID)
                                                                        .setSmallIcon(R.drawable.ic_action_notification)
                                                                        .setContentTitle("Thông báo!")
                                                                        .setContentText(message1)
                                                                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                                                                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                                                        .build();

                                                                int notificationId1 = 0;
                                                                notificationManagerCompat.notify(notificationId1, notification);
                                                                break;

                                                        }
                                                    }
                                                }
                                            }

                                        }
                                    }
                            }
                        });
                    }
                }


                orderArray.clear();
                orderAdapter.notifyDataSetChanged();


                if(user!=null) {
                    db.collection("staff").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful())
                                for (QueryDocumentSnapshot document1 : task.getResult()) {

                                    if (user.getEmail().toString().equals(document1.getString("email"))) {
                                        db.collection("order").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    orderArray.clear();
                                                    idList.clear();
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        if (document1.getString("store").toString().equals(document.getString("storeID"))) {
                                                            if (document.getLong("method") == 1) {
                                                                if (document.getLong("status") != 5&&document.getLong("status")!=4) {

                                                                    Order order =new Order(document.getString("orderID"), document.getString("userID"), document.getString("name")
                                                                            , document.getString("storeID"), date, document.getLong("status").intValue()
                                                                            , document.getString("address"), document.getString("phoneNumber")
                                                                            , document.getLong("subtotal"), document.getLong("ship")
                                                                            , document.getLong("total"), document.getString("deliveryNote")
                                                                            , document.getLong("method").intValue());
                                                                    orderArray.add(order);
                                                                    idList.add(document.getId());
                                                                    orderAdapter.notifyDataSetChanged();

                                                                }

                                                            }
                                                        }
                                                        tabLayout.getTabAt(1).getOrCreateBadge().setNumber(orderArray.size());
                                                    }
                                                }

                                                else {
                                                    Log.w(TAG, "Error getting documents.", task.getException());

                                                }
                                            }
                                        });
                                    }
                                }
                        }
                    });
                }


            }
        });
        orderAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(listView);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();
                String docID=idList.get(i).toString();
                bundle.putString("documentID",docID);
                bundle.putString("nhanhang","taicho");
                Navigation.findNavController(getView()).navigate(R.id.action_staffOrderFragment_to_orderDetailFragment,bundle);
            }
        });


    }
}