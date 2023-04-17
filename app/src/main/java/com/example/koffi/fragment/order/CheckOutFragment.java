package com.example.koffi.fragment.order;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.koffi.R;
import com.example.koffi.adapter.CartItemAdapter;
import com.example.koffi.adapter.ToppingAdapter;
import com.example.koffi.models.CartItem;
import com.example.koffi.models.Item;
import com.example.koffi.models.Order;
import com.example.koffi.models.Topping;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckOutFragment extends Fragment {

    TextView orderMethodTxt;
    Button changeOrderMethodBtn;
    int method;
    TextView bottomMethodText;

    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    String address;
    String addressName;


    String storeID;
    String storeAddress;


    public CheckOutFragment() {
        // Required empty public constructor
    }
    public static CheckOutFragment newInstance(String param1, String param2) {
        CheckOutFragment fragment = new CheckOutFragment();
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
        return inflater.inflate(R.layout.fragment_check_out, container, false);
    }

    ArrayList<CartItem> cart;
    long total;
    long subtotal;
    long number;
    FirebaseFirestore db;
    TextView tvSubtotal;
    TextView tvTotal;
    TextView tvTotal2;
    TextView tvNumber;
    CartItemAdapter cartAdapter;
    Item item;
    String receiverName;
    String receiverPhone;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(getView()).navigate(R.id.menuFragment);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
        db = FirebaseFirestore.getInstance();

        //Get store
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        storeID = sharedPref.getString("store" ,"Chọn cửa hàng");
        storeAddress = sharedPref.getString("storeAddress","Chọn cửa hàng");

        //Get address
        address = sharedPref.getString("dc","Chọn địa chỉ");
        addressName = sharedPref.getString("tendc","Chọn địa chỉ");

        //Get receiver info
        receiverName = sharedPref.getString("receiverName","Thêm tên");
        receiverPhone = sharedPref.getString("receiverPhone","Thêm số điện thoại");

        //Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.checkout_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        //Init
        bottomMethodText = view.findViewById(R.id.checkout_bottom_methodText);
        changeOrderMethodBtn = view.findViewById(R.id.checkout_changeBtn);
        orderMethodTxt = view.findViewById(R.id.checkout_ordermethod);

        //Handle order method
        method = sharedPref.getInt("orderMethod",0);
        if (method==0) {
            ship = 20000;
            deliveryMethod();
        }
        else if (method==1) {
            ship = 0;
            takeAwayMethod();
        }

        //Change orderMethod
        changeOrderMethodBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeOrderOnClick();
            }
        });

        cart = new ArrayList<CartItem>();
        if (getArguments().getParcelableArrayList("cartItems") != null)
        cart = getArguments().getParcelableArrayList("cartItems");
        total = 0;
        subtotal = 0;
        for (CartItem item : cart) {
            subtotal += item.price;
        }
        total = subtotal + ship;
        tvSubtotal = view.findViewById(R.id.cart_subtotal);
        tvSubtotal.setText(subtotal + "đ");
        tvTotal = view.findViewById(R.id.cart_total);
        tvTotal2 = view.findViewById(R.id.cart_total2);
        tvTotal.setText(total + "đ");
        tvTotal2.setText(total + "đ");
        tvNumber = view.findViewById(R.id.numberOfItems);
        number = getArguments().getLong("numberOfItems");
        tvNumber.setText(number + " sản phẩm");
        edtNote = view.findViewById(R.id.checkout_deliveryNote);

        //Autofill data
        TextView tvName = view.findViewById(R.id.checkout_tvName);
        TextView tvPhone = view.findViewById(R.id.checkout_tvPhone);
        TextView tvDate = view.findViewById(R.id.checkout_tvDate);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        tvDate.setText(strDate);
        tvName.setText(receiverName);
        tvPhone.setText(receiverPhone);
        if (tvName.getText().toString().equals("Thêm tên")||tvPhone.getText().toString().equals("Thêm số điện thoại"))
            db.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    receiverName = documentSnapshot.getString("Ten");
                    receiverPhone = documentSnapshot.getString("Sdt");
                    if (receiverName != null)
                        if (!receiverName.isEmpty()) {
                            tvName.setText(receiverName);
                            editor.putString("receiverName",receiverName);
                            editor.apply();
                            System.out.println("Dòng 207: "+receiverName);
                        }

                    if(receiverPhone.isEmpty())
                        System.out.println("Chốt phone nè: "+receiverPhone);
                    if (receiverPhone != null)
                        if (!receiverPhone.isEmpty() ) {
                            tvPhone.setText(receiverPhone);
                            editor.putString("receiverPhone",receiverPhone);
                            editor.apply();
                            System.out.println("Dòng 217: "+receiverPhone);
                        }

                }
            });

        //Receiver information
        LinearLayout receiver = view.findViewById(R.id.checkout_receiver);
        receiver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_checkout_receiver,
                        (LinearLayout)view.findViewById(R.id.receiver_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                //Set bottom sheet height
                setBottomSheetHeight(bottomSheetView);

                //Handle bottom sheet
                ImageButton backBtn = bottomSheetView.findViewById(R.id.receiver_backBtn);
                backBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                EditText edtName = bottomSheetView.findViewById(R.id.receiver_name);
                EditText edtPhone = bottomSheetView.findViewById(R.id.receiver_phone);
                if (receiverName != null)
                    if (!receiverName.isEmpty())
                        edtName.setText(receiverName);
                if (receiverPhone != null)
                    if (!receiverPhone.isEmpty())
                        edtPhone.setText(receiverPhone);
                Button doneBtn = bottomSheetView.findViewById(R.id.receiver_doneBtn);
                doneBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (edtName.getText().toString().isEmpty() || edtPhone.getText().toString().isEmpty()) {
                            Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin!", Toast.LENGTH_LONG).show();
                        } else {
                            receiverName = edtName.getText().toString().trim();
                            tvName.setText(receiverName);
                            editor.putString("receiverName",receiverName);
                            editor.apply();
                            receiverPhone = edtPhone.getText().toString().trim();
                            tvPhone.setText(receiverPhone);
                            System.out.println("Ở đây: "+ receiverPhone);
                            editor.putString("receiverPhone",receiverPhone);
                            editor.apply();
                            System.out.println("Bí mật mí: "+sharedPref.getString("receiverPhone","Không có nha má"));
                            bottomSheetDialog.dismiss();
                        }
                    }
                });
                //Show dialog
                bottomSheetDialog.show();
            }
        });

        //Listen to data change
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Query query = db.collection("order")
                    .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("status", 0);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            cartID = doc.getId();
                            db.collection("cartItems").whereEqualTo("cartID", doc.getId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null) {
                                                Log.w(TAG, "listen:error", error);
                                                return;
                                            }
                                            System.out.println("Db updated");
                                            reloadCart();
                                            for (DocumentChange dc : value.getDocumentChanges()) {
                                                switch (dc.getType()) {
                                                    case ADDED:
                                                        Log.d(TAG, "New cart item: " + dc.getDocument().getData());
                                                        break;
                                                    case MODIFIED:
                                                        Log.d(TAG, "Modified cart item: " + dc.getDocument().getData());
                                                        break;
                                                    case REMOVED:
                                                        Log.d(TAG, "Removed cart item: " + dc.getDocument().getData());
                                                        break;
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
            });
        }
        //Cart list
        ListView cartList = view.findViewById(R.id.cartList);
        cartAdapter = new CartItemAdapter(getContext(),cart,true);
        cartAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                setListViewHeight(cartList);
            }
        });
        cartList.setAdapter(cartAdapter);
        setListViewHeight(cartList);
        cartList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Bottom Sheet Dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_itemdetail,
                        (LinearLayout)view.findViewById(R.id.menu_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);

                TextView tvName = bottomSheetDialog.findViewById(R.id.tvName);
                tvName.setText(cart.get(i).item);

                RadioButton sizeM = bottomSheetDialog.findViewById(R.id.sizeM_radio);
                RadioButton sizeL = bottomSheetDialog.findViewById(R.id.sizeL_radio);

                ImageButton closeView = bottomSheetDialog.findViewById(R.id.itemdetail_closeBtn);
                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });

                if (cart.get(i).size.equals("Upsize")) {
                    sizeL.setChecked(true);
                    isL = true;
                    sizePrice = 6000;
                } else {
                    sizeM.setChecked(true);
                    isL = false;
                    sizePrice = 0;
                }

                TextView tvNumber = bottomSheetDialog.findViewById(R.id.tvNumber);
                tvNumber.setText(cart.get(i).quantity + "");
                Button totalBtn = bottomSheetDialog.findViewById(R.id.itemTotalPrice);
                totalBtn.setText("Thay đổi: " + cart.get(i).price + "đ");
                EditText edtNote = bottomSheetDialog.findViewById(R.id.edtNote);
                edtNote.setText(cart.get(i).note);
                numberUnit = cart.get(i).quantity;

                //Topping listview
                ListView toppingListView = bottomSheetDialog.findViewById(R.id.topping_listview);

                ArrayList<Topping> toppingArray = new ArrayList<Topping>();

                ToppingAdapter toppingAdapter = new ToppingAdapter(getContext(), toppingArray);
                toppingListView.setAdapter(toppingAdapter);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("toppings").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Topping topping = new Topping(documentSnapshot.getId(), documentSnapshot.getString("name"),
                                            documentSnapshot.getLong("price"));
                                    toppingArray.add(topping);
                                }
                                toppingAdapter.notifyDataSetChanged();
                                setListViewHeight(toppingListView);
                                bottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface dialogInterface) {
                                        for (int n = 0; n < toppingArray.size(); n++) {
                                            for (Topping cartTopping : cart.get(i).toppings) {
                                                if (cartTopping.id.equals(toppingArray.get(n).id)) {
                                                    CheckBox checkBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
                                                    checkBox.setChecked(true);
                                                    System.out.println(cartTopping.name);
                                                }
                                            }
                                        }
                                    }
                                });
                                toppingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                        CheckBox checkBox = toppingListView.getChildAt(i).findViewById(R.id.checkBox);
                                        checkBox.setChecked(!checkBox.isChecked());
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });

                                ImageButton plusBtn = bottomSheetDialog.findViewById(R.id.plusButton);
                                plusBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        numberUnit = Integer.parseInt(tvNumber.getText().toString()) + 1;
                                        tvNumber.setText(Long.toString(numberUnit));
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });

                                ImageButton minusBtn = bottomSheetDialog.findViewById(R.id.minusButton);
                                minusBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (Long.parseLong(tvNumber.getText().toString()) - 1 >= 0)
                                            numberUnit = Integer.parseInt(tvNumber.getText().toString()) - 1;
                                        if (numberUnit > 0) {
                                            tvNumber.setText(Long.toString(numberUnit));
                                            checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                        } else if (numberUnit == 0) {
                                            tvNumber.setText(Long.toString(numberUnit));
                                            totalBtn.setText("Xóa khỏi giỏ hàng");
                                        }
                                    }
                                });

                                sizeM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b) {
                                            sizeL.setChecked(false);
                                            isL = false;
                                        }
                                        if (isL) {
                                            sizePrice = 6000;
                                        }
                                        else {
                                            sizePrice = 0;
                                        }
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });
                                sizeL.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                    @Override
                                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                                        if (b) {
                                            sizeM.setChecked(false);
                                            isL = true;
                                        }
                                        if (isL) {
                                            sizePrice = 6000;
                                        }
                                        else {
                                            sizePrice = 0;
                                        }
                                        checkListViewCheckBox(toppingListView, toppingArray, bottomSheetDialog, numberUnit);
                                    }
                                });

                                totalBtn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Query query = db.collection("cartItems")
                                                .whereEqualTo("cartID", cart.get(i).cartID)
                                                .whereEqualTo("item", cart.get(i).item)
                                                .whereEqualTo("size", cart.get(i).size)
                                                .whereEqualTo("toppings", cart.get(i).toppings);
                                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        docID = snapshot.getId();
                                                        System.out.println(docID);
                                                        if (numberUnit != 0) {
                                                            long sum = 0;
                                                            for (int n = 0; n < 8; n++) {
                                                                CheckBox itemCheckBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
                                                                if (itemCheckBox.isChecked()) {
                                                                    sum += toppingArray.get(n).price;
                                                                }
                                                            }
                                                            note = edtNote.getText().toString().trim();
                                                            size = sizeL.isChecked() ? "Upsize" : "Vừa";
                                                            ArrayList<Topping> toppingToCart = new ArrayList<>();
                                                            for (int i = 0; i < 8; i++) {
                                                                CheckBox checkBox = toppingListView.getChildAt(i).findViewById(R.id.checkBox);
                                                                if (checkBox.isChecked()) {
                                                                    toppingToCart.add(toppingArray.get(i));
                                                                }
                                                            }
                                                            totalUnit = (unit + sum + sizePrice) * numberUnit;
                                                            Query find = db.collection("cartItems")
                                                                    .whereEqualTo("cartID", cart.get(i).cartID)
                                                                    .whereEqualTo("item", cart.get(i).item)
                                                                    .whereEqualTo("size", size)
                                                                    .whereEqualTo("toppings", toppingToCart);
                                                            find.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                    if (task.isSuccessful()) {
                                                                        CartItem itemInCart = new CartItem();
                                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                            exist = documentSnapshot.getId();
                                                                            itemInCart = documentSnapshot.toObject(CartItem.class);
                                                                        }
                                                                        if (exist.equals(docID) || exist.isEmpty()) {
                                                                            db.collection("cartItems").document(docID)
                                                                                    .update("note", note, "size", size,
                                                                                            "quantity", numberUnit, "price", totalUnit,
                                                                                            "toppings", toppingToCart).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                @Override
                                                                                public void onSuccess(Void unused) {
                                                                                    Toast.makeText(getContext(), "Đã cập nhật sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                                    bottomSheetDialog.dismiss();
                                                                                }
                                                                            });
                                                                        } else {
                                                                            System.out.println("Doc exist: " + exist);
                                                                            db.collection("cartItems").document(exist)
                                                                                    .update("note", note,
                                                                                            "quantity", numberUnit + itemInCart.quantity,
                                                                                            "price", totalUnit + itemInCart.price)
                                                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                        @Override
                                                                                        public void onSuccess(Void unused) {
                                                                                            db.collection("cartItems").document(docID).delete();
                                                                                            Toast.makeText(getContext(), "Đã cập nhật sản phẩm trong giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                                            bottomSheetDialog.dismiss();
                                                                                        }
                                                                                    });
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }
                                                        else {
                                                            db.collection("cartItems").document(docID)
                                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(getContext(), "Đã xóa khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                                                                    bottomSheetDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                                //Show dialog
                                bottomSheetDialog.show();
                            }
                        });

                //Get cart item
                db.collection("menu").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("menu")
                                        .document(document.getId())
                                        .collection("items")
                                        .whereEqualTo("name", cart.get(i).item)
                                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                            System.out.println("Found item");
                                            item = snapshot.toObject(Item.class);
                                            TextView tvPrice = bottomSheetDialog.findViewById(R.id.tvPrice);
                                            tvPrice.setText(item.price + "đ");
                                            TextView tvDes = bottomSheetDialog.findViewById(R.id.tvDescription);
                                            tvDes.setText(item.description);
                                            ImageView imageView = bottomSheetView.findViewById(R.id.itemdetail_image);
                                            int drawableId = view.getResources().getIdentifier(item.image, "drawable", getContext().getPackageName());
                                            imageView.setImageResource(drawableId);
                                            unit = item.price;
                                        }
                                    }
                                });
                                if (item != null) break;
                            }
                        }
                    }
                });
            }
        });

        //Add more item
        Button addBtn = view.findViewById(R.id.checkout_addBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(view).navigate(R.id.menuFragment);
            }
        });

        //Payment Method
