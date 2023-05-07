package com.example.koffi.fragment.staff;

import static android.content.ContentValues.TAG;
import static com.example.koffi.FunctionClass.setListViewHeight;

import android.app.Notification;
import android.database.DataSetObserver;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.koffi.NotificationApp;
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


public class DeliveryTabFragment extends Fragment {
    private NotificationManagerCompat notificationManagerCompat;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public DeliveryTabFragment() {
        // Required empty public constructor
    }


    public static DeliveryTabFragment newInstance(String param1, String param2) {
        DeliveryTabFragment fragment = new DeliveryTabFragment();
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
        return inflater.inflate(R.layout.fragment_delivery_tab, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Date date = Calendar.getInstance().getTime();
        ArrayList<Order> orderArray = new ArrayList<Order>();

        //Sample
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,4,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,5,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));
        orderArray.add(new Order("user123","Cẩm Tiên","store123", date,2,"123 Nhà","0123456789",new Long(35000),new Long(20000),new Long(55000),"ko co gi",0));

        ArrayList<String> idList = new ArrayList<String>();
        this.notificationManagerCompat = NotificationManagerCompat.from(getContext());
        ListView listView = view.findViewById(R.id.deliveryLv);
        TabLayout tabLayout = getParentFragment().getView().findViewById(R.id.order_tabLayout);
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Bundle bundle=new Bundle();

                Navigation.findNavController(getView()).navigate(R.id.action_staffOrderFragment_to_orderDetailFragment);
            }
        });


    }
}