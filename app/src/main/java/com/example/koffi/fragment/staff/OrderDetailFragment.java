package com.example.koffi.fragment.staff;

import static android.content.ContentValues.TAG;
import static com.example.koffi.FunctionClass.setListViewHeight;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.dialog.CancelOrderDialog;
import com.example.koffi.models.CartItem;
import com.example.koffi.models.Topping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class OrderDetailFragment extends Fragment {

    TextView title;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static String docID;
    private String mParam1;
    private String mParam2;

    public OrderDetailFragment() {
        // Required empty public constructor
    }

    ArrayList<CartItem> cart=new ArrayList<CartItem>();
    public static OrderDetailFragment newInstance(String param1, String param2) {
        OrderDetailFragment fragment = new OrderDetailFragment();
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
        return inflater.inflate(R.layout.fragment_order_detail, container, false);
    }

    String orderID;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.orderdetail_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Init
        Button cancelBtn = view.findViewById(R.id.orderdetail_cancelBtn);
        Button btnchangeState=view.findViewById(R.id.orderdetail_confirmBtn);
        TextView txtSubtotal=view.findViewById(R.id.order_subtotal);
        TextView Total=view.findViewById(R.id.order_total);
        TextView orderid=view.findViewById(R.id.order_id);
        TextView ordername=view.findViewById(R.id.order_name);
        TextView orderphone=view.findViewById(R.id.order_phone);
        TextView orderaddress=view.findViewById(R.id.order_address);
        TextView state=view.findViewById(R.id.state_order);
        TextView note=view.findViewById(R.id.note_order);
        TextView kieunhan=view.findViewById(R.id.kieunhan_txt);
        TextView tvAddress = view.findViewById(R.id.order_tvAddress);
        TextView tvNote = view.findViewById(R.id.detail_note);

        if(getArguments().getString("documentID")!=null)
            orderID=getArguments().getString("documentID");

        //listview
        ListView cartList = view.findViewById(R.id.orderdetail_cartList);
        CartItemAdapter cartAdapter = new CartItemAdapter(getContext(),cart,false);
        cartAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(cartList);
            }
        });
        cartList.setAdapter(cartAdapter);
        setListViewHeight(cartList);
        TextView tvNum = view.findViewById(R.id.order_number);
        int num[] = new int[1];
        db.collection("cartItems").whereEqualTo("cartID", orderID)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    num[0] = 0;
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        CartItem cartItem = snapshot.toObject(CartItem.class);
                        num[0] += cartItem.quantity;
                        cart.add(cartItem);
                    }
                    tvNum.setText("(" + num[0] + " món)");
                    cartAdapter.notifyDataSetChanged();
                }
            }
        });
        cartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String text = "Sản phẩm: " + cart.get(i).item;
                text += "\nSize: " + cart.get(i).size + "\nToppings: ";
                for (Topping topping : cart.get(i).toppings) {
                    text += "\n+ " + topping.name;
                }
                text += "\nGhi chú: ";
                if (cart.get(i).note != null && !cart.get(i).note.isEmpty()) {
                    text += cart.get(i).note;
                } else text += "Không có ghi chú";
                AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                Context context = getContext();
                alert.setTitle("Thông tin chi tiết: ");
                alert.setMessage(text);
                alert.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
                dialog.getButton(dialog.BUTTON_POSITIVE)
                    .setTextColor(Color.parseColor("#795C34"));
            }
        });

        //setInfomation
        if(getArguments().getString("documentID")!=null) {
            title = view.findViewById(R.id.order_title);
        //settype
        if(getArguments().getString("nhanhang")!=null&&getArguments().getString("nhanhang").toString().equals("taicho"))
        {
            kieunhan.setText("(Tự đến lấy)");
            tvAddress.setText("Cửa hàng:  ");
        }
        if(getArguments().getString("from") != null && getArguments().getString("from").equals("yes")) {
            cancelBtn.setVisibility(View.GONE);
            btnchangeState.setVisibility(View.GONE);
        }
        //Setting
        FragmentManager fm = getParentFragmentManager();
        int count = fm.getBackStackEntryCount();

            DocumentReference docRef = db.collection("order").document(getArguments().getString("documentID"));
            db.collection("order").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(error!=null)
                    {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        Log.d(TAG, "Modified Order: " + dc.getDocument().getData());
                    }
                    docRef.get().addOnCompleteListener(new com.google.android.gms.tasks.OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    txtSubtotal.setText(document.getLong("subtotal").toString()+"đ");
                                    Total.setText(document.getLong("total").toString()+"đ");
                                    orderid.setText(document.getString("orderID"));
                                    ordername.setText(document.getString("name"));
                                    orderphone.setText(document.getString("phoneNumber"));
                                    orderaddress.setText(document.getString("address"));
                                    if(document.getString("deliveryNote")!=null)
                                        if(!document.getString("deliveryNote").equals(""))
                                            note.setText(document.getString("deliveryNote"));
                                        long status = document.getLong("status");
                                    if (status == 1)
                                        state.setText("Chờ xác nhận");
                                    else if (status == 2)
                                        state.setText("Đang chuẩn bị");
                                    else if (status == 3)
                                        state.setText("Đang giao hàng");
                                    else if (status == 4)
                                        state.setText("Đã hoàn thành");
                                    if (status == 1)
                                        btnchangeState.setText("Xác nhận đơn hàng");
                                    else if (status == 2)
                                        btnchangeState.setText("Xác nhận chuẩn bị xong");
                                    else if (status == 3)
                                        btnchangeState.setText("Xác nhận đã giao");
                                    if (status == 5) {
                                        btnchangeState.setVisibility(View.GONE);
                                        cancelBtn.setVisibility(View.GONE);
                                        state.setText("Đã hủy");
                                        tvNote.setText("Lý do hủy: ");
                                    }
                                    btnchangeState.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (document.getLong("status") == 1) {
                                                Toast.makeText(Total.getContext(), "Đã xác nhận đơn hàng!", Toast.LENGTH_LONG).show();
                                                docRef.update("confirmTime", sdf.format(new Date()));
                                                Navigation.findNavController(getView()).popBackStack();
                                            }
                                            else
                                                if(document.getLong("status")==2) {
                                                    Toast.makeText(Total.getContext(), "Xác nhận chuẩn bị xong!", Toast.LENGTH_LONG).show();
                                                    docRef.update("serveTime", sdf.format(new Date()));
                                                    Navigation.findNavController(getView()).popBackStack();
                                                }
                                           else if (document.getLong("status") == 3) {
                                                    Toast.makeText(Total.getContext(), "Xác nhận đã giao hàng!", Toast.LENGTH_LONG).show();
                                                    docRef.update("deliTime", sdf.format(new Date()));
                                                    Navigation.findNavController(getView()).popBackStack();
                                                }
                                            docRef.update("status",document.getLong("status")+1);
                                        }
                                    });
                                    //Cancel order
                                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            docID=document.getId().toString();
                                            CancelOrderDialog cancelDialog = new CancelOrderDialog(getContext());
                                            cancelDialog.show();
                                        }
                                    });

                                }
                            }
                        }
                    });
                }
            });

        }


    }
    private void ClientSideSetting() {
        title.setText("Đơn hàng của bạn");
    }
}