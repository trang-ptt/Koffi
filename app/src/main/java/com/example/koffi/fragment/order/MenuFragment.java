package com.example.koffi.fragment.order;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.koffi.R;
import com.example.koffi.adapter.CategoryAdapter;
import com.example.koffi.adapter.MenuAdapter;
import com.example.koffi.models.CartItem;
import com.example.koffi.models.Category;
import com.example.koffi.models.Item;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;

public class MenuFragment extends Fragment {

    FirebaseFirestore db;
    private GridView gridView;
    private RecyclerView recyclerView;
    private ArrayList<Category> menuArray;
    private CategoryAdapter categoryAdapter;
    private MenuAdapter menuAdapter;
    private LinearLayout menuLinear;
    private LinearLayout rootLinear;
    private BottomAppBar bottomAppBar;

    private int orderMethod;
    private ImageView methodImage;
    private TextView methodText;
    private TextView addressText;

    SharedPreferences sharedPref;
    String address;
    String storeAddress;
    Item homeItem=null;


    public MenuFragment() {
        // Required empty public constructor
    }

    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        //Get argument
        if (getArguments()!=null) {
            homeItem = getArguments().getParcelable("homeItem");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Init
        methodImage = view.findViewById(R.id.menu_methodImage);
        methodText = view.findViewById(R.id.menu_methodText);
        addressText = view.findViewById(R.id.menu_addressText);

        //Get order method
        sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        orderMethod = sharedPref.getInt("orderMethod",0);

        //Get address
        address = sharedPref.getString("dc","Chọn địa chỉ");
        storeAddress = sharedPref.getString("storeAddress","Chọn cửa hàng");

        //Set address
        if (orderMethod==0) {
            addressText.setText(address);
        }
        else if (orderMethod==1) {
            addressText.setText(storeAddress);
        }

        //Set order method
        setOrderMethod(orderMethod);

        //Toolbar
        Toolbar toolbar = view.findViewById(R.id.menu_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();

        gridView = view.findViewById(R.id.category_gridview);
        recyclerView = view.findViewById(R.id.menu_recycler);

        menuArray = new ArrayList<Category>();
//        getMenuArray();
        loadMenu();

        categoryAdapter = new CategoryAdapter(getContext(),menuArray);
        gridView.setAdapter(categoryAdapter);

        menuAdapter = new MenuAdapter(getContext(), menuArray);
        recyclerView.setAdapter(menuAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        if (homeItem!=null) {
            menuAdapter.openBottomSheet(homeItem,getView());
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                recyclerView.scrollToPosition(i);
                recyclerView.getChildAt(i).requestFocus();
            }
        });

        //Toolbar title (To change category)
        LinearLayout changeCategory = view.findViewById(R.id.changeCategory);
        changeCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_category,
                        (LinearLayout)view.findViewById(R.id.menu_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);


                //Handle Grid View
                gridView = bottomSheetView.findViewById(R.id.category_gridview_bottomsheet);
                gridView.setAdapter(categoryAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        recyclerView.scrollToPosition(i);
                        recyclerView.getChildAt(i).requestFocus();
                    }
                });
                ImageButton closeView = bottomSheetDialog.findViewById(R.id.itemdetail_closeBtn);
                closeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                //Show dialog
                bottomSheetDialog.show();
            }
        });

        bottomAppBar = getView().findViewById(R.id.bottomAppBar);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            bottomAppBar.setVisibility(View.INVISIBLE);
        }

        //Navigate to checkout
        LinearLayout totalPrice = view.findViewById(R.id.totalPrice);
        totalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("cartItems", cartItems);
                bundle.putLong("numberOfItems", number);
                Navigation.findNavController(view).navigate(R.id.action_menuFragment_to_checkOutFragment,bundle);
            }
        });

        LinearLayout addressInfo = view.findViewById(R.id.menu_ordermethod);
        addressInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Bottom sheet dialog
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(),R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_ordermethod,
                        (LinearLayout)view.findViewById(R.id.ordermethod_bottomsheet));
                bottomSheetDialog.setContentView(bottomSheetView);

                //Handle bottom sheet
                TextView addressTxt = bottomSheetView.findViewById(R.id.delivery_address);
                TextView storeAddressTxt = bottomSheetView.findViewById(R.id.checkout_address);
                ImageButton closeBtn = bottomSheetView.findViewById(R.id.othermethod_closeBtn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                    }
                });
                addressTxt.setText(address);
                storeAddressTxt.setText(storeAddress);

                //Delivery
                LinearLayout delivery = bottomSheetView.findViewById(R.id.ordermethod_delivery);
                delivery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeOrderMethod(bottomSheetDialog,0);
                    }
                });

                //Delivery edit
                Button editAddressBtn = bottomSheetView.findViewById(R.id.delivery_editBtn);
                editAddressBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();

                        Navigation.findNavController(getView()).navigate(R.id.action_menuFragment_to_addressFragment2);
                    }
                });

                //Take away
                LinearLayout takeaway = bottomSheetView.findViewById(R.id.ordermethod_takeaway);
                takeaway.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeOrderMethod(bottomSheetDialog,1);
                    }
                });

                //Take away edit
                Button editStoreBtn = bottomSheetView.findViewById(R.id.takeaway_editBtn);
                editStoreBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bottomSheetDialog.dismiss();
                        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.main_bottom_nav);
                        bottomNavigationView.setSelectedItemId(R.id.store);
                        Navigation.findNavController(getView()).navigate(R.id.storeFragment);
                    }
                });

                //Show dialog
                bottomSheetDialog.show();
            }
        });

        //Listen to data changed
        if (user != null) {
            Query query = db.collection("order")
                    .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("status", 0);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            db.collection("cartItems").whereEqualTo("cartID", doc.getId())
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Log.w(TAG, "listen:error", error);
                                        return;
                                    }
                                    System.out.println("Db updated");
                                    loadCart();
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

    }

    long total;
    long number;
    ArrayList<CartItem> cartItems;
    private void loadCart() {
        total = 0;
        number = 0;
        cartItems = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Query query = db.collection("order")
                    .whereEqualTo("userID", FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .whereEqualTo("status", 0);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            System.out.println("Doc: " + doc.getData());
                            db.collection("cartItems")
                                    .whereEqualTo("cartID", doc.getId()).get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().size() == 0) {
                                                    bottomAppBar.findViewById(R.id.totalPrice).setVisibility(View.GONE);
                                                }
                                                else {
                                                    bottomAppBar.findViewById(R.id.totalPrice).setVisibility(View.VISIBLE);
                                                    total = 0;
                                                    number = 0;
                                                    cartItems = new ArrayList<>();
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                        total += snapshot.getLong("price");
                                                        number += snapshot.getLong("quantity");
                                                        cartItems.add(snapshot.toObject(CartItem.class));
                                                        System.out.println("Cart items: " + cartItems);
                                                    }
                                                    TextView tvTotal = bottomAppBar.findViewById(R.id.totalItemsPrice);
                                                    tvTotal.setText(total + "đ");
                                                    TextView tvNumber = bottomAppBar.findViewById(R.id.numberOfItems);
                                                    tvNumber.setText("" + number);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putParcelableArrayList("cartItems", cartItems);
                                                    if (bundle == null) System.out.println("Bundle is null");
                                                }
                                            }
                                        }
                                    });
                        }
                    }
                }
            });
        }
    }

    ProgressDialog pd;
    private void loadMenu() {
        pd = new ProgressDialog(getActivity(),R.style.ProgressStyle);
        pd.setTitle("Đang tải menu...");
        pd.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getMenuArray();
            }
        }).start();
    }
    /*public void displayMenu(int i) {
            DocumentReference categoryDocument = db.collection("menu").document("category-"+i);
            categoryDocument.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                ArrayList<Item> itemsArray = new ArrayList<Item>();
                                categoryDocument.collection("items").get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                for (QueryDocumentSnapshot itemDocument : task.getResult()) {
                                                    Item menuItem = new Item(itemDocument.getId(), itemDocument.getString("name"),
                                                            itemDocument.getString("image"), itemDocument.getLong("price"), itemDocument.getString("description"));
                                                    itemsArray.add(menuItem);
                                                }
                                                if (document.exists()) {
                                                    Category category = new Category(document.getId(),
                                                            document.getString("name"),document.getString("image"),itemsArray);
                                                    menuArray.add(category);
                                                    categoryAdapter.notifyDataSetChanged();
                                                    menuAdapter.notifyDataSetChanged();
                                                    if (i == 1) displayMenu(2);
                                                    else if (i == 2) displayMenu(3);
                                                    else if (i == 3) displayMenu(4);
                                                    else if (i == 4) displayMenu(5);
                                                    else if (i == 5) displayMenu(6);
                                                    else if (i == 6) displayMenu(7);
                                                    else if (i == 7) displayMenu(8);
                                                    else if (i == 8) {
                                                        gridView.setVisibility(View.VISIBLE);
                                                        recyclerView.setVisibility(View.VISIBLE);
                                                        pd.dismiss();
                                                    }
                                                }
                                            }
                                        });

                                }
                            else {
                                System.out.println("Error getting documents."+ task.getException());
                            }
                        }
                    });
    }
*/
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater optionInflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menufrag_option_menu,menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                menuAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                menuAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }
    public void setOrderMethod(int method) {
        if (method==0 || method==1) {
            orderMethod = method;

            String methodIcon, text;
            if (orderMethod==0) {
                methodIcon="icon_delivery";
                text="Giao đến";
                addressText.setText(address);
            }
            else {
                methodIcon="icon_takeaway";
                text="Đến lấy tại";
                addressText.setText(storeAddress);
            }

            int drawableId = getView().getResources().getIdentifier(methodIcon, "drawable", getContext().getPackageName());
            methodImage.setImageResource(drawableId);
            methodText.setText(text);
        }
    }
    public void changeOrderMethod(BottomSheetDialog bottomSheetDialog, int method) {
        setOrderMethod(method);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("orderMethod",method);
        editor.apply();

        bottomSheetDialog.dismiss();
    }
    public void getMenuArray() {
        db.collection("menu")
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot categoryDocument : queryDocumentSnapshots) {
                    ArrayList<Item> itemsArray = new ArrayList<Item>();
                    db.collection("menu")
                            .document(categoryDocument.getId())
                            .collection("items")
                            .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for (QueryDocumentSnapshot itemDocument : queryDocumentSnapshots) {
                                Item menuItem = new Item(itemDocument.getId(),itemDocument.getString("name"),
                                        itemDocument.getString("image"),itemDocument.getLong("price"),itemDocument.getString("description"));
                                itemsArray.add(menuItem);

                            }
                            Category category = new Category(categoryDocument.getId(),
                                    categoryDocument.getString("name"),categoryDocument.getString("image"),itemsArray);
                            menuArray.add(category);
                            if (menuArray.size()==8) {
                                menuArray.sort(Comparator.comparing(a -> a.id));
                                categoryAdapter.notifyDataSetChanged();
                                menuAdapter.notifyDataSetChanged();
                                pd.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}