//        LinearLayout payment = view.findViewById(R.id.checkout_payment);
//        payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Bottom sheet dialog
//                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
//                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_paymentmethod,
//                        (LinearLayout)view.findViewById(R.id.paymentmethod_bottomsheet));
//                bottomSheetDialog.setContentView(bottomSheetView);
//                //Set bottom sheet height
//                setBottomSheetHeight(bottomSheetView);
//                //Handle bottom sheet
//
//                //Show dialog
//                bottomSheetDialog.show();
//            }
//        });

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        LinearLayout deleteOrder = view.findViewById(R.id.checkout_deleteorder);
        deleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db.collection("order")
                        .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .whereEqualTo("status", 0);
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                db.collection("cartItems")
                                        .whereEqualTo("cartID", documentSnapshot.getId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        db.collection("cartItems")
                                                                .document(snapshot.getId())
                                                                .delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        cart.clear();
                                                                        cartAdapter.notifyDataSetChanged();
                                                                        tvNumber.setText("0 sản phẩm");
                                                                        tvSubtotal.setText("0đ");
                                                                        tvTotal.setText("20000đ");
                                                                        tvTotal2.setText("0đ");
                                                                    }
                                                                });
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
            }
        });
        //Navigate to OrderFragment
        Button orderBtn = view.findViewById(R.id.orderBtn);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cart.isEmpty()) {
                    Toast.makeText(getContext(),"Giỏ hàng của bạn không có gì hết! Vui lòng chọn sản phẩm!", Toast.LENGTH_LONG).show();
                }
                else if (receiverName == null || receiverPhone == null) {
                    Toast.makeText(getContext(),"Vui lòng nhập đầy đủ thông tin người nhận", Toast.LENGTH_LONG).show();
                }
                else if (receiverName.isEmpty() || receiverPhone.isEmpty()) {
                    Toast.makeText(getContext(),"Vui lòng nhập đầy đủ thông tin người nhận", Toast.LENGTH_LONG).show();
                } else {
                    if (method == 0 && address.isEmpty()) {
                        Toast.makeText(getContext(),"Vui lòng chọn địa chỉ giao", Toast.LENGTH_LONG).show();
                    } else if (method == 1 && storeAddress.isEmpty()) {
                        Toast.makeText(getContext(),"Vui lòng chọn cửa hàng nhận", Toast.LENGTH_LONG).show();
                    } else {
                        Date date = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        Bundle bundle = new Bundle();
                        bundle.putInt("method", method);
                        bundle.putString("orderID", cartID);
                        bundle.putParcelableArrayList("orderItems", cart);
                        bundle.putLong("total", total);
                        bundle.putLong("subtotal", subtotal);
                        bundle.putLong("numberOfItems", number);
                        bundle.putString("receiverName", receiverName);
                        bundle.putString("receiverPhone", receiverPhone);
                        bundle.putString("from", "checkout");
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                        bundle.putString("time", sdf.format(new Date()));
                        if (method == 0) {
                            bundle.putString("address", address);
                        } else
                            bundle.putString("address", storeAddress);
                        if (method == 0) {
                            Geocoder geocoder = new Geocoder(getContext());
                            try {
                                List<Address> addressList;
                                addressList = geocoder.getFromLocationName(address, 1);
                                if (addressList != null && addressList.size() > 0) {
                                    double lat = addressList.get(0).getLatitude();
                                    double lng = addressList.get(0).getLongitude();
                                    float[] result = new float[1];
                                    float[] min = new float[1];
                                    min[0] = Float.MAX_VALUE;
                                    db.collection("stores").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                            Location.distanceBetween(lat,lng,
                                                                    snapshot.getDouble("latitude"), snapshot.getDouble("longitude"), result);
                                                            float distance = result[0] / 1000;
                                                            if (min[0] > distance) {
                                                                min[0] = distance;
                                                                storeID = snapshot.getId();
                                                            }
                                                        }
                                                        if (min[0] > 15) {
                                                            Toast.makeText(getContext(), "Địa chỉ của bạn nằm ngoài bán kính giao hàng (15km) nên đơn hàng không thể đặt", Toast.LENGTH_LONG).show();
                                                        } else {
                                                            String deliveryNote = edtNote.getText().toString().trim();
                                                            db.collection("order").document(cartID)
                                                                    .update("address", address, "orderID", cartID,
                                                                            "date", formatter.format(date),
                                                                            "deliveryNote", deliveryNote, "method", 0,
                                                                            "name", receiverName, "phoneNumber", receiverPhone,
                                                                            "ship", ship, "status", 1, "storeID", storeID,
                                                                            "subtotal", subtotal, "total", total);
                                                            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                                                            //Create new cart
                                                            Order order = new Order(
                                                                    FirebaseAuth.getInstance().getCurrentUser().getUid(), 0
                                                            );
                                                            db.collection("order").add(order);
                                                            Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_orderFragment, bundle);
                                                        }
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Không tìm thấy địa chỉ, vui lòng nhập địa chỉ khác", Toast.LENGTH_SHORT).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        else if (method == 1) {
                            db.collection("order").document(cartID)
                                    .update("date", formatter.format(date), "orderID", cartID,
                                            "method", 1, "storeID", storeID, "address", storeAddress,
                                            "name", receiverName, "phoneNumber", receiverPhone,
                                            "ship", ship, "status", 1,
                                            "subtotal", subtotal, "total", total);
                            Toast.makeText(getContext(), "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                            //Create new cart
                            Order order = new Order(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid(), 0
                            );
                            db.collection("order").add(order);
                            Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_orderFragment, bundle);
                        }
                    }
                }
            }
        });
    }

    long totalUnit;
    boolean isL = false;
    long unit;
    long sizePrice = 0;
    int numberUnit;
    String size;
    String note;
    EditText edtNote;
    int ship = 0;
    String docID;
    String exist = "";
    String cartID;

    private void checkListViewCheckBox(ListView toppingListView, ArrayList<Topping> toppingArray, BottomSheetDialog bottomSheetView, long number) {
        long sum = 0;
        for (int n = 0; n < 8; n++) {
            CheckBox itemCheckBox = toppingListView.getChildAt(n).findViewById(R.id.checkBox);
            if (itemCheckBox.isChecked()) {
                sum += toppingArray.get(n).price;
            }
        }
        Button totalBtn = bottomSheetView.findViewById(R.id.itemTotalPrice);
        totalBtn.setText("Thay đổi: " + (unit + sum + sizePrice) * number + "đ");
        System.out.println("Unit: " + unit + "\tSum: " + sum + "\tSize price: " + sizePrice + "\tNumber: " + number);
    }

    private void reloadCart() {
        cart.clear();
        total = 0;
        subtotal = 0;
        number = 0;
        Query query = db.collection("order")
                .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                .whereEqualTo("status", 0);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    cart.clear();
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        db.collection("cartItems").whereEqualTo("cartID", doc.getId())
                                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() == 0) {
                                        tvNumber.setText("0 sản phẩm");
                                        tvSubtotal.setText("0đ");
                                        tvTotal.setText("0đ");
                                        tvTotal2.setText("20000đ");
                                    }
                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                        CartItem cartItem = snapshot.toObject(CartItem.class);
                                        cart.add(cartItem);
                                        subtotal += cartItem.price;
                                        number += cartItem.quantity;
                                    }
                                    total = subtotal + ship;
                                    tvNumber.setText(number + " sản phẩm");
                                    tvSubtotal.setText(subtotal + "đ");
                                    tvTotal.setText(total + "đ");
                                    tvTotal2.setText(total + "đ");
                                    cartAdapter.notifyDataSetChanged();
                                }
                            }
                        });
                    }
                }
            }
        });
    }
    public void setListViewHeight(ListView listview) {
        ListAdapter listAdapter = listview.getAdapter();
        if (listAdapter != null) {
            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listview);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = listview.getLayoutParams();
            params.height = totalHeight + (listview.getDividerHeight() * (listAdapter.getCount() - 1));
            listview.setLayoutParams(params);
        }
    }
    public void setBottomSheetHeight(View bottomSheetView) {
        ViewGroup.LayoutParams lp =bottomSheetView.getLayoutParams();
        lp.height= Resources.getSystem().getDisplayMetrics().heightPixels;
        bottomSheetView.setLayoutParams(lp);
    }
    public void deliveryMethod(){
        LinearLayout delivery = getView().findViewById(R.id.checkout_delivery);
        LinearLayout takeaway = getView().findViewById(R.id.takeaway_store);
        LinearLayout ship = getView().findViewById(R.id.checkout_ship);

        delivery.setVisibility(View.VISIBLE);
        takeaway.setVisibility(View.GONE);
        ship.setVisibility(View.VISIBLE);

        orderMethodTxt.setText("Giao tận nơi");
        bottomMethodText.setText("Giao tận nơi • ");

        //Address
        TextView txtTendc= getView().findViewById(R.id.tendc_checkout);
        TextView txtdc=getView().findViewById(R.id.dc_checkout);

        txtTendc.setText(addressName);
        txtdc.setText(address);

        //Navigate to Address Fragment
        LinearLayout chooseAddress = getView().findViewById(R.id.checkout_chooseAddress);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from","checkout");
                Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_addressFragment22,bundle);
            }
        });
    }
    public void takeAwayMethod() {
        LinearLayout delivery = getView().findViewById(R.id.checkout_delivery);
        LinearLayout takeaway = getView().findViewById(R.id.takeaway_store);
        LinearLayout ship = getView().findViewById(R.id.checkout_ship);
        TextView addressNameTxt = getView().findViewById(R.id.checkout_takeaway_addressName);
        TextView addressTxt = getView().findViewById(R.id.checkout_takeaway_address);

        delivery.setVisibility(View.GONE);
        takeaway.setVisibility(View.VISIBLE);
        ship.setVisibility(View.GONE);

        //Address
        if (!storeAddress.equals("Chọn cửa hàng")) {
            String addressName = storeAddress.substring(0,storeAddress.indexOf(","));
            addressNameTxt.setText(addressName);
            addressTxt.setText(storeAddress);
        }
        else {
            editor.putString("storeAddress","Tầng 50 Bitexco Tower, 2 Hải Triều, Phường Bến Nghé, Quận 1");
            editor.putString("store","store-1");
            editor.apply();
        }


        orderMethodTxt.setText("Tự đến lấy");
        bottomMethodText.setText("Tự đến lấy • ");

        //Choose store
        LinearLayout chooseAddress = getView().findViewById(R.id.takeaway_store);
        chooseAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("from","checkout");
                Navigation.findNavController(getView()).navigate(R.id.action_checkOutFragment_to_storeFragment,bundle);
            }
        });
    }
    public void changeOrderOnClick() {
        //Bottom sheet dialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_ordermethod,
                (LinearLayout)getView().findViewById(R.id.ordermethod_bottomsheet));
        bottomSheetDialog.setContentView(bottomSheetView);

        //Handle bottom sheet
        Button editDeliveryBtn = bottomSheetView.findViewById(R.id.delivery_editBtn);
        Button editTABtn = bottomSheetView.findViewById(R.id.takeaway_editBtn);
        TextView addressTxt = bottomSheetView.findViewById(R.id.delivery_address);
        TextView storeAddressTxt = bottomSheetView.findViewById(R.id.checkout_address);

        editDeliveryBtn.setVisibility(View.GONE);
        editTABtn.setVisibility(View.GONE);
        addressTxt.setText(address);
        storeAddressTxt.setText(storeAddress);

        //Delivery
        LinearLayout delivery = bottomSheetView.findViewById(R.id.ordermethod_delivery);
        delivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ship = 20000;
                total = subtotal + ship;
                tvTotal.setText(total + "đ");
                tvTotal2.setText(total + "đ");
                method=0;
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("orderMethod",method);
                editor.apply();
                deliveryMethod();
                bottomSheetDialog.dismiss();
            }
        });

        //Take away
        LinearLayout takeaway = bottomSheetView.findViewById(R.id.ordermethod_takeaway);
        takeaway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ship = 0;
                total = subtotal + ship;
                tvTotal.setText(total + "đ");
                tvTotal2.setText(total + "đ");
                method=1;
                SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt("orderMethod",method);
                editor.apply();
                takeAwayMethod();
                bottomSheetDialog.dismiss();
            }
        });

        //Show dialog
        bottomSheetDialog.show();
    }
